rootProject.name = "whispers-translation-service"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("idea-ext", "1.1.7")
            version("java", JavaVersion.VERSION_17.toString())
            version("kotlin", "2.0.0-Beta2")
            version("ktlint-extension", "1.1.0")
            version("jacoco", "0.8.10")
            version("spring-boot", "3.2.1")
            version("jackson", "2.15.3")
            version("cloud-feign", "4.1.0")
            version("feign", "13.1")
            version("testcontainers", "1.19.3")
            version("springdoc", "2.3.0")

            plugin("idea-ext", "org.jetbrains.gradle.plugin.idea-ext").versionRef("idea-ext")
            plugin("spring-boot", "org.springframework.boot").versionRef("spring-boot")
            plugin("spring-dependency-manager", "io.spring.dependency-management").version("1.1.4")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-plugin-spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")
            plugin("dependency-check", "org.owasp.dependencycheck").version("9.0.4")
            plugin("sonarqube", "org.sonarqube").version("4.4.1.3373")
            plugin("ktlint", "org.jlleitschuh.gradle.ktlint").version("12.0.3")
            plugin("versions", "com.github.ben-manes.versions").version("0.50.0")

            library("spring-boot-starter", "org.springframework.boot", "spring-boot-starter").versionRef("spring-boot")
            library("spring-cloud-openfeign", "org.springframework.cloud", "spring-cloud-starter-openfeign").versionRef(
                "cloud-feign"
            )

            library("feign-okhttp", "io.github.openfeign", "feign-okhttp").versionRef("feign")

            library("whsprs-language", "com.rr.whispers", "commons-language").withoutVersion()
            library("whsprs-observability", "com.rr.whispers", "commons-observability").withoutVersion()
            library("whsprs-web", "com.rr.whispers", "commons-web").withoutVersion()

            bundle(
                "whsprs",
                listOf(
                    "whsprs-language",
                    "whsprs-observability",
                    "whsprs-web"
                )
            )

            library(
                "jackson-module-kotlin",
                "com.fasterxml.jackson.module",
                "jackson-module-kotlin"
            ).versionRef("jackson")
            library(
                "jackson-databind",
                "com.fasterxml.jackson.core",
                "jackson-databind"
            ).versionRef("jackson")
            bundle("jackson", listOf("jackson-module-kotlin", "jackson-databind"))

            library("springdoc-ui", "org.springdoc", "springdoc-openapi-starter-webmvc-ui").versionRef("springdoc")
            library("springdoc-api", "org.springdoc", "springdoc-openapi-starter-webmvc-api").versionRef("springdoc")
            bundle("springdoc", listOf("springdoc-ui", "springdoc-api"))

            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef("kotlin")
            library("kotlin-stdlib", "org.jetbrains.kotlin", "kotlin-stdlib").versionRef("kotlin")
            bundle("kotlin", listOf("kotlin-reflect", "kotlin-stdlib"))

            library("spring-boot-starter-test", "org.springframework.boot", "spring-boot-starter-test").withoutVersion()
            library("mockito-kotlin", "org.mockito.kotlin", "mockito-kotlin").version("5.2.0")
            library("mockito-core", "org.mockito", "mockito-core").version("5.7.0")
            bundle("mockito", listOf("mockito-kotlin", "mockito-core"))

            library("whsprs-web-test", "com.rr.whispers", "commons-web-test").withoutVersion()
            bundle("whsprs-test", listOf("whsprs-web-test"))

            library("junit-jupiter", "org.testcontainers", "junit-jupiter").versionRef("testcontainers")
            library("testcontainers", "org.testcontainers", "testcontainers").versionRef("testcontainers")
            bundle("testcontainers", listOf("junit-jupiter", "testcontainers"))

            library("wiremock", "org.wiremock", "wiremock").version("3.3.0")
            library("junit-platform-launcher", "org.junit.platform", "junit-platform-launcher").withoutVersion()
        }
    }
}
