package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class TranslationModel(
    @JsonProperty("translation_id")
    val id: String,
    @JsonProperty("key_id")
    val keyId: Int,
    @JsonProperty("translation")
    val translation: String,
    @JsonProperty("language_iso")
    val languageIso: String,
)
