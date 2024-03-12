package com.rr.whispers.translationservice.translations.resource.v1

import com.rr.whispers.translationservice.translations.WhispersTranslationProject
import com.rr.whispers.translationservice.translations.resource.v1.model.TranslationModel
import com.rr.whispers.translationservice.translations.service.ITranslationService
import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/app/v1/translations")
@Tag(name = "translations", description = "the translations API")
class TranslationResource(
    private val translationService: ITranslationService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(
        summary = "Searches for charging stations by a query",
        description = "Searches for charging stations by a query"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = 200.toString(),
                description = "successful operation",
                content = [
                    Content(
                        array = ArraySchema(schema = Schema(implementation = TranslationModel::class))
                    )
                ]
            )
        ]
    )
    @GetMapping("", produces = ["application/json"])
    @Timed(
        value = "translationService.api.get",
        description = "Time taken to return translations"
    )
    fun get(
        @RequestHeader("x-locale", required = true) locale: String,
        @RequestHeader("x-user-agent", required = true) agent: String,
        @RequestHeader("x-timezone", required = false) targetTimezone: Long?,
        @RequestHeader("x-date-time", required = false) targetDateTime: Long?,
        @Parameter project: WhispersTranslationProject
    ): List<TranslationModel> {
        logger.info("Searching for translations for locale: $locale and project: $project")
        val translations = translationService.getTranslations(project, locale)
        return translations.map { s -> TranslationModel.Builder().key(s.key).value(s.value).build() }
    }
}
