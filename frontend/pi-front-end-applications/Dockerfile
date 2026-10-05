FROM node:24.20.0-alpine3.24 AS base

RUN npm install --global npm@11.19.0

RUN apk add --no-cache ripgrep

RUN apk update && apk upgrade --no-cache && apk add --no-cache openssl

RUN addgroup -g 9001 runner && adduser -G runner -u 9001 -D runner \
    && mkdir /app /sharp \
    && chown -R runner:runner /app /sharp

USER runner

FROM base AS application

ARG APP_NAME

ENV APP_NAME=${APP_NAME}
ENV PORT=3002

WORKDIR /sharp
RUN npm install sharp

WORKDIR /app
COPY --link --chown=9001:9001 ./apps/next-apps/${APP_NAME}/public ./apps/next-apps/${APP_NAME}/public
COPY --link --chown=9001:9001 ./apps/next-apps/${APP_NAME}/.next/standalone ./
COPY --link --chown=9001:9001 ./apps/next-apps/${APP_NAME}/.next/static ./apps/next-apps/${APP_NAME}/.next/static

# The runtime env-var tool now runs on Node built-ins only (node:util parseArgs +
# styleText), so it needs no `npm install` layer and ships no third-party code.
COPY --link --chown=9001:9001 ./scripts/runtimeEnvVars /runtimeEnvVars
COPY --link --chown=9001:9001 ./apps/next-apps/${APP_NAME}/.env.production ./apps/next-apps/${APP_NAME}/.env.production

WORKDIR /app/apps/next-apps/${APP_NAME}
EXPOSE 3002

# Uses the global fetch + AbortSignal.timeout, so a hung server fails the probe after
# 5s instead of relying solely on Docker's own --timeout to kill it.
HEALTHCHECK --interval=10s --timeout=10s --start-period=90s --retries=6 \
    CMD node -e "fetch('http://127.0.0.1:' + (process.env.PORT || 3002) + '/api/liveness', { signal: AbortSignal.timeout(5000) }).then(response => process.exit(response.status === 200 ? 0 : 1)).catch(() => process.exit(1))"

ENV NODE_OPTIONS="--unhandled-rejections=warn --max-old-space-size=2048"
ENV NEXT_SHARP_PATH="/sharp/node_modules/sharp"
# Next.js standalone binds to `process.env.HOSTNAME || '0.0.0.0'`, and Docker sets
# HOSTNAME to the container id - so without this the server listens only on the
# container IP and every loopback HEALTHCHECK probe fails.
ENV HOSTNAME="0.0.0.0"

ENTRYPOINT ["/bin/ash"]
CMD ["-c", "/runtimeEnvVars/index.mjs replace --working-dir=. --env-file-path=./.env.production -v && node server.js"]
