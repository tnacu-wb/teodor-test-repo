package uk.co.whitbread.integrationtests.framework.config

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import uk.co.whitbread.integrationtests.framework.wiremock.BakedMappings
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget

class IntegrationConfigTest :
    FunSpec({
        test("host-local defaults preserve the current integration environment") {
            val config = IntegrationConfig.fromEnvironment { null }

            serviceEndpoints(config) shouldBe
                mapOf(
                    IntegrationConfig.OHIP_INTERFACE_ENV to IntegrationConfig.DEFAULT_OHIP_INTERFACE,
                    IntegrationConfig.HOTEL_RESERVATION_ENV to IntegrationConfig.DEFAULT_HOTEL_RESERVATION,
                    IntegrationConfig.RATE_MANAGEMENT_GATEWAY_ENV to
                        IntegrationConfig.DEFAULT_RATE_MANAGEMENT_GATEWAY,
                    IntegrationConfig.CONTENT_ENV to IntegrationConfig.DEFAULT_CONTENT,
                    IntegrationConfig.SPENDING_ENTITY_ENV to IntegrationConfig.DEFAULT_SPENDING_ENTITY,
                    IntegrationConfig.COMPANY_ENTITY_ENV to IntegrationConfig.DEFAULT_COMPANY_ENTITY,
                    IntegrationConfig.CDH_ADAPTER_ENV to IntegrationConfig.DEFAULT_CDH_ADAPTER,
                    IntegrationConfig.PIBA_ACCOUNT_ENV to IntegrationConfig.DEFAULT_PIBA_ACCOUNT,
                    IntegrationConfig.PAYMENT_ORCHESTRATION_ENV to
                        IntegrationConfig.DEFAULT_PAYMENT_ORCHESTRATION,
                    IntegrationConfig.BASKET_ENV to IntegrationConfig.DEFAULT_BASKET,
                )
            WireMockTarget.entries.forEach { target ->
                config.wireMockEndpoints[target] shouldBe target.defaultUrl
            }
        }

        test("each service environment setting overrides only its endpoint") {
            val cases =
                serviceEndpoints(IntegrationConfig.fromEnvironment { null })
                    .keys
                    .associateWith { setting -> "http://${setting.lowercase().replace('_', '-')}.test:8080/" }

            cases.forEach { (overriddenSetting, overriddenValue) ->
                val config =
                    IntegrationConfig.fromEnvironment { setting ->
                        overriddenValue.takeIf { setting == overriddenSetting }
                    }

                serviceEndpoints(config).forEach { (setting, actual) ->
                    val expected =
                        if (setting == overriddenSetting) {
                            overriddenValue.removeSuffix("/")
                        } else {
                            serviceEndpoints(IntegrationConfig.fromEnvironment { null }).getValue(setting)
                        }
                    actual shouldBe expected
                }
            }
        }

        test("all endpoint loaders share strict origin validation") {
            val settings =
                serviceEndpoints(IntegrationConfig.fromEnvironment { null }).keys +
                    WireMockTarget.entries.map { target -> target.envVar }
            val invalidValues =
                listOf(
                    "",
                    "not a URL",
                    "ftp://service.test:8080",
                    "http:///missing-host",
                    "http://service.test:0",
                    "http://service.test:65536",
                    "http://user@service.test:8080",
                    "http://service.test:8080/path",
                    "http://service.test:8080?query=true",
                    "http://service.test:8080#fragment",
                )

            settings.forEach { setting ->
                invalidValues.forEach { invalid ->
                    val failure =
                        shouldThrow<IllegalArgumentException> {
                            IntegrationConfig.fromEnvironment { candidate ->
                                invalid.takeIf { candidate == setting }
                            }
                        }
                    failure.message.orEmpty() shouldContain setting
                }
            }
        }

        test("readiness dependencies use the resolved endpoint snapshot") {
            val values =
                mapOf(
                    IntegrationConfig.OHIP_INTERFACE_ENV to "http://ohip:9100/",
                    IntegrationConfig.HOTEL_RESERVATION_ENV to "http://hotel-reservation:9103/",
                    IntegrationConfig.RATE_MANAGEMENT_GATEWAY_ENV to "http://rules:9105/",
                    IntegrationConfig.CONTENT_ENV to "http://content:9106/",
                    IntegrationConfig.SPENDING_ENTITY_ENV to "http://spending:9132/",
                    IntegrationConfig.COMPANY_ENTITY_ENV to "http://company:9118/",
                    IntegrationConfig.CDH_ADAPTER_ENV to "http://cdh-adapter:9119/",
                    IntegrationConfig.PIBA_ACCOUNT_ENV to "http://piba:9064/",
                    IntegrationConfig.PAYMENT_ORCHESTRATION_ENV to "http://payment-orchestration:9200/",
                    IntegrationConfig.BASKET_ENV to "http://basket:9104/",
                ) +
                    WireMockTarget.entries.associate { target ->
                        target.envVar to "http://wiremock-${target.name.lowercase()}:8080/"
                    }

            val dependencies =
                IntegrationConfig
                    .fromEnvironment(values::get)
                    .readinessDependencies
                    .associate { dependency -> dependency.name to dependency.url }

            dependencies shouldBe
                mapOf(
                    "OHIP adapter" to "http://ohip:9100/ohip/actuator/health",
                    "Hotel Reservation entity" to
                        "http://hotel-reservation:9103/v1/reservations/actuator/health",
                    "Content entity" to "http://content:9106/v1/content/actuator/health",
                    "Rules Manager" to "http://rules:9105/rmg/actuator/health",
                    "CDH adapter" to "http://cdh-adapter:9119/v1/cdh/actuator/health",
                    "Company entity" to "http://company:9118/v1/companies/actuator/health",
                    "PIBA account" to "http://piba:9064/piba-account-service/actuator/health",
                    "Spending entity" to "http://spending:9132/v1/spending/actuator/health",
                    "Payment Orchestration" to
                        "http://payment-orchestration:9200/payment-orchestrator/actuator/health",
                    "Basket" to "http://basket:9104/v1/baskets/actuator/health",
                    "WireMock Opera" to "http://wiremock-opera:8080/__admin/mappings",
                    "WireMock CDH" to "http://wiremock-cdh:8080/__admin/mappings",
                    "WireMock AEM" to "http://wiremock-aem:8080/__admin/mappings",
                    "WireMock Worldline" to "http://wiremock-worldline:8080/__admin/mappings",
                )
        }

        test("only the WireMocks that bind-mount startup mappings require them") {
            // Requiring names on AEM or Worldline would fail preflight against a healthy stack;
            // omitting them on CDH or Opera restores the silent-401 failure this check exists for.
            val required =
                IntegrationConfig
                    .fromEnvironment { null }
                    .readinessDependencies
                    .mapNotNull { dependency ->
                        (dependency.check as? ReadinessCheck.WireMockMappings)
                            ?.let { dependency.name to it.requiredNames }
                    }.toMap()

            required shouldBe
                mapOf(
                    "WireMock Opera" to BakedMappings.OPERA,
                    "WireMock CDH" to BakedMappings.CDH,
                    "WireMock AEM" to emptySet(),
                    "WireMock Worldline" to emptySet(),
                )
        }
    })

private fun serviceEndpoints(config: IntegrationConfig): Map<String, String> =
    linkedMapOf(
        IntegrationConfig.OHIP_INTERFACE_ENV to config.ohipInterfaceBaseUrl,
        IntegrationConfig.HOTEL_RESERVATION_ENV to config.hotelReservationBaseUrl,
        IntegrationConfig.RATE_MANAGEMENT_GATEWAY_ENV to config.rateManagementGatewayBaseUrl,
        IntegrationConfig.CONTENT_ENV to config.contentBaseUrl,
        IntegrationConfig.SPENDING_ENTITY_ENV to config.spendingEntityBaseUrl,
        IntegrationConfig.COMPANY_ENTITY_ENV to config.companyEntityBaseUrl,
        IntegrationConfig.CDH_ADAPTER_ENV to config.cdhAdapterBaseUrl,
        IntegrationConfig.PIBA_ACCOUNT_ENV to config.pibaAccountBaseUrl,
        IntegrationConfig.PAYMENT_ORCHESTRATION_ENV to config.paymentOrchestrationBaseUrl,
        IntegrationConfig.BASKET_ENV to config.basketBaseUrl,
    )
