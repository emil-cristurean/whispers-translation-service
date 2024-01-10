package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectModel(
    @JsonProperty("project_id")
    val id: String,
    @JsonProperty("project_type")
    val type: String,
    @JsonProperty("name")
    val name: String
)
