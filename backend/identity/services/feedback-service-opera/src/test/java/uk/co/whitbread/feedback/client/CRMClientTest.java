package uk.co.whitbread.feedback.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.feedback.model.Feedback;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CRMClientTest {

    private static final String TOKEN = "test-token";
    private static final String ENTITY_ID = "test-entity-id";
    private static final String ODATA_ENTITY_ID_HEADER = "OData-EntityId";

    @Autowired
    private CRMClient crmClient;

    @MockitoBean
    private RestTemplate restTemplateMock;

    @Test
    void shouldGetFeedbackWithContactType() {
        //Given
        Feedback feedback = createMockedFeedback();

        //When
        String feedbackWithContactType = crmClient.getFeedbackWithContactType(feedback);

        //Then
        assertNotNull(feedbackWithContactType);
        assertTrue(feedbackWithContactType.contains("customerid_contact@odata.bind"));
    }

    @Test
    void shouldGetFeedbackWithSleepAndReported() {
        //Given
        Feedback feedback = createMockedFeedback();

        //When
        String feedbackWithContactType = crmClient.getFeedbackWithContactType(feedback);

        //Then
        assertNotNull(feedbackWithContactType);
        assertTrue(feedbackWithContactType.contains("whb_wfsleep"));
        assertTrue(feedbackWithContactType.contains("whb_wfreported"));
    }

    @Test
    void shouldPostFeedbackWithValidResponseAndReturnEntityId() {
        //Given
        Feedback feedback = createMockedFeedback();

        ResponseEntity<String> mockResponse = createPostFeedbackResponse(true);
        when(restTemplateMock.exchange(anyString(), eq(POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        //When
        String result = crmClient.postFeedback(TOKEN, feedback);

        //Then
        assertNotNull(result);
        assertEquals(ENTITY_ID, result);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenEntityIdHeaderIsMissing() {
        //Given
        Feedback feedback = createMockedFeedback();

        ResponseEntity<String> mockResponse = createPostFeedbackResponse(false);
        when(restTemplateMock.exchange(anyString(), eq(POST), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        //When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> crmClient.postFeedback(TOKEN, feedback));

        assertTrue(exception.getMessage().contains("Response does not contain an 'OData-EntityId' header"));
    }

    private Feedback createMockedFeedback() {
        return new Feedback("source", "Mr", "WHDB2013", "111111111", "01/01/0001", "test@test.test", "reason", "summary", 1, "first", "last", "EC1N2TD", "hotel", true, 1, 1, "type-of-visit", "loyalty-card-number", "check-number", "01/01/0001",true,true);
    }

    private ResponseEntity<String> createPostFeedbackResponse(boolean withEntityIdHeader) {
        HttpHeaders headers = new HttpHeaders();
        if (withEntityIdHeader) {
            headers.put(ODATA_ENTITY_ID_HEADER, List.of("https://example.com(" + ENTITY_ID + ")"));
        }
        return new ResponseEntity<>(null, headers, HttpStatus.OK);
    }
}