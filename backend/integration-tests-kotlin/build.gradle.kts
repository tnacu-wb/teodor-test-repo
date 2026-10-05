plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.serialization") version "2.4.20"
    application
    id("io.ktor.plugin") version "3.5.1"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

repositories {
    mavenCentral()
}

val ktorVersion = "3.5.1"

val integrationTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += output + compileClasspath
}

dependencies {
    // Kotest
    testImplementation("io.kotest:kotest-runner-junit5:6.2.2")
    testImplementation("io.kotest:kotest-assertions-core:6.2.2")

    // Reusable provisioning HTTP client
    implementation("io.ktor:ktor-client-java:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    // Local mock-provisioning server and generated API documentation
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-routing-openapi:$ktorVersion")
    implementation("io.ktor:ktor-server-swagger:$ktorVersion")

    // Test-only deterministic HTTP transport
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")

    // Architecture / package-structure rules (Docker-free)
    testImplementation("com.lemonappdev:konsist:0.17.3")

    // Reusable provisioning serialization and coroutine runtime
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    // JWT signing and JWKS generation for authenticated integration-test requests.
    implementation("com.nimbusds:nimbus-jose-jwt:10.9.1")

    // Ktor brings slf4j-api; provide a minimal binding that surfaces
    // WARN/ERROR while keeping INFO/DEBUG chatter quiet.
    testRuntimeOnly("org.slf4j:slf4j-simple:2.0.16")
    runtimeOnly("org.slf4j:slf4j-simple:2.0.16")
}

application {
    mainClass.set("uk.co.whitbread.integrationtests.provisioning.MockProvisioningApplicationKt")
}

configurations[integrationTest.implementationConfigurationName]
    .extendsFrom(configurations.testImplementation.get())
configurations[integrationTest.runtimeOnlyConfigurationName]
    .extendsFrom(configurations.testRuntimeOnly.get())

kotlin {
    target.compilations
        .getByName(integrationTest.name)
        .associateWith(target.compilations.getByName("main"))
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "25"
    targetCompatibility = "25"
}

// Capture the provider during configuration so test execution does not access Task.project.
val scenarioEvidenceDirectory = layout.buildDirectory.dir("test-evidence")

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn")
    testLogging {
        showStandardStreams = true
    }
    reports {
        junitXml.apply {
            isOutputPerTestCase = true
            includeSystemOutLog = true
            includeSystemErrLog = true
        }
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs environment-backed integration journeys."
    group = "verification"
    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    shouldRunAfter(tasks.test)
    doNotTrackState("Integration tests depend on externally managed environment state")
    systemProperty(
        "kotest.framework.config.fqn",
        "uk.co.whitbread.integrationtests.framework.config.IntegrationTestConfig",
    )
    doFirst("clear scenario evidence from previous executions") {
        check(scenarioEvidenceDirectory.get().asFile.deleteRecursively()) {
            "Could not clear ${scenarioEvidenceDirectory.get().asFile} before integrationTest execution"
        }
    }
}

tasks.named("check") {
    dependsOn(integrationTest.classesTaskName)
}
