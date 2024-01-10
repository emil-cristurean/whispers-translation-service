package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class KeyNameModel(
    @JsonProperty("ios")
    val ios: String,
    @JsonProperty("android")
    val android: String,
    @JsonProperty("web")
    val web: String,
    @JsonProperty("other")
    val other: String
)
