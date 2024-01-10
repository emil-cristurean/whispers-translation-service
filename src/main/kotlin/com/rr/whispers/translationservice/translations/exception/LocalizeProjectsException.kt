package com.rr.whispers.translationservice.translations.exception

class LocalizeProjectsException(innerException: Throwable?) :
    Exception(DEFAULT_MESSAGE, innerException) {
    companion object {
        const val DEFAULT_MESSAGE = "Unknown exception when getting localize projects"
    }
}
