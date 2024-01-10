package com.rr.whispers.translationservice.translations.service.domain

import org.apache.commons.lang3.StringUtils

class LanguageDomain private constructor(
    val id: Int,
    val name: String,
    val code: String
) {
    data class Builder(
        var id: Int = 0,
        var name: String = StringUtils.EMPTY,
        var code: String = StringUtils.EMPTY
    ) {
        fun id(id: Int) = apply { this.id = id }

        fun name(name: String) = apply { this.name = name }

        fun code(code: String) = apply { this.code = code }

        fun build() = LanguageDomain(id, name, code)
    }
}
