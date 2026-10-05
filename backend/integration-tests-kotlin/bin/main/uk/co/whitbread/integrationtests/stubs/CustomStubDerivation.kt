package uk.co.whitbread.integrationtests.stubs

import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition

/**
 * Base derivation for custom override stubs.
 *
 * A custom stub replaces a default by excluding the default's id and installing the override
 * under its own unique id. The override must keep the default's request matchers byte-identical,
 * so the changed response lands on exactly the requests the default would have served — copying
 * matcher shapes by hand is how overrides silently drift from their defaults. Deriving keeps the
 * matcher in one place: build the default, re-id it, swap only the response.
 *
 * Used by the CDH search error/limit overrides and the Opera empty-body overrides; the Opera
 * rejection overrides use their own [rejectedByOpera]-style wrapper over the same idea.
 */
internal fun PlannedStub.answering(
    id: String,
    response: ResponseDefinition,
): PlannedStub =
    copy(
        id = id,
        mappings = mappings.map { mapping -> mapping.copy(response = response) },
    )
