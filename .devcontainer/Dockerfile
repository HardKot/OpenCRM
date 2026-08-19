# СТАДИЯ 1: забираем Java (JDK), Node.js, gradle из официальных образов
FROM eclipse-temurin:21-jdk-jammy AS java
FROM node:24-slim AS node
FROM gradle:8.10-jdk21 AS gradle

# СТАДИЯ 2: финальный лёгкий образ
FROM ubuntu:26.04 AS devcontainer

EXPOSE 3000
EXPOSE 8080
EXPOSE 80
EXPOSE 443
EXPOSE 5005

# Копирование утилит и библиотек из предыдущих стадий
# Устанавливаем Java (JRE) из стадии java
COPY --from=java /opt/java/openjdk /opt/java
ENV JAVA_HOME=/opt/java
ENV PATH="$JAVA_HOME/bin:$PATH"

# Устанавливаем Node.js из стадии node
COPY --from=node /usr/local/bin/node /usr/local/bin/
COPY --from=node /usr/local/lib/node_modules /usr/local/lib/node_modules
RUN ln -s /usr/local/lib/node_modules/npm/bin/npm-cli.js /usr/local/bin/npm && \
    ln -s /usr/local/lib/node_modules/npm/bin/npx-cli.js /usr/local/bin/npx

# Устанавливаем Gradle из стадии gradle
COPY --from=gradle /opt/gradle /opt/gradle
ENV GRADLE_HOME=/opt/gradle
ENV PATH="$GRADLE_HOME/bin:$PATH"


# Установка репозиториев, пакетов и дополнительных утилит
RUN echo "==> Установка основных пакетов" \
    && apt-get update && apt-get install -y --no-install-recommends \
    git \
    curl \
    ca-certificates \
    sudo \
    && echo "==> Установка дополнительных пакетов" \
    neovim \
    postgresql-client \
    ripgrep \
    && echo "==> Очистка кэша пакетов" \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

ARG USERNAME=devuser

RUN mkdir -p /etc/sudoers.d/ \
    && usermod -l $USERNAME ubuntu \
    && usermod -d /home/$USERNAME -m $USERNAME \
    && groupmod -n $USERNAME ubuntu 2>/dev/null || true \
    && echo "$USERNAME ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME \
    && mkdir -p /commandhistory \
    && touch /commandhistory/.bash_history \
    && chown -R $USERNAME:$USERNAME /commandhistory \
    && echo "export PROMPT_COMMAND='history -a' && export HISTFILE=/commandhistory/.bash_history" >> "/home/$USERNAME/.bashrc"

# Настройка переменных окружения для подключения к PostgreSQL
ENV PGHOST=localhost \
    PGPORT=5432 \
    PGUSER=postgres \
    PGPASSWORD=postgres \
    PGDATABASE=postgres


# Настройка переменных окружения для приложения
ENV SPRING_PROFILES_ACTIVE=dev \
    DOCKER_COMPOSE_ENABLED=false \
    SPRING_DEVTOOLS_RESTART_ENABLED=true \
    SPRING_DEVTOOLS_RESTART_POLL_INTERVAL=1s \
    SPRING_DEVTOOLS_RESTART_QUIET_PERIOD=500ms \ 
    ENABLE_REMOTE_DEBUG=true \
    CHOKIDAR_USEPOLLING=true \
    CHOKIDAR_INTERVAL=300 \
    PATH=${PATH}:/workspace/scripts

USER $USERNAME
WORKDIR /workspace

CMD ["sleep", "infinity"]
