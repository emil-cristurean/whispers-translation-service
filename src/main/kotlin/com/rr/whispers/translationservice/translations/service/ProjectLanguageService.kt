package com.rr.whispers.translationservice.translations.service

import com.rr.whispers.translationservice.translations.WhispersTranslationProject
import com.rr.whispers.translationservice.translations.adapter.ILocalizeAdapter
import com.rr.whispers.translationservice.translations.exception.ProjectLanguageNotFoundException
import com.rr.whispers.translationservice.translations.exception.ProjectNotFoundException
import com.rr.whispers.translationservice.translations.service.domain.LanguageDomain
import com.rr.whispers.translationservice.translations.service.domain.ProjectDomain
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.util.Locale

@Service
fun interface IProjectLanguageService {
    fun getLanguage(project: ProjectDomain, locale: String): LanguageDomain
}

@Component
class ProjectLanguageService(
    private val localizeAdapter: ILocalizeAdapter
) : IProjectLanguageService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    companion object {
        val DEFAULT_LOCALE: Locale = Locale.UK
    }

    override fun getLanguage(project: ProjectDomain, locale: String): LanguageDomain {
        val targetLocale = StringUtils.parseLocale(locale) ?: DEFAULT_LOCALE

        logger.info("Processing project: ${project.id} and name: ${project.name}")
        val languages = localizeAdapter.getProjectLanguages(project.id)

        val targetLanguage = getLocalizeProjectLanguageName(project.whispersTranslationProject, targetLocale)
        val language = languages.find { s -> s.iso == targetLanguage }
            ?: throw ProjectLanguageNotFoundException(project.whispersTranslationProject, locale, null)

        return LanguageDomain.Builder().id(language.id).name(language.name).code(language.iso).build()
    }

    private fun getLocalizeProjectLanguageName(project: WhispersTranslationProject, locale: Locale): String {
        return when (project) {
            WhispersTranslationProject.ANDROID -> locale.language

            WhispersTranslationProject.IOS -> locale.language

            WhispersTranslationProject.LOCATION_SERVICE -> locale.toString()

            WhispersTranslationProject.MY_GARAGE_SERVICE -> locale.toString()

            WhispersTranslationProject.NOTIFICATION_SERVICE -> locale.toString()

            else -> throw ProjectNotFoundException(project, null)
        }
    }
}
