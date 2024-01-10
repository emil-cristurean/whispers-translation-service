package com.rr.whispers.translationservice.translations.exception

import com.rr.whispers.translationservice.translations.WhispersTranslationProject

class ProjectNotFoundException(project: WhispersTranslationProject, innerException: Throwable?) :
    Exception("Fail to get localize project whispers project: $project", innerException) {
    companion object {
        const val DEFAULT_MESSAGE = "Failed to find localize project"
    }
}
