package com.rr.whispers.translationservice.translations.service.domain

import com.rr.whispers.translationservice.translations.WhispersTranslationProject
import org.apache.commons.lang3.StringUtils

class ProjectDomain private constructor(
    val id: String,
    val name: String,
    val whispersTranslationProject: WhispersTranslationProject
) {
    data class Builder(
        var id: String = StringUtils.EMPTY,
        var name: String = StringUtils.EMPTY,
        var whispersTranslationProject: WhispersTranslationProject = WhispersTranslationProject.UNKNOWN
    ) {
        fun id(id: String) = apply { this.id = id }

        fun name(name: String) = apply { this.name = name }

        fun whispersTranslationProject(whispersTranslationProject: WhispersTranslationProject) =
            apply { this.whispersTranslationProject = whispersTranslationProject }

        fun build() = ProjectDomain(id, name, whispersTranslationProject)
    }
}
