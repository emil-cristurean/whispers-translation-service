package com.rr.whispers.translationservice.translations.exception

class LocalizeProjectLanguagesException(val projectId: String, innerException: Throwable?) :
    Exception("Unknown exception when getting languages for localize project: $projectId", innerException) {
    companion object {
        const val DEFAULT_MESSAGE = "Unknown exception when getting localize project languages"
    }
}
