package com.rr.whispers.translationservice.translations

enum class LocalizeProjectName(val value: String) {
    ANDROID("Whispers Android"),
    BACKEND("Whispers Backend"),
    IOS("Whispers iOS"),
    UNKNOWN("UNKNOWN");

    companion object {
        infix fun get(value: String): LocalizeProjectName {
            val type = entries.firstOrNull { it.value == value }
            return type ?: UNKNOWN
        }
    }
}
