package uk.co.whitbread.integrationtests.testkit.featureflags

import uk.co.whitbread.integrationtests.framework.http.BaggageFlag

/**
 * One feature flag a scenario can override on a service-under-test call.
 *
 * [key] is the Unleash flag name exactly as the service resolves it, so a typo here
 * silently leaves the deployed default in place rather than failing the scenario.
 *
 * Implementations are per-service enums in this package, for example [OhipFeatureFlag].
 * The interface is sealed so a journey cannot introduce an ad-hoc flag outside that
 * vocabulary; the framework's baggage encoding sees it only through [BaggageFlag].
 */
sealed interface FeatureFlag : BaggageFlag
