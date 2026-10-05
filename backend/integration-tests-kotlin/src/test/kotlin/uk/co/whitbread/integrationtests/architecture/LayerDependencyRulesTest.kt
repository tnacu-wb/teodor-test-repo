package uk.co.whitbread.integrationtests.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import java.io.File

/**
 * Docker-free structural rules for package layout and layer boundaries.
 *
 * Scopes are path-based so the same package name can exist in more than one
 * source set (main / test / integrationTest).
 */
class LayerDependencyRulesTest :
    FunSpec({
        val moduleRoot = File(".").canonicalFile
        val mainKotlin = File(moduleRoot, "src/main/kotlin")
        val integrationKotlin = File(moduleRoot, "src/integrationTest/kotlin")
        val rootPackage = "uk.co.whitbread.integrationtests"
        val clientsPrefix = "$rootPackage.clients"
        val journeysPrefix = "$rootPackage.journeys"
        val stubsPrefix = "$rootPackage.stubs"
        val frameworkPrefix = "$rootPackage.framework"
        val testkitPrefix = "$rootPackage.testkit"
        val testkitModelPrefix = "$rootPackage.testkit.model"

        fun importNames(file: File): List<String> =
            file
                .readLines()
                .map { it.trim() }
                .filter { it.startsWith("import ") }
                .map { it.removePrefix("import ").substringBefore(" as ").trim() }

        fun packageName(file: File): String? =
            file
                .readLines()
                .firstOrNull { it.trim().startsWith("package ") }
                ?.trim()
                ?.removePrefix("package ")
                ?.trim()

        fun kotlinFilesUnder(dir: File): List<File> =
            dir
                .walkTopDown()
                .filter { it.isFile && it.extension == "kt" }
                .toList()

        fun topLevelPackagesUnder(sourceSetKotlin: File): Set<String> {
            val base = File(sourceSetKotlin, rootPackage.replace('.', '/'))
            if (!base.isDirectory) return emptySet()
            return base
                .listFiles()
                ?.filter { it.isDirectory }
                ?.map { it.name }
                ?.toSet()
                .orEmpty()
        }

        fun filesImporting(
            files: List<File>,
            forbiddenPrefixes: List<String>,
        ): List<String> =
            files.flatMap { file ->
                importNames(file)
                    .filter { importName -> forbiddenPrefixes.any { importName.startsWith(it) } }
                    .map { importName -> "${file.relativeTo(moduleRoot)} -> $importName" }
            }

        fun classNames(file: File): List<String> {
            val classRegex =
                Regex(
                    """^(?:(?:public|internal|private|protected|data|sealed|open|abstract|final|enum|annotation)\s+)*class\s+(\w+)""",
                )
            return file.readLines().mapNotNull { line ->
                classRegex.find(line.trim())?.groupValues?.get(1)
            }
        }

        val journeyFiles = kotlinFilesUnder(integrationKotlin).filter { it.path.contains("/journeys/") }

        // `main` and `stubs` need no rule against importing clients or journeys: those packages exist
        // only in the integrationTest source set, which is never on main's compile classpath, so the
        // import would not resolve.

        test("framework must not import journeys or clients") {
            val frameworkFiles =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filter { it.path.contains("/framework/") }
            filesImporting(frameworkFiles, listOf(clientsPrefix, journeysPrefix)).shouldBeEmpty()
        }

        test("framework must not import testkit") {
            val frameworkFiles =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filter { it.path.contains("/framework/") }
            filesImporting(frameworkFiles, listOf(testkitPrefix)).shouldBeEmpty()
        }

        test("wiremock framework must not import reporting") {
            val wiremockFiles =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filter { it.path.contains("/framework/wiremock/") }
            filesImporting(wiremockFiles, listOf("$frameworkPrefix.reporting")).shouldBeEmpty()
        }

        test("http framework must not import reporting") {
            val httpFiles =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filter { it.path.contains("/framework/http/") }
            filesImporting(httpFiles, listOf("$frameworkPrefix.reporting")).shouldBeEmpty()
        }

        test("testkit must not import journeys or clients") {
            val testkitFiles =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filter { it.path.contains("/testkit/") }
            filesImporting(testkitFiles, listOf(clientsPrefix, journeysPrefix)).shouldBeEmpty()
        }

        test("clients must not import stubs") {
            val clientFiles = kotlinFilesUnder(integrationKotlin).filter { it.path.contains("/clients/") }
            filesImporting(clientFiles, listOf(stubsPrefix)).shouldBeEmpty()
        }

        test("clients import only from the client allowlist") {
            // Every request a client issues must go through ServiceApiClient, which supplies the
            // baggage header, evidence capture, and the ApiResult decoding. A blocklist of Ktor
            // imports cannot guarantee that: a client written against java.net.http or OkHttp
            // would pass it while silently dropping all three. So the rule is an allowlist —
            // any new import prefix under clients/ fails the build by name and must be added
            // here deliberately.
            val allowedImportPrefixes =
                listOf(
                    // Endpoint-specific request shaping; verbs and bodies stay in ServiceApiClient.
                    "io.ktor.client.request.parameter",
                    "io.ktor.client.request.header",
                    "java.time.",
                    "kotlinx.serialization.",
                    "$clientsPrefix.",
                    "$frameworkPrefix.config.IntegrationTestConfig",
                    "$frameworkPrefix.http.ApiResult",
                    "$frameworkPrefix.http.ServiceApiClient",
                    "$testkitPrefix.featureflags.",
                    "$testkitPrefix.model.",
                )
            val clientFiles = kotlinFilesUnder(integrationKotlin).filter { it.path.contains("/clients/") }
            val offenders =
                clientFiles.flatMap { file ->
                    importNames(file)
                        .filterNot { importName -> allowedImportPrefixes.any { importName.startsWith(it) } }
                        .map { importName -> "${file.relativeTo(moduleRoot)} -> $importName" }
                }
            offenders.shouldBeEmpty()
        }

        test("only testkit.featureflags may import BaggageFlag") {
            // BaggageFlag is the framework's open seam for baggage-encodable flags, and the
            // sealed FeatureFlag vocabulary is its only sanctioned implementation. Any other
            // implementor could put an unreviewed flag key on the wire, so the import is
            // confined to the vocabulary package. FeatureFlag itself is sealed, so its own
            // hierarchy cannot grow outside that package either.
            val files =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filterNot { it.path.contains("/testkit/featureflags/") }
                    .filterNot { it.path.contains("/framework/http/") }
            filesImporting(files, listOf("$frameworkPrefix.http.BaggageFlag")).shouldBeEmpty()
        }

        test("nothing outside journeys may import journeys") {
            val nonJourneyFiles =
                (kotlinFilesUnder(mainKotlin) + kotlinFilesUnder(integrationKotlin))
                    .filterNot { it.path.contains("/journeys/") }
            filesImporting(nonJourneyFiles, listOf(journeysPrefix)).shouldBeEmpty()
        }

        test("main root packages are framework, provisioning, stubs, testkit") {
            topLevelPackagesUnder(mainKotlin)
                .shouldContainExactlyInAnyOrder(setOf("framework", "provisioning", "stubs", "testkit"))
        }

        test("integrationTest root packages are clients, framework, journeys, testkit") {
            topLevelPackagesUnder(integrationKotlin)
                .shouldContainExactlyInAnyOrder(setOf("clients", "framework", "journeys", "testkit"))
        }

        test("journey classes extend JourneySpec") {
            val journeySpecDecl =
                Regex(
                    """class\s+\w+\s*:\s*JourneySpec\b""",
                    setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL),
                )
            val journeySpecSplitDecl =
                Regex(
                    """class\s+\w+\s*:\s*\R\s*JourneySpec\b""",
                    setOf(RegexOption.MULTILINE),
                )
            val offenders =
                journeyFiles
                    .filterNot { file ->
                        val text = file.readText()
                        journeySpecDecl.containsMatchIn(text) || journeySpecSplitDecl.containsMatchIn(text)
                    }.map { it.relativeTo(moduleRoot).path }
            offenders.shouldBeEmpty()
        }

        test("top-level client types outside model packages are named *Api") {
            val offenders =
                kotlinFilesUnder(integrationKotlin)
                    .filter { it.path.contains("/clients/") }
                    .filterNot { it.path.contains("/model/") }
                    .flatMap { file ->
                        classNames(file)
                            .filterNot { it.endsWith("Api") }
                            .map { name -> "${file.relativeTo(moduleRoot)}::$name" }
                    }
            offenders.shouldBeEmpty()
        }

        test("WireMock stub builder files named *Stubs*.kt live under stubs packages") {
            kotlinFilesUnder(mainKotlin)
                .filter { it.name.contains("Stubs") }
                .filterNot { it.path.contains("/stubs/") }
                .map { it.relativeTo(moduleRoot).path }
                .shouldBeEmpty()
        }

        test("package segments under clients and journeys are lowercase") {
            val packages =
                kotlinFilesUnder(integrationKotlin)
                    .mapNotNull { packageName(it) }
                    .filter { it.startsWith(clientsPrefix) || it.startsWith(journeysPrefix) }
                    .toSet()
            packages
                .filter { pkg ->
                    pkg
                        .removePrefix(rootPackage)
                        .split('.')
                        .filter { it.isNotEmpty() }
                        .any { segment -> segment.any(Char::isUpperCase) }
                }.shouldBeEmpty()
        }

        test("journey file primary classes are named *Spec") {
            val offenders =
                journeyFiles
                    .mapNotNull { file ->
                        val expected = file.nameWithoutExtension
                        if (!expected.endsWith("Spec")) {
                            return@mapNotNull file.relativeTo(moduleRoot).path
                        }
                        val names = classNames(file)
                        if (expected !in names) {
                            "${file.relativeTo(moduleRoot)} missing class $expected (found $names)"
                        } else {
                            null
                        }
                    }
            offenders.shouldBeEmpty()
        }

        test("shared service keys align between clients and journeys") {
            val journeyOnlyKeys = setOf("pages")
            val clientKeys =
                File(integrationKotlin, "$rootPackage/clients".replace('.', '/'))
                    .listFiles()
                    ?.filter { it.isDirectory }
                    ?.map { it.name }
                    ?.toSet()
                    .orEmpty()
            val journeyKeys =
                File(integrationKotlin, "$rootPackage/journeys".replace('.', '/'))
                    .listFiles()
                    ?.filter { it.isDirectory }
                    ?.map { it.name }
                    ?.toSet()
                    .orEmpty()

            (journeyKeys - journeyOnlyKeys - clientKeys).shouldBeEmpty()
        }

        test("Konsist can parse integrationTest sources") {
            Konsist
                .scopeFromDirectory("src/integrationTest/kotlin")
                .files
                .assertTrue { it.path.contains("src/integrationTest/kotlin") || it.path.contains("integrationTest") }
        }

        test("journeys must not import framework") {
            filesImporting(journeyFiles, listOf(frameworkPrefix)).shouldBeEmpty()
        }

        test("journeys must not use raw Ktor client request APIs") {
            val offenders =
                journeyFiles
                    .flatMap { file ->
                        importNames(file)
                            .filter {
                                it.startsWith("io.ktor.client.request") ||
                                    it.startsWith("io.ktor.client.statement") ||
                                    it == "io.ktor.http.ContentType"
                            }.map { importName -> "${file.relativeTo(moduleRoot)} -> $importName" }
                    }
            offenders.shouldBeEmpty()
        }

        test("testkit model packages must not import WireMock admin or Ktor clients") {
            val modelFiles =
                kotlinFilesUnder(mainKotlin)
                    .filter { packageName(it)?.startsWith(testkitModelPrefix) == true }
                    .filterNot { it.path.contains("/mocks/") }
            val offenders =
                filesImporting(
                    modelFiles,
                    listOf(
                        "$rootPackage.framework.wiremock",
                        "io.ktor.client",
                        "io.ktor.http",
                    ),
                )
            offenders.shouldBeEmpty()
        }

        test("public typed client methods that hit the SUT take testId") {
            // Leading whitespace is required: client methods are class members, so anchoring at
            // column 0 matched nothing at all. Known limits, deliberately not engineered around: an
            // explicit return type is required, so `Unit`-returning and expression-bodied methods are
            // skipped, and the parameter check is a substring match. A client method missing testId
            // fails loudly at the first journey that calls it, because no stub's baggage matcher
            // will match its request.
            val methodRegex =
                Regex(
                    """^[ \t]*(?!private\b)(?:suspend\s+)?fun\s+(\w+)\s*\((.*?)\)\s*:""",
                    setOf(RegexOption.MULTILINE, RegexOption.DOT_MATCHES_ALL),
                )
            val offenders =
                kotlinFilesUnder(integrationKotlin)
                    .filter { it.name.endsWith("Api.kt") }
                    .flatMap { file ->
                        methodRegex.findAll(file.readText()).mapNotNull { match ->
                            val name = match.groupValues[1]
                            val params = match.groupValues[2]
                            if ("testId" in params) {
                                null
                            } else {
                                "${file.relativeTo(moduleRoot)}::$name"
                            }
                        }
                    }
            offenders.shouldBeEmpty()
        }

        test("scenario model types avoid endpoint-shaped names") {
            val forbiddenName =
                Regex("""(?i)(Request|Response|companySearch|getCompanies|reservationLookup)$""")
            val modelFiles =
                kotlinFilesUnder(mainKotlin)
                    .filter { packageName(it)?.startsWith(testkitModelPrefix) == true }
                    .filterNot { it.path.contains("/mocks/") }
            val offenders =
                modelFiles.flatMap { file ->
                    classNames(file)
                        .filter { forbiddenName.containsMatchIn(it) }
                        .map { name -> "${file.relativeTo(moduleRoot)}::$name" }
                }
            offenders.shouldBeEmpty()
        }
    })
