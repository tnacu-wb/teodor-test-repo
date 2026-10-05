package uk.co.whitbread.hotel.register.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import java.nio.charset.StandardCharsets;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

final class ContractVerifierWireMockSupport {

    private ContractVerifierWireMockSupport() {
    }

    static WireMockServer createServer() {
        return new WireMockServer(WireMockSpring.options().port(0));
    }

    static void ensureRunning(WireMockServer wireMockServer) {
        if (!wireMockServer.isRunning()) {
            wireMockServer.start();
            loadStubMappings(wireMockServer);
        }
    }

    static void resetRequests(WireMockServer wireMockServer) {
        wireMockServer.resetRequests();
    }

    static void stop(WireMockServer wireMockServer) {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private static void loadStubMappings(WireMockServer wireMockServer) {
        try {
            var resolver = new PathMatchingResourcePatternResolver();
            var resources = resolver.getResources("classpath:stubs/*.json");
            for (var resource : resources) {
                String json = resource.getContentAsString(StandardCharsets.UTF_8);
                wireMockServer.addStubMapping(StubMapping.buildFrom(json));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load WireMock stubs", e);
        }
    }
}
