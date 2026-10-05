package uk.co.whitbread.integrationtests.support.wiremock

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import uk.co.whitbread.integrationtests.framework.http.BAGGAGE_HEADER
import uk.co.whitbread.integrationtests.framework.http.matchesTestId
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockEndpoints
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockInstances
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * One Docker-free stand-in for the four WireMock Admin APIs, shared by the hermetic suite.
 *
 * Every test previously carried its own [MockEngine] emulator. That made each file free to hold a
 * different belief about how WireMock answers, and a wrong belief shared by all of them is
 * invisible: no emulator modelled a malformed success body, so `listStubs` reading `200 {}` as
 * "no mappings" agreed with every test until review caught it.
 *
 * [routes] is the composition point. A fake answers only the routes its test expects and rejects
 * anything else, so "the installer makes no other Admin call" stays an enforced property rather
 * than an accident of which emulator a file happened to define.
 */
internal class FakeWireMockAdmin(
    private val routes: Set<AdminRoute>,
    private val journalCount: Int = 0,
    private val mappingStatus: (StubMapping) -> HttpStatusCode = { HttpStatusCode.Created },
    private val failInstallAt: Int? = null,
    failRequestRemoval: Boolean = false,
    private val onInstall: (String) -> Unit = {},
) {
    /** Every mapping POSTed, recorded before the response status is decided. */
    val installed = CopyOnWriteArrayList<CapturedMapping>()

    /** Journal count queries, in the order they were asked. */
    val countQueries = CopyOnWriteArrayList<CountQuery>()

    /** Successfully installed mappings per WireMock host, carrying their assigned stub IDs. */
    val mappings: Map<String, MutableList<StubMapping>> =
        HOSTS.associateWith { CopyOnWriteArrayList<StubMapping>() }

    val adminCallCount = AtomicInteger()
    val postCount = AtomicInteger()

    /** Mutable so a test can let cleanup fail once and then succeed on retry. */
    val failRequestRemoval = AtomicBoolean(failRequestRemoval)

    private val json = Json { ignoreUnknownKeys = true }

    private val client =
        HttpClient(MockEngine) {
            install(ContentNegotiation) {
                // WireMockAdmin.stub sends a typed StubMapping body, so the install route needs
                // content negotiation; every other Admin call sends an already-encoded string.
                json(json)
            }
            engine {
                addHandler { request -> handle(request) }
            }
        }

    val instances: WireMockInstances = testInstances(client)

    /** Installed mappings across all hosts whose baggage matcher names [testId]. */
    fun ownedMappings(testId: String): List<StubMapping> = mappings.values.flatten().filter { mapping -> mapping.isOwnedBy(testId) }

    fun close() {
        instances.close()
    }

    private suspend fun MockRequestHandleScope.handle(request: HttpRequestData): HttpResponseData {
        adminCallCount.incrementAndGet()
        val path = request.url.encodedPath
        val hostMappings = mappings.getValue(request.url.host)

        return when {
            request.method == HttpMethod.Post && path == MAPPINGS && AdminRoute.INSTALL_MAPPING in routes ->
                install(request)

            request.method == HttpMethod.Get && path == MAPPINGS && AdminRoute.LIST_MAPPINGS in routes ->
                respondJson(mappingsBody(hostMappings))

            request.method == HttpMethod.Delete &&
                path.startsWith("$MAPPINGS/") &&
                AdminRoute.DELETE_MAPPING in routes -> {
                val id = path.substringAfterLast('/')
                hostMappings.removeAll { mapping -> mapping.id == id }
                respond("", HttpStatusCode.NoContent)
            }

            request.method == HttpMethod.Post && path == COUNT_REQUESTS && AdminRoute.COUNT_REQUESTS in routes -> {
                countQueries += CountQuery(request.url.host, json.decodeFromString(request.bodyText()))
                respondJson("""{"count":$journalCount,"requestJournalDisabled":false}""")
            }

            request.method == HttpMethod.Post && path == REMOVE_REQUESTS && AdminRoute.REMOVE_REQUESTS in routes -> {
                if (failRequestRemoval.get()) {
                    respond("request cleanup failed", HttpStatusCode.InternalServerError)
                } else {
                    respondJson("""{"serveEvents":[]}""")
                }
            }

            // Deliberately fatal: a test only enables the routes it expects, so an unhandled call
            // is the installer doing something the test did not account for.
            else -> error("Unexpected request: ${request.method.value} ${request.url}")
        }
    }

    private suspend fun MockRequestHandleScope.install(request: HttpRequestData): HttpResponseData {
        val currentPost = postCount.incrementAndGet()
        if (currentPost == failInstallAt) {
            return respond("install failed", HttpStatusCode.InternalServerError)
        }

        val mapping = json.decodeFromString<StubMapping>(request.bodyText())
        installed += CapturedMapping(request.url.host, mapping)
        onInstall(request.url.host)

        val status = mappingStatus(mapping)
        if (!status.isSuccess()) {
            return respondJson("""{"error":"rejected"}""", status)
        }

        val id = "mapping-$currentPost"
        mappings.getValue(request.url.host) += mapping.copy(id = id)

        return respondJson("""{"id":"$id"}""", HttpStatusCode.Created)
    }

    private suspend fun HttpRequestData.bodyText(): String = body.toByteArray().decodeToString()

    private fun MockRequestHandleScope.respondJson(
        content: String,
        status: HttpStatusCode = HttpStatusCode.OK,
    ): HttpResponseData =
        respond(
            content = content,
            status = status,
            headers = headersOf("Content-Type", ContentType.Application.Json.toString()),
        )

    private companion object {
        const val MAPPINGS = "/__admin/mappings"
        const val COUNT_REQUESTS = "/__admin/requests/count"
        const val REMOVE_REQUESTS = "/__admin/requests/remove"
    }
}

/** WireMock Admin operations a [FakeWireMockAdmin] can be configured to answer. */
internal enum class AdminRoute {
    INSTALL_MAPPING,
    LIST_MAPPINGS,
    DELETE_MAPPING,
    COUNT_REQUESTS,
    REMOVE_REQUESTS,
}

internal data class CapturedMapping(
    val host: String,
    val mapping: StubMapping,
)

internal data class CountQuery(
    val host: String,
    val pattern: RequestPattern,
)

/**
 * Answers stub installation only, so any other Admin call fails the test.
 *
 * @param mappingStatus lets a scenario reject a chosen mapping to exercise install failure.
 */
internal fun installOnlyWireMock(mappingStatus: (StubMapping) -> HttpStatusCode = { HttpStatusCode.Created }): FakeWireMockAdmin =
    FakeWireMockAdmin(
        routes = setOf(AdminRoute.INSTALL_MAPPING),
        mappingStatus = mappingStatus,
    )

/**
 * Answers stub installation only, notifying [onInstall] of each installing host in order.
 *
 * Separate from [installOnlyWireMock] so each factory takes exactly one lambda: with two, a
 * trailing-lambda call site binds to the last parameter, and Kotlin's coercion of any lambda
 * result to `Unit` would make that misbinding compile and silently disable the other hook.
 */
internal fun recordingInstallWireMock(onInstall: (String) -> Unit): FakeWireMockAdmin =
    FakeWireMockAdmin(
        routes = setOf(AdminRoute.INSTALL_MAPPING),
        onInstall = onInstall,
    )

/** Answers journal counts only, so counting cannot be satisfied by any other Admin call. */
internal fun journalCountWireMock(journalCount: Int): FakeWireMockAdmin =
    FakeWireMockAdmin(
        routes = setOf(AdminRoute.COUNT_REQUESTS),
        journalCount = journalCount,
    )

/** Install, list, delete, and journal removal: the provisioning lifecycle without counting. */
internal fun provisioningWireMock(
    failInstallAt: Int? = null,
    failRequestRemoval: Boolean = false,
): FakeWireMockAdmin =
    FakeWireMockAdmin(
        routes =
            setOf(
                AdminRoute.INSTALL_MAPPING,
                AdminRoute.LIST_MAPPINGS,
                AdminRoute.DELETE_MAPPING,
                AdminRoute.REMOVE_REQUESTS,
            ),
        failInstallAt = failInstallAt,
        failRequestRemoval = failRequestRemoval,
    )

/** The four named WireMock targets backed by one deterministic transport. */
internal fun testInstances(client: HttpClient): WireMockInstances =
    WireMockEndpoints
        .of(WireMockTarget.entries.associateWith { target -> "http://${target.name.lowercase()}.test" })
        .createInstances(client)

/** Renders a WireMock `GET /__admin/mappings` success body. */
internal fun mappingsBody(mappings: List<StubMapping>): String =
    """{"mappings":${Json.encodeToString(ListSerializer(StubMapping.serializer()), mappings)}}"""

/** True when this mapping's baggage matcher names [testId]. */
internal fun StubMapping.isOwnedBy(testId: String): Boolean =
    request.headers
        ?.get(BAGGAGE_HEADER)
        ?.matchesTestId(testId) == true

private val HOSTS = listOf("opera.test", "cdh.test", "aem.test", "worldline.test")
