FROM mcr.microsoft.com/devcontainers/base:ubuntu

# Rename default 'vscode' user to 'devuser' to avoid UID conflicts (since 'vscode' takes UID 1000)
RUN usermod -l devuser vscode \
    && groupmod -n devuser vscode \
    && usermod -d /home/devuser -m devuser \
    && echo "devuser ALL=(ALL) NOPASSWD:ALL" >> /etc/sudoers

# Install Eclipse Temurin JDK 21
RUN apt-get update && apt-get install -y wget apt-transport-https gnupg2 \
    && wget -O - https://packages.adoptium.net/artifactory/api/gpg/key/public | apt-key add - \
    && echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | tee /etc/apt/sources.list.d/adoptium.list \
    && apt-get update \
    && apt-get install -y temurin-21-jdk \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install Node.js 24 and npm
RUN curl -fsSL https://deb.nodesource.com/setup_24.x | sudo -E bash - \
    && apt-get update && apt-get install -y nodejs \
    && apt-get clean && rm -rf /var/lib/apt/lists/*
