package com.rr.whispers.translationservice.translations.resource.v1

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.rr.whispers.translationservice.TranslationServiceApplication
import com.rr.whispers.translationservice.translations.WhispersTranslationProject
import com.rr.whispers.translationservice.translations.exception.LocalizeProjectKeysException
import com.rr.whispers.translationservice.translations.exception.LocalizeProjectLanguagesException
import com.rr.whispers.translationservice.translations.exception.LocalizeProjectsException
import com.rr.whispers.translationservice.translations.exception.ProjectLanguageNotFoundException
import com.rr.whispers.translationservice.translations.exception.ProjectNotFoundException
import com.rr.whispers.translationservice.translations.resource.v1.model.TranslationModel
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.stream.Stream

@SpringBootTest(classes = [TranslationServiceApplication::class])
@AutoConfigureMockMvc
@Transactional
@WireMockTest(httpPort = 8089)
class TranslationResourceIT {
    @Autowired
    private lateinit var mvc: MockMvc

    companion object {
        const val TRANSLATIONS_URL = "/api/app/v1/translations"
        const val LOCALIZE_PROJECTS_URL = "/api2/projects"
        const val KEYS_QUERY_PARAMS_URL = "limit=1000&page=1&disable_references=0&include_comments=0&" +
            "include_screenshots=0&include_translations=1&filter_untranslated=1&filter_unverified=1"

        const val X_LOCALE_HEADER_NAME = "x-locale"
        const val X_LOCALE_HEADER_VALUE_EN_GB = "en-GB"
        const val X_LOCALE_HEADER_VALUE_KO_KR = "ko-KR"
        const val X_LOCALE_HEADER_VALUE_JA_JP = "ja-JP"
        const val X_LOCALE_HEADER_VALUE_RO_RO = "ro-RO"

        const val X_USER_AGENT_HEADER_NAME = "x-user-agent"
        const val X_USER_AGENT_HEADER_VALUE = "Rolls-royce"

        @JvmStatic
        fun getTranslationBackedData(): Stream<Arguments> = Stream.of(
            Arguments.of(WhispersTranslationProject.LOCATION_SERVICE, X_LOCALE_HEADER_VALUE_EN_GB, 12),
            Arguments.of(WhispersTranslationProject.LOCATION_SERVICE, X_LOCALE_HEADER_VALUE_KO_KR, 12),
            Arguments.of(WhispersTranslationProject.LOCATION_SERVICE, X_LOCALE_HEADER_VALUE_JA_JP, 12),
            Arguments.of(WhispersTranslationProject.MY_GARAGE_SERVICE, X_LOCALE_HEADER_VALUE_EN_GB, 53),
            Arguments.of(WhispersTranslationProject.MY_GARAGE_SERVICE, X_LOCALE_HEADER_VALUE_KO_KR, 53),
            Arguments.of(WhispersTranslationProject.MY_GARAGE_SERVICE, X_LOCALE_HEADER_VALUE_JA_JP, 53),
            Arguments.of(WhispersTranslationProject.NOTIFICATION_SERVICE, X_LOCALE_HEADER_VALUE_EN_GB, 142),
            Arguments.of(WhispersTranslationProject.NOTIFICATION_SERVICE, X_LOCALE_HEADER_VALUE_KO_KR, 142),
            Arguments.of(WhispersTranslationProject.NOTIFICATION_SERVICE, X_LOCALE_HEADER_VALUE_JA_JP, 142),
        )
    }

    private fun stubGetResponse(url: String, responseBody: String, responseStatus: Int = HttpStatus.OK.value()) {
        WireMock.stubFor(
            WireMock.get(url)
                .willReturn(
                    WireMock.aResponse()
                        .withStatus(responseStatus)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )
    }

    private val localizeProjectsNoProjectsFileName = "mocks/localize/projects_noProjects.json"
    private val localizeProjectsNoProjectsResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectsNoProjectsFileName)?.readText()

    private val localizeProjectsFileName = "mocks/localize/projects.json"
    private val localizeProjectsResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectsFileName)?.readText()

    private val localizeProjectAndroidLanguagesFileName = "mocks/localize/project_android_languages.json"
    private val localizeProjectAndroidLanguagesResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectAndroidLanguagesFileName)?.readText()

    private val localizeProjectIOSLanguagesFileName = "mocks/localize/project_ios_languages.json"
    private val localizeProjectIOSLanguagesResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectIOSLanguagesFileName)?.readText()

    private val localizeProjectBackendLanguagesFileName = "mocks/localize/project_backend_languages.json"
    private val localizeProjectBackendLanguagesResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectBackendLanguagesFileName)?.readText()

    private val localizeProjectAndroidKeysFileName = "mocks/localize/project_android_keys.json"
    private val localizeProjectAndroidKeysResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectAndroidKeysFileName)?.readText()

    private val localizeProjectIOSKeysFileName = "mocks/localize/project_ios_keys.json"
    private val localizeProjectIOSKeysResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectIOSKeysFileName)?.readText()

    private val localizeProjectBackendKeysFileName = "mocks/localize/project_backend_keys.json"
    private val localizeProjectBackendKeysResponse: String? =
        this::class.java.classLoader.getResource(localizeProjectBackendKeysFileName)?.readText()

    @Test
    fun `translation resource - file - projects with no projects file is loaded`() {
        assertThat(localizeProjectsNoProjectsResponse).isNotNull
    }

    @Test
    fun `translation resource - file - projects file is loaded`() {
        assertThat(localizeProjectsResponse).isNotNull
    }

    @Test
    fun `translation resource - file - project android languages is loaded`() {
        assertThat(localizeProjectAndroidLanguagesResponse).isNotNull
    }

    @Test
    fun `translation resource - file - project IOS languages is loaded`() {
        assertThat(localizeProjectIOSLanguagesResponse).isNotNull
    }

    @Test
    fun `translation resource - file - project backend languages is loaded`() {
        assertThat(localizeProjectBackendLanguagesResponse).isNotNull
    }

    @Test
    fun `translation resource - file - project android keys is loaded`() {
        assertThat(localizeProjectAndroidKeysResponse).isNotNull
    }

    @Test
    fun `translation resource - file - project IOS keys is loaded`() {
        assertThat(localizeProjectIOSKeysResponse).isNotNull
    }

    @Test
    fun `translation resource - file - project backend keys is loaded`() {
        assertThat(localizeProjectBackendKeysResponse).isNotNull
    }

    @Test
    fun `translation resource - unknown project`() {
        // arrange
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.UNKNOWN}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_EN_GB)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isEqualTo(ProjectNotFoundException.DEFAULT_MESSAGE)
    }

    @Test
    fun `translation resource - localize error when getting projects`() {
        // arrange
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_EN_GB)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isEqualTo(LocalizeProjectsException.DEFAULT_MESSAGE)
    }

    @Test
    fun `translation resource - project not found`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsNoProjectsResponse!!)
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_EN_GB)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isEqualTo(ProjectNotFoundException.DEFAULT_MESSAGE)
    }

    @Test
    fun `translation resource - localize error when getting project languages`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_RO_RO)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isEqualTo(LocalizeProjectLanguagesException.DEFAULT_MESSAGE)
    }

    @Test
    fun `translation resource - project language not found`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/languages?limit=1000&page=1",
            localizeProjectAndroidLanguagesResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_RO_RO)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isNotFound).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isEqualTo(ProjectLanguageNotFoundException.DEFAULT_MESSAGE)
    }

    @Test
    fun `translation resource - localize error when getting project keys`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/languages?limit=1000&page=1",
            localizeProjectAndroidLanguagesResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_EN_GB)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isInternalServerError).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isEqualTo(LocalizeProjectKeysException.DEFAULT_MESSAGE)
    }

    @Test
    fun `translation resource - get translations Android EN-GB`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/languages?limit=1000&page=1",
            localizeProjectAndroidLanguagesResponse!!
        )
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectAndroidKeysResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_EN_GB)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(1000)
    }

    @Test
    fun `translation resource - get translations Android KO_KR`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/languages?limit=1000&page=1",
            localizeProjectAndroidLanguagesResponse!!
        )
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectAndroidKeysResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_KO_KR)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(1000)
    }

    @Test
    fun `translation resource - get translations Android JA_JP`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/languages?limit=1000&page=1",
            localizeProjectAndroidLanguagesResponse!!
        )
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/android/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectAndroidKeysResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.ANDROID}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_JA_JP)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(1000)
    }

    @Test
    fun `translation resource - get translations IOS EN-GB`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse("$LOCALIZE_PROJECTS_URL/ios/languages?limit=1000&page=1", localizeProjectIOSLanguagesResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/ios/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectIOSKeysResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.IOS}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_EN_GB)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(1000)
    }

    @Test
    fun `translation resource - get translations IOS KO_KR`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse("$LOCALIZE_PROJECTS_URL/ios/languages?limit=1000&page=1", localizeProjectIOSLanguagesResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/ios/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectIOSKeysResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.IOS}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_KO_KR)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(1000)
    }

    @Test
    fun `translation resource - get translations IOS JA_JP`() {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse("$LOCALIZE_PROJECTS_URL/ios/languages?limit=1000&page=1", localizeProjectIOSLanguagesResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/ios/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectIOSKeysResponse!!
        )
        val url = "${TRANSLATIONS_URL}?project=${WhispersTranslationProject.IOS}"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, X_LOCALE_HEADER_VALUE_JA_JP)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(1000)
    }

    @MethodSource("getTranslationBackedData")
    @ParameterizedTest(name = "translation resource - get translations Backend - {0} {1}")
    fun translationResourceGetTranslationsBackend(project: WhispersTranslationProject, locale: String, numberOfTranslations: Int) {
        // arrange
        stubGetResponse(LOCALIZE_PROJECTS_URL, localizeProjectsResponse!!)
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/backend/languages?limit=1000&page=1",
            localizeProjectBackendLanguagesResponse!!
        )
        stubGetResponse(
            "$LOCALIZE_PROJECTS_URL/backend/keys?$KEYS_QUERY_PARAMS_URL",
            localizeProjectBackendKeysResponse!!
        )
        val url = "$TRANSLATIONS_URL?project=$project"

        // act
        val result = mvc.perform(
            MockMvcRequestBuilders.get(url)
                .header(X_LOCALE_HEADER_NAME, locale)
                .header(X_USER_AGENT_HEADER_NAME, X_USER_AGENT_HEADER_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk).andReturn()

        // assert
        assertThat(result).isNotNull
        assertThat(result.response).isNotNull
        assertThat(result.response.errorMessage).isNull()

        val translations: List<TranslationModel> = getObjectMapper().readValue(result.response.contentAsString)
        assertThat(translations).isNotNull.isNotEmpty.hasSize(numberOfTranslations)
    }

    private fun getObjectMapper(): ObjectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )
}
