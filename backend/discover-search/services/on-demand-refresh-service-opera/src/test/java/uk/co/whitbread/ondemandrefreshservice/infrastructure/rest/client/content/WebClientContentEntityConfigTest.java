package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.content;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.config.WebClientContentEntityConfig;

class WebClientContentEntityConfigTest {

    @Test
    void contentEntityWebClient_shouldBeCreated() {
        // Arrange
        WebClientContentEntityConfig config = new WebClientContentEntityConfig();

        ReflectionTestUtils.setField(config, "contentEntityBaseOrigin", "http://dummy-url.com");
        WebClient.Builder webClientBuilder = WebClient.builder();

        // Act
        WebClient webClient = config.contentEntityWebClient(webClientBuilder);

        // Assert
        assertNotNull(webClient, "WebClient bean should be created");
    }
}
