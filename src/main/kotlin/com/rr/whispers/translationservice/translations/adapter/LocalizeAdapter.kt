package com.rr.whispers.translationservice.translations.adapter

import com.rr.whispers.translationservice.translations.adapter.model.KeyModel
import com.rr.whispers.translationservice.translations.adapter.model.LanguageModel
import com.rr.whispers.translationservice.translations.adapter.model.ProjectModel
import com.rr.whispers.translationservice.translations.exception.LocalizeProjectKeysException
import com.rr.whispers.translationservice.translations.exception.LocalizeProjectLanguagesException
import com.rr.whispers.translationservice.translations.exception.LocalizeProjectsException
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
interface ILocalizeAdapter {
    fun getProjects(): List<ProjectModel>

    fun getProjectLanguages(projectId: String): List<LanguageModel>

    fun getProjectKeys(projectId: String): List<KeyModel>
}

@Component
class LocalizeAdapter @Autowired constructor(
    private val localizeClient: LocalizeClient
) : ILocalizeAdapter {
    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        const val PAGE_NUMBER = 1
        const val PAGE_LIMIT = 1000
    }

    override fun getProjects(): List<ProjectModel> {
        try {
            logger.info("Getting projects ...")
            return localizeClient.getProjects().body?.projects ?: emptyList()
        } catch (exception: FeignException) {
            throw LocalizeProjectsException(exception)
        }
    }

    override fun getProjectLanguages(projectId: String): List<LanguageModel> {
        try {
            logger.info("Getting languages for project: $projectId ...")
            return localizeClient.getProjectLanguages(projectId, PAGE_LIMIT, PAGE_NUMBER).body?.languages ?: emptyList()
        } catch (exception: FeignException) {
            throw LocalizeProjectLanguagesException(projectId, exception)
        }
    }

    override fun getProjectKeys(projectId: String): List<KeyModel> {
        try {
            logger.info("Getting keys for project: $projectId ...")
            return localizeClient.getProjectKeys(projectId, PAGE_LIMIT, PAGE_NUMBER).body?.keys ?: emptyList()
        } catch (exception: FeignException) {
            throw LocalizeProjectKeysException(projectId, exception)
        }
    }
}
