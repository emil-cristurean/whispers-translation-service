package com.rr.whispers.translationservice.translations.adapter.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProjectLanguageResponseModel(
    @JsonProperty("project_id")
    val projectId: String,
    @JsonProperty("languages")
    val languages: List<LanguageModel>
)
