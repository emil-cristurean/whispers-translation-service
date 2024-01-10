package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectKeyResponseModel(
    @JsonProperty("project_id")
    val projectId: String,
    @JsonProperty("keys")
    val keys: List<KeyModel>
)
