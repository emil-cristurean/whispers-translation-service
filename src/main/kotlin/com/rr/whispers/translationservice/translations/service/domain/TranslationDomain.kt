package com.rr.whispers.translationservice.translations.service.domain

import org.apache.commons.lang3.StringUtils

class TranslationDomain private constructor(
    val key: String,
    val value: String
) {
    data class Builder(
        var key: String = StringUtils.EMPTY,
        var value: String = StringUtils.EMPTY
    ) {
        fun key(key: String) = apply { this.key = key }

        fun value(value: String) = apply { this.value = value }

        fun build() = TranslationDomain(key, value)
    }
}
