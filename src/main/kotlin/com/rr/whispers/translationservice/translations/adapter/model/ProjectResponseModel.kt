package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectResponseModel(
    @JsonProperty("projects")
    val projects: List<ProjectModel>
)
