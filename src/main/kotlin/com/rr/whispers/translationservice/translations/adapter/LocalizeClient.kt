package com.rr.whispers.translationservice.translations.adapter

import com.rr.whispers.translationservice.translations.adapter.model.ProjectKeyResponseModel
import com.rr.whispers.translationservice.translations.adapter.model.ProjectLanguageResponseModel
import com.rr.whispers.translationservice.translations.adapter.model.ProjectResponseModel
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "localize-adapter",
    configuration = [LocalizeClientConfiguration::class],
    url = "\${whsprs.third-party-systems.localize.url}"
)
interface LocalizeClient {
    @GetMapping(value = ["/api2/projects"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getProjects(): ResponseEntity<ProjectResponseModel>

    @GetMapping(value = ["/api2/projects/{projectId}/languages"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getProjectLanguages(
        @PathVariable("projectId") projectId: String,
        @RequestParam("limit") limit: Int,
        @RequestParam("page") page: Int
    ): ResponseEntity<ProjectLanguageResponseModel>

    @GetMapping(value = ["/api2/projects/{projectId}/keys"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun getProjectKeys(
        @PathVariable("projectId") projectId: String,
        @RequestParam("limit") limit: Int,
        @RequestParam("page") page: Int,
        @RequestParam("disable_references") disableReferences: Int = 0,
        @RequestParam("include_comments") includeComments: Int = 0,
        @RequestParam("include_screenshots") includeScreenshots: Int = 0,
        @RequestParam("include_translations") includeTranslations: Int = 1,
        @RequestParam("filter_untranslated") filterUntranslated: Int = 1,
        @RequestParam("filter_unverified") filterUnverified: Int = 1
    ): ResponseEntity<ProjectKeyResponseModel>
}
