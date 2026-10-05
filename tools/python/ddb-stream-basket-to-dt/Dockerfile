# syntax=docker/dockerfile:1

# Use official Python Alpine image for better CPU compatibility across different x86_64 processors
# Python 3.14 is required by pyproject.toml
FROM python:3.14.7-alpine

# Accept build timestamp as a build argument (optional - auto-generates if not provided)
ARG BUILD_TIMESTAMP

# Capture timestamp at build time - use provided ARG or generate with date command
RUN if [ -z "$BUILD_TIMESTAMP" ]; then \
        date -u '+%Y-%m-%d %H:%M:%S UTC' > /build-timestamp.txt; \
    else \
        echo "$BUILD_TIMESTAMP" > /build-timestamp.txt; \
    fi

# Install uv
COPY --from=ghcr.io/astral-sh/uv:0.12.9 /uv /uvx /bin/

# Prevents Python from writing pyc files.
ENV PYTHONDONTWRITEBYTECODE=1

# Keeps Python from buffering stdout and stderr to avoid situations where
# the application crashes without emitting any logs due to buffering.
ENV PYTHONUNBUFFERED=1

# Install the project into `/app`
WORKDIR /app

# Enable bytecode compilation
ENV UV_COMPILE_BYTECODE=1

# Copy from the cache instead of linking since it's a mounted volume
ENV UV_LINK_MODE=copy

# Omit development dependencies
ENV UV_NO_DEV=1

# Create a non-privileged user that the app will run under.
RUN addgroup -g 9001 runner && adduser -G runner -u 9001 -D runner -h /app

# Install the project's dependencies using the lockfile and settings
RUN --mount=type=cache,target=.cache/uv,uid=9001,gid=9001 \
    --mount=type=bind,source=uv.lock,target=uv.lock \
    --mount=type=bind,source=pyproject.toml,target=pyproject.toml \
    su -s /bin/sh - runner -c "uv sync --locked --no-build --no-install-project"

# Then, add the rest of the project source code and install it
# Installing separately from its dependencies allows optimal layer caching
# Copy only necessary files (not tests or other files) to minimize attack surface
COPY pyproject.toml uv.lock basket_stream_to_dynatrace.py /app/
RUN --mount=type=cache,target=.cache/uv,uid=9001,gid=9001 \
    su -s /bin/sh - runner -c "uv sync --locked --no-build"

# Place executables in the environment at the front of the path
ENV PATH="/app/.venv/bin:$PATH"

# Reset the entrypoint, don't invoke `uv`
ENTRYPOINT []

# Remove pip completely since we're using uv exclusively, eliminating PRISMA-2022-0168 vulnerability
# Remove pip from: venv, system Python, uv managed Python, and ensurepip module
RUN rm -f /app/.venv/bin/pip* ; \
    rm -rf /app/.venv/lib/python*/site-packages/pip* ; \
    rm -f /usr/local/bin/pip* ; \
    rm -rf /usr/local/lib/python*/site-packages/pip* ; \
    rm -rf /usr/local/lib/python*/ensurepip ; \
    rm -f /app/.local/share/uv/python/*/bin/pip* ; \
    rm -rf /app/.local/share/uv/python/*/lib/python*/site-packages/pip* ; \
    rm -rf /app/.local/share/uv/python/*/lib/python*/ensurepip 2>/dev/null || true

# Use the non-privileged user to run our application
USER runner

# Run the application.  The DDB_TABLE and DT_ENV environment variables used here must be provided at runtime.
# DT_API_KEY also needs to be provided from a secret store as an environment variable at runtime. 
CMD uv run --locked --no-build basket_stream_to_dynatrace.py ${DDB_TABLE} --dt-env ${DT_ENV:-whitbread-non-prod}

# This is a bit of a hack to get the test containers to wait 5 seconds for this one to be ready.
# The application will exit immediately if it cannot connect to DynamoDB or Dynatrace, so I can't think
# of a meaningful healthcheck other than "does it run at all", which Docker gives us by default.
HEALTHCHECK CMD sleep 5