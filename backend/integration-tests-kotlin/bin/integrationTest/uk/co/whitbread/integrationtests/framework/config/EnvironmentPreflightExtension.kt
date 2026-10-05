package uk.co.whitbread.integrationtests.framework.config

import io.kotest.core.listeners.ProjectListener
import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import io.ktor.client.plugins.HttpTimeout

/**
 * Blocks project test execution until the externally managed integration environment is ready.
 */
internal class EnvironmentPreflightExtension(
    private val dependenciesProvider: () -> List<ReadinessDependency>,
    private val settingsProvider: () -> PreflightSettings = { PreflightSettings.load() },
) : ProjectListener {
    override suspend fun beforeProject() {
        val dependencies = dependenciesProvider()
        val settings = settingsProvider()
        val client =
            HttpClient(Java) {
                install(HttpTimeout) {
                    requestTimeoutMillis = settings.requestTimeoutMillis
                    connectTimeoutMillis = settings.requestTimeoutMillis
                    socketTimeoutMillis = settings.requestTimeoutMillis
                }
            }

        try {
            val summary =
                EnvironmentPreflight(
                    dependencies = dependencies,
                    probe = HttpDependencyProbe(client),
                    settings = settings,
                ).awaitReady()
            println(
                "Environment preflight passed: ${summary.dependencyCount} dependencies ready " +
                    "in ${summary.elapsedMillis}ms (${summary.rounds} round${if (summary.rounds == 1) "" else "s"})",
            )
        } finally {
            client.close()
        }
    }
}
