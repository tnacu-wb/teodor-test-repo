package uk.co.whitbread.integrationtests.framework.config

import uk.co.whitbread.integrationtests.framework.config.validation.validateHttpBaseUrl
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockEndpoints
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget

/**
 * Resolved endpoint configuration for one integration-test process.
 *
 * Host-local defaults preserve the normal developer workflow. Environment overrides
 * allow the same test process to address services by Compose DNS name in a future
 * container runtime.
 */
class IntegrationConfig private constructor(
    val ohipInterfaceBaseUrl: String,
    val hotelReservationBaseUrl: String,
    val rateManagementGatewayBaseUrl: String,
    val contentBaseUrl: String,
    val spendingEntityBaseUrl: String,
    val companyEntityBaseUrl: String,
    val cdhAdapterBaseUrl: String,
    val pibaAccountBaseUrl: String,
    val paymentOrchestrationBaseUrl: String,
    val basketBaseUrl: String,
    val wireMockEndpoints: WireMockEndpoints,
) {
    /**
     * Dependencies that must be ready before Kotest is allowed to execute journeys.
     *
     * Startup remains external to this framework. This list is only the HTTP contract
     * used to verify that the already-started environment is ready for the full suite.
     */
    internal val readinessDependencies =
        listOf(
            ReadinessDependency(
                name = "OHIP adapter",
                url = "$ohipInterfaceBaseUrl/ohip/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Hotel Reservation entity",
                url = "$hotelReservationBaseUrl/v1/reservations/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Content entity",
                url = "$contentBaseUrl/v1/content/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Rules Manager",
                url = "$rateManagementGatewayBaseUrl/rmg/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "CDH adapter",
                url = "$cdhAdapterBaseUrl/v1/cdh/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Company entity",
                url = "$companyEntityBaseUrl/v1/companies/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "PIBA account",
                url = "$pibaAccountBaseUrl/piba-account-service/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Spending entity",
                url = "$spendingEntityBaseUrl/v1/spending/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Payment Orchestration",
                url = "$paymentOrchestrationBaseUrl/payment-orchestrator/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
            ReadinessDependency(
                name = "Basket",
                url = "$basketBaseUrl/v1/baskets/actuator/health",
                check = ReadinessCheck.ActuatorHealth,
            ),
        ) +
            // Derived from the enum rather than listed, so a new WireMock is preflighted the moment
            // it exists. This list is the one place the compiler could not catch an omission, and
            // forgetting an entry here is silent: the container is simply never health-checked.
            WireMockTarget.entries.map { target ->
                ReadinessDependency(
                    name = "WireMock ${target.displayName}",
                    url = "${wireMockEndpoints[target]}/__admin/mappings",
                    check = ReadinessCheck.WireMockMappings(target.requiredMappings),
                )
            }

    companion object {
        const val OHIP_INTERFACE_ENV = "OHIP_INTERFACE_BASE_URL"
        const val HOTEL_RESERVATION_ENV = "HOTEL_RESERVATION_BASE_URL"
        const val RATE_MANAGEMENT_GATEWAY_ENV = "RATE_MANAGEMENT_GATEWAY_BASE_URL"
        const val CONTENT_ENV = "CONTENT_BASE_URL"
        const val SPENDING_ENTITY_ENV = "SPENDING_ENTITY_BASE_URL"
        const val COMPANY_ENTITY_ENV = "COMPANY_ENTITY_BASE_URL"
        const val CDH_ADAPTER_ENV = "CDH_ADAPTER_BASE_URL"
        const val PIBA_ACCOUNT_ENV = "PIBA_ACCOUNT_BASE_URL"
        const val PAYMENT_ORCHESTRATION_ENV = "PAYMENT_ORCHESTRATION_BASE_URL"
        const val BASKET_ENV = "BASKET_BASE_URL"

        const val DEFAULT_OHIP_INTERFACE = "http://localhost:9100"
        const val DEFAULT_HOTEL_RESERVATION = "http://localhost:9103"
        const val DEFAULT_RATE_MANAGEMENT_GATEWAY = "http://localhost:9105"
        const val DEFAULT_CONTENT = "http://localhost:9106"
        const val DEFAULT_SPENDING_ENTITY = "http://localhost:9132"
        const val DEFAULT_COMPANY_ENTITY = "http://localhost:9118"
        const val DEFAULT_CDH_ADAPTER = "http://localhost:9119"
        const val DEFAULT_PIBA_ACCOUNT = "http://localhost:9064"
        const val DEFAULT_PAYMENT_ORCHESTRATION = "http://localhost:9200"
        const val DEFAULT_BASKET = "http://localhost:9104"

        /** Resolves every endpoint exactly once from environment values and local defaults. */
        fun fromEnvironment(environment: (String) -> String? = { setting -> System.getenv(setting) }): IntegrationConfig =
            IntegrationConfig(
                ohipInterfaceBaseUrl = readEndpoint(OHIP_INTERFACE_ENV, DEFAULT_OHIP_INTERFACE, environment),
                hotelReservationBaseUrl =
                    readEndpoint(HOTEL_RESERVATION_ENV, DEFAULT_HOTEL_RESERVATION, environment),
                rateManagementGatewayBaseUrl =
                    readEndpoint(RATE_MANAGEMENT_GATEWAY_ENV, DEFAULT_RATE_MANAGEMENT_GATEWAY, environment),
                contentBaseUrl = readEndpoint(CONTENT_ENV, DEFAULT_CONTENT, environment),
                spendingEntityBaseUrl = readEndpoint(SPENDING_ENTITY_ENV, DEFAULT_SPENDING_ENTITY, environment),
                companyEntityBaseUrl = readEndpoint(COMPANY_ENTITY_ENV, DEFAULT_COMPANY_ENTITY, environment),
                cdhAdapterBaseUrl = readEndpoint(CDH_ADAPTER_ENV, DEFAULT_CDH_ADAPTER, environment),
                pibaAccountBaseUrl = readEndpoint(PIBA_ACCOUNT_ENV, DEFAULT_PIBA_ACCOUNT, environment),
                paymentOrchestrationBaseUrl =
                    readEndpoint(PAYMENT_ORCHESTRATION_ENV, DEFAULT_PAYMENT_ORCHESTRATION, environment),
                basketBaseUrl = readEndpoint(BASKET_ENV, DEFAULT_BASKET, environment),
                wireMockEndpoints = WireMockEndpoints.fromEnvironment(environment),
            )

        private fun readEndpoint(
            setting: String,
            default: String,
            environment: (String) -> String?,
        ): String = validateHttpBaseUrl(setting, environment(setting) ?: default)
    }
}
