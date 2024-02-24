FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /gradle/src

# 1. download gradle wrapper
COPY gradlew /gradle/src
COPY gradle/wrapper/* /gradle/src/gradle/wrapper/
COPY gradle/libs.versions.toml /gradle/src/gradle/libs.versions.toml
ENV GRADLE_USER_HOME=/gradle
RUN chmod +x gradlew
RUN ./gradlew --version

# 2. download all dependencies
COPY build.gradle* settings.gradle* gradle.properties* /gradle/src/
ARG ODM_ARTIFACTORY_USER
ARG ODM_ARTIFACTORY_PASSWORD
RUN echo "odmArtifactoryUser=$ODM_ARTIFACTORY_USER\nodmArtifactoryPassword=$ODM_ARTIFACTORY_PASSWORD" >> /gradle/gradle.properties
RUN ./gradlew --no-daemon resolveDependencies

# 3. gradle kt lint check dependencies
COPY src /gradle/src/src
COPY .editorconfig /gradle/src/.editorconfig
RUN ./gradlew -no-daemon clean ktlintCheck

# 4. full gradle build
RUN ./gradlew -no-daemon build -x test

FROM eclipse-temurin:17-jre-focal

ARG GIT_LAST_COMMIT_SHA
ARG CLUSTER_SERVICE_CURRENT_VERSION
ENV Internal__Info__CommitSha=${GIT_LAST_COMMIT_SHA}
ENV CLUSTER_SERVICE_CURRENT_VERSION=${CLUSTER_SERVICE_CURRENT_VERSION}
ENV Internal__Info__ClusterServiceVersion=${CLUSTER_SERVICE_CURRENT_VERSION}

COPY entrypoint.sh /
COPY --from=builder /gradle/src/build/libs/whispers-translation-service.jar app.jar

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["sh", "./entrypoint.sh"]
