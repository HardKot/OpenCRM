#!/usr/bin/env bash
set -euo pipefail

cd /workspace/frontend

if [ ! -d node_modules ] || [ -z "$(ls -A node_modules 2>/dev/null)" ]; then
  npm ci --no-audit --no-fund
fi

npm start &
FRONT_PID=$!

cd /workspace/backend
GRADLE_MAX_WORKERS="${GRADLE_MAX_WORKERS:-$(nproc)}"
GRADLE_JVM_ARGS="${GRADLE_JVM_ARGS:--Xms512m -Xmx4g -XX:MaxMetaspaceSize=1g}"

./gradlew bootRun --continuous \
  -Dorg.gradle.daemon=true \
  -Dorg.gradle.parallel=true \
  "-Dorg.gradle.workers.max=${GRADLE_MAX_WORKERS}" \
  "-Dorg.gradle.jvmargs=${GRADLE_JVM_ARGS}" &
BACK_PID=$!

cleanup() {
  kill "$FRONT_PID" "$BACK_PID" 2>/dev/null || true
  wait || true
}

trap cleanup INT TERM EXIT

wait -n "$FRONT_PID" "$BACK_PID"
