FROM postgres:17-alpine

# Le misure sono salvate con timestamp: il database lavora sempre in UTC
ENV TZ=UTC \
    PGTZ=UTC

HEALTHCHECK --interval=10s --timeout=5s --start-period=10s --retries=5 \
    CMD pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" || exit 1
