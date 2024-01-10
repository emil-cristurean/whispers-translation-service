package com.rr.whispers.translationservice.translations.exception

import com.rr.whispers.translationservice.translations.WhispersTranslationProject

class ProjectLanguageNotFoundException(
    project: WhispersTranslationProject,
    language: String,
    innerException: Throwable?
) :
    Exception("Fail to get localize language: $language for project whispers project: $project", innerException) {
    companion object {
        const val DEFAULT_MESSAGE = "Failed to find localize project language"
    }
}
