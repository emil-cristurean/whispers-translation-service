package com.rr.whispers.translationservice.translations.service

import com.rr.whispers.translationservice.translations.LocalizeProjectName
import com.rr.whispers.translationservice.translations.LocalizeTagName
import com.rr.whispers.translationservice.translations.WhispersTranslationProject
import com.rr.whispers.translationservice.translations.adapter.ILocalizeAdapter
import com.rr.whispers.translationservice.translations.exception.ProjectNotFoundException
import com.rr.whispers.translationservice.translations.service.domain.ProjectDomain
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
interface IProjectService {
    fun getProject(project: WhispersTranslationProject): ProjectDomain

    fun getProjectTags(project: WhispersTranslationProject): List<String>
}

@Component
class ProjectService(
    private val localizeAdapter: ILocalizeAdapter
) : IProjectService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getProject(project: WhispersTranslationProject): ProjectDomain {
        val projectName = getLocalizeProjectName(project)

        val localizeProjects = localizeAdapter.getProjects()
        logger.info("Found: ${localizeProjects.size} projects")

        val localizeProject = localizeProjects.find { s -> s.name == projectName.value }
            ?: throw ProjectNotFoundException(project, null)
        return ProjectDomain.Builder().id(localizeProject.id).name(localizeProject.name)
            .whispersTranslationProject(project).build()
    }

    override fun getProjectTags(project: WhispersTranslationProject): List<String> {
        return when (project) {
            WhispersTranslationProject.ANDROID -> emptyList()

            WhispersTranslationProject.IOS -> emptyList()

            WhispersTranslationProject.LOCATION_SERVICE -> listOf(LocalizeTagName.CSS_RATING.value)

            WhispersTranslationProject.MY_GARAGE_SERVICE -> listOf(LocalizeTagName.MY_GARAGE_SERVICE.value)

            WhispersTranslationProject.NOTIFICATION_SERVICE -> listOf(LocalizeTagName.NOTIFICATION_SERVICE.value)

            WhispersTranslationProject.UNKNOWN -> emptyList()
        }
    }

    private fun getLocalizeProjectName(project: WhispersTranslationProject): LocalizeProjectName {
        return when (project) {
            WhispersTranslationProject.ANDROID -> LocalizeProjectName.ANDROID

            WhispersTranslationProject.IOS -> LocalizeProjectName.IOS

            WhispersTranslationProject.LOCATION_SERVICE -> LocalizeProjectName.BACKEND

            WhispersTranslationProject.MY_GARAGE_SERVICE -> LocalizeProjectName.BACKEND

            WhispersTranslationProject.NOTIFICATION_SERVICE -> LocalizeProjectName.BACKEND

            else -> throw ProjectNotFoundException(project, null)
        }
    }
}
