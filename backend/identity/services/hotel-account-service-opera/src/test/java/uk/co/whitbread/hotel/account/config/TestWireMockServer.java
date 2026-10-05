package uk.co.whitbread.hotel.account.config;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestWireMockServer {

    private static final WireMockServer WIRE_MOCK_SERVER = startServer();

    private TestWireMockServer() {
    }

    public static void start() {
        WIRE_MOCK_SERVER.port();
    }

    private static WireMockServer startServer() {
        WireMockServer wireMockServer = new WireMockServer(options().dynamicPort());
        wireMockServer.start();
        System.setProperty("wiremock.server.port", String.valueOf(wireMockServer.port()));
        loadStubs(wireMockServer);
        Runtime.getRuntime().addShutdownHook(new Thread(wireMockServer::stop));
        return wireMockServer;
    }

    private static void loadStubs(WireMockServer wireMockServer) {
        URL stubsUrl = TestWireMockServer.class.getClassLoader().getResource("stubs");
        if (stubsUrl == null) {
            return;
        }

        try (var stubFiles = Files.walk(Path.of(stubsUrl.toURI()))) {
            stubFiles
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(path -> loadStub(wireMockServer, path));
        } catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Unable to load WireMock stubs", e);
        }
    }

    private static void loadStub(WireMockServer wireMockServer, Path path) {
        try {
            wireMockServer.addStubMapping(StubMapping.buildFrom(Files.readString(path)));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load WireMock stub " + path, e);
        }
    }
}