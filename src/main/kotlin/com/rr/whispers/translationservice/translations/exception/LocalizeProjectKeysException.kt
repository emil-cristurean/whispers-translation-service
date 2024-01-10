package com.rr.whispers.translationservice.translations.exception

class LocalizeProjectKeysException(val projectId: String, innerException: Throwable?) :
    Exception("Unknown exception when getting keys for localize project: $projectId", innerException) {
    companion object {
        const val DEFAULT_MESSAGE = "Unknown exception when getting localize project keys"
    }
}
