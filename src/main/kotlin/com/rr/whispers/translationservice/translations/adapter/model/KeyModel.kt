package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class KeyModel(
    @JsonProperty("key_id")
    val id: Int,
    @JsonProperty("key_name")
    val name: KeyNameModel,
    @JsonProperty("tags")
    val tags: List<String>,
    @JsonProperty("translations")
    val translations: List<TranslationModel>,

)
