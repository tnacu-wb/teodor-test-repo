package uk.co.whitbread.feedback.controller;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.co.whitbread.feedback.model.Feedback;
import uk.co.whitbread.feedback.model.FeedbackResponse;
import uk.co.whitbread.feedback.service.FeedbackService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FeedbackControllerTest {

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private FeedbackService mockFeedbackService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void shouldReturnA200SuccessWhenPayloadIsValid() throws Exception {
        //Given
        Feedback payload = createMockedFeedback();
        FeedbackResponse feedbackResponse = random(FeedbackResponse.class);

        when(mockFeedbackService.addFeedback(payload)).thenReturn(feedbackResponse);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + serverPort + "/feedback"))
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        //When
        HttpResponse<String> response = httpClient
                .send(request, HttpResponse.BodyHandlers.ofString());

        //Then
        JsonNode responseBody = objectMapper.readTree(response.body());
        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertEquals(feedbackResponse.getCaseReferenceId(), responseBody.path("caseReferenceId").asText());
    }

    private Feedback createMockedFeedback() {
        return new Feedback( "source", "Mr", "WHDB2013", "111111111", "01/01/0001", "test@test.test", "reason", "summary", 1, "first", "last", "EC1N2TD", "hotel", true, 1, 1, "type-of-visit", "loyalty-card-number", "check-number", "01/01/0001",true,true);
    }

}
