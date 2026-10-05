package uk.co.whitbread.integrationtests.provisioning

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiDoc
import io.ktor.openapi.OpenApiInfo
import io.ktor.openapi.jsonSchema
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.OpenApiDocSource
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.openapi.hide
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.utils.io.ExperimentalKtorApi
import kotlinx.coroutines.CancellationException
import uk.co.whitbread.integrationtests.framework.http.testIdHeaderValue
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockEndpoints
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockInstances
import uk.co.whitbread.integrationtests.testkit.mocks.MockInstaller
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.bookingJson
import java.util.UUID

fun main() {
    val wiremock = WireMockEndpoints.fromEnvironment().createInstances()

    try {
        embeddedServer(
            factory = Netty,
            host = System.getenv("MOCK_PROVISIONING_HOST") ?: "127.0.0.1",
            port = System.getenv("MOCK_PROVISIONING_PORT")?.toInt() ?: 9190,
            module = { mockProvisioningModule(wiremock) },
        ).start(wait = true)
    } finally {
        wiremock.close()
    }
}

@OptIn(ExperimentalKtorApi::class)
internal fun Application.mockProvisioningModule(
    wiremock: WireMockInstances,
    newTestId: () -> String = { UUID.randomUUID().toString() },
) {
    val mockInstaller = MockInstaller(wiremock)

    install(ContentNegotiation) {
        json(bookingJson)
    }
    // The mocks install or they do not. Nothing automated branches on why, and the log line below
    // keeps the exception and its stack trace, so the response only has to say that provisioning
    // failed. That is the whole error contract, and it is why no route carries a catch.
    install(StatusPages) {
        exception<CancellationException> { _, failure -> throw failure }
        exception<Throwable> { call, failure ->
            this@mockProvisioningModule.log.error("Mock provisioning failed", failure)
            call.respond(HttpStatusCode.ServiceUnavailable, ApiError("Mock provisioning failed"))
        }
    }

    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, HealthResponse(status = "UP"))
        }

        post("/mock-sessions") {
            val booking = call.receive<Booking>()
            val testId = newTestId()

            mockInstaller.installFor(booking, testId)

            call.respond(
                HttpStatusCode.Created,
                MockSessionResponse(
                    testId = testId,
                    baggage = testIdHeaderValue(testId),
                ),
            )
        }.describe {
            summary = "Create an isolated mock session"
            requestBody {
                required = true
                description = "Booking scenario data used to derive WireMock mappings"
                // Load-bearing, not decoration: this line generates the entire Booking schema
                // graph, and with it the only browsable description of the request contract.
                // Ktor's OpenAPI compiler plugin is opt-in and inert in this build, so nothing
                // regenerates the schema if this is deleted — /openapi.json would still answer
                // 200 with an empty `components`.
                schema = jsonSchema<Booking>()
            }
            responses {
                HttpStatusCode.Created {
                    description = "Mocks installed under a server-generated test ID"
                    schema = jsonSchema<MockSessionResponse>()
                }
            }
        }

        delete("/mock-sessions/{testId}") {
            mockInstaller.removeFor(checkNotNull(call.parameters["testId"]))
            call.respond(HttpStatusCode.NoContent)
        }

        val openApiBase =
            OpenApiDoc(
                info = OpenApiInfo(title = "Mock Provisioning API", version = "poc"),
            )
        val openApiSource = OpenApiDocSource.Routing(contentType = ContentType.Application.Json)

        get("/openapi.json") {
            val document = checkNotNull(openApiSource.read(call.application, openApiBase))
            call.respondText(document.content, document.contentType)
        }.hide()

        swaggerUI("/swagger") {
            info = openApiBase.info
            source = openApiSource
            remotePath = "openapi.json"
        }
    }
}
