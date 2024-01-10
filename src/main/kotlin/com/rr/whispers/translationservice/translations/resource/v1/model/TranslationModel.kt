package com.rr.whispers.translationservice.translations.resource.v1.model

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.commons.lang3.StringUtils

data class TranslationModel(
    @JsonProperty("key")
    @Schema(description = "Translation key")
    val key: String,

    @JsonProperty("value")
    @Schema(description = "Translation value")
    val value: String
) {
    data class Builder(
        var key: String = StringUtils.EMPTY,
        var value: String = StringUtils.EMPTY,
    ) {
        fun key(key: String) = apply { this.key = key }

        fun value(value: String) = apply { this.value = value }

        fun build() = TranslationModel(key, value)
    }
}
