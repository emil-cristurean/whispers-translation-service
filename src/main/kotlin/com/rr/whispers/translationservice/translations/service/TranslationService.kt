package com.rr.whispers.translationservice.translations.service

import com.rr.whispers.translationservice.translations.WhispersTranslationProject
import com.rr.whispers.translationservice.translations.adapter.ILocalizeAdapter
import com.rr.whispers.translationservice.translations.adapter.model.KeyNameModel
import com.rr.whispers.translationservice.translations.service.domain.TranslationDomain
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
fun interface ITranslationService {
    fun getTranslations(project: WhispersTranslationProject, locale: String): List<TranslationDomain>
}

@Component
class TranslationService(
    private val localizeAdapter: ILocalizeAdapter,
    private val projectService: IProjectService,
    private val projectLanguageService: IProjectLanguageService
) : ITranslationService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun getTranslations(project: WhispersTranslationProject, locale: String): List<TranslationDomain> {
        logger.info("Getting translations for project: $project")

        val projectDomain = projectService.getProject(project)

        val language = projectLanguageService.getLanguage(projectDomain, locale)

        val keys = localizeAdapter.getProjectKeys(projectDomain.id)
        logger.info("Found ${keys.size} keys")

        val translations = mutableListOf<TranslationDomain>()
        keys.forEach { key ->
            // filter for tag
            val projectTags = projectService.getProjectTags(project)
            if (projectTags.isNotEmpty()) {
                if (!key.tags.containsAll(projectTags)) {
                    return@forEach
                }
            }

            // filter for language
            val translation = key.translations.find { t -> t.languageIso == language.code }
            if (translation != null) {
                val translationKey = getKeyValue(project, key.name)
                translations.add(TranslationDomain.Builder().key(translationKey).value(translation.translation).build())
            }
        }

        return translations
    }

    private fun getKeyValue(project: WhispersTranslationProject, key: KeyNameModel): String {
        return when (project) {
            WhispersTranslationProject.ANDROID -> key.android

            WhispersTranslationProject.IOS -> key.android

            WhispersTranslationProject.LOCATION_SERVICE -> key.other

            WhispersTranslationProject.MY_GARAGE_SERVICE -> key.other

            WhispersTranslationProject.NOTIFICATION_SERVICE -> key.other

            WhispersTranslationProject.UNKNOWN -> key.other
        }
    }
}
