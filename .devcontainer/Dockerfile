FROM eclipse-temurin:25-jdk-alpine

# verknüpft das Image mit dem Repo (nötig, damit GITHUB_TOKEN es in ghcr.io pushen/pullen darf)
LABEL org.opencontainers.image.source="https://github.com/nicoweiss67/450-tictactest-mvk"

ARG GRADLE_VERSION=9.3.0
ARG USERNAME=nico
ARG USER_UID=1000
ARG USER_GID=1000

# Basis-Tools
RUN apk update && apk add --no-cache \
        bash \
        curl \
        git \
        unzip \
        shadow \
        python3 \
        rsync \
        gcompat \
        libstdc++

# Gradle manuell installieren (apk-Paket ist zu alt / hängt an OpenJDK 8)
RUN curl -fsSL -o /tmp/gradle.zip \
        https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip \
    && unzip -q /tmp/gradle.zip -d /opt \
    && rm /tmp/gradle.zip

ENV GRADLE_HOME=/opt/gradle-${GRADLE_VERSION}
ENV PATH=${GRADLE_HOME}/bin:${PATH}

# Benutzer mit UID:GID 1000:1000 anlegen
RUN addgroup -g ${USER_GID} ${USERNAME} \
    && adduser -D -u ${USER_UID} -G ${USERNAME} -s /bin/bash ${USERNAME}

USER ${USERNAME}
WORKDIR /workspace