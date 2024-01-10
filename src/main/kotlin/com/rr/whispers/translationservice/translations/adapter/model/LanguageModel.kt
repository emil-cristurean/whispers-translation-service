package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LanguageModel(
    @JsonProperty("lang_id")
    val id: Int,
    @JsonProperty("lang_iso")
    val iso: String,
    @JsonProperty("lang_name")
    val name: String
)
