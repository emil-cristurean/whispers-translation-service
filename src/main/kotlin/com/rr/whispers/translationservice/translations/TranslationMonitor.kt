package com.rr.whispers.translationservice.translations

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TranslationMonitor @Autowired constructor(
    private val meterRegistry: MeterRegistry
) {
    private val localizeProjectsExceptionCounter = Counter.builder(LOCALIZE_PROJECTS_EXCEPTION_COUNTER_NAME)
    private val localizeProjectLanguagesExceptionCounter = Counter.builder(LOCALIZE_PROJECT_LANGUAGES_EXCEPTION_COUNTER_NAME)
    private val localizeProjectKeysExceptionCounter = Counter.builder(LOCALIZE_PROJECT_KEYS_EXCEPTION_COUNTER_NAME)

    companion object {
        const val LOCALIZE_PROJECTS_EXCEPTION_COUNTER_NAME = "whsprs_translations_localize_projects_exception"
        const val LOCALIZE_PROJECT_LANGUAGES_EXCEPTION_COUNTER_NAME = "whsprs_translations_localize_project_languages_exception"
        const val LOCALIZE_PROJECT_KEYS_EXCEPTION_COUNTER_NAME = "whsprs_translations_localize_project_keys_exception"
    }

    fun incrementLocalizeProjectsExceptionCounter() {
        localizeProjectsExceptionCounter
            .register(meterRegistry)
            .increment()
    }

    fun incrementLocalizeProjectLanguagesExceptionCounter(projectId: String) {
        localizeProjectLanguagesExceptionCounter
            .tag("projectId", projectId)
            .register(meterRegistry)
            .increment()
    }

    fun incrementLocalizeProjectKeysExceptionCounter(projectId: String) {
        localizeProjectKeysExceptionCounter
            .tag("projectId", projectId)
            .register(meterRegistry)
            .increment()
    }
}
