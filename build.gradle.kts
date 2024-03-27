import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.owasp.dependencycheck.reporting.ReportGenerator.Format


plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.manager)

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.spring)

    alias(libs.plugins.dependency.check)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.versions)
    id("jacoco")

    alias(libs.plugins.reckon)
}

group = "com.rr.whispers"
version = ""

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

repositories {
    mavenCentral()
    repositories {
        maven {
            setUrl("https://odmartifactory.jfrog.io/odmartifactory/rr-whispers-mvn/")
            credentials {
                username = "${properties["odmArtifactoryUser"]}"
                password = "${properties["odmArtifactoryPassword"]}"
            }
            name = "odm_artifactory"
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("com.rr.whispers:dependencies-bom:2.0.20")
    }
    applyMavenExclusions(false)
}

dependencies {
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.cloud.openfeign)

    implementation(libs.bundles.whsprs)

    implementation(libs.bundles.jackson)
    implementation(libs.bundles.springdoc)
    implementation(libs.feign.okhttp)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.bundles.whsprs.test)
    testImplementation(libs.bundles.testcontainers)

    testImplementation(libs.wiremock)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.register("resolveDependencies") {
    doLast {
        configurations.runtimeClasspath.get().resolve()
        configurations.testRuntimeClasspath.get().resolve()
    }
}

tasks.test {
    useJUnitPlatform()

    finalizedBy(tasks.jacocoTestReport)
}

sonarqube {
    properties {
        property("sonar.dependencyCheck.htmlReportPath", "build/reports/dependency-check-report.html")
        property("sonar.dependencyCheck.jsonReportPath", "build/reports/dependency-check-report.json")

        property("sonar.dependencyCheck.summarize", true)
        property("sonar.exclusions", "**/generated/**, **/generated-sources/**, **/src-gen/**")

        property("sonar.java.coveragePlugin", "jacoco")
    }
}

dependencyCheck {
    autoUpdate = true
    format = Format.ALL.toString()
    analyzers(
        closureOf<org.owasp.dependencycheck.gradle.extension.AnalyzerExtension> {
            assemblyEnabled = false
        }
    )
    nvd(
        closureOf<org.owasp.dependencycheck.gradle.extension.NvdExtension> {
            apiKey = "2cc37c97-2133-43e5-be00-dcc3b203b0f4"
            delay = 1600
        }
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = libs.versions.java.get()
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    version.set(libs.versions.ktlint.extension.get())
    debug.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    reporters {
        reporter(ReporterType.JSON)
        reporter(ReporterType.HTML)
    }
}

fun String.isNonStable(): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(this)
    return isStable.not()
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
    rejectVersionIf {
        candidate.version.isNonStable()
    }

    checkForGradleUpdate = true
    outputFormatter = "json,xml,html"
    outputDir = "build/dependencyUpdates"
    reportfileName = "report"
}

extensions.configure<org.ajoberstar.reckon.gradle.ReckonExtension> {
    setDefaultInferredScope("patch")
    stages("rc", "final")
    setScopeCalc(calcScopeFromProp().or(calcScopeFromCommitMessages()))
    setStageCalc(calcStageFromProp())
}
