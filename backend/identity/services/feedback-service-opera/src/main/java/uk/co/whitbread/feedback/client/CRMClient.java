package uk.co.whitbread.feedback.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.co.whitbread.feedback.client.utils.HeadersBuilder;
import uk.co.whitbread.feedback.model.Feedback;
import uk.co.whitbread.feedback.properties.AuthProperties;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CRMClient {

    private static final String CUSTOMERID_CONTACT_PROPERTY = "customerid_contact@odata.bind";

    private static final String ENTITY_ID_HEADER = "OData-EntityId";
    private static final String TICKETNUMBER_FIELD = "ticketnumber";

    private final AuthProperties authProperties;
    private final HeadersBuilder headersBuilder;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    public String postFeedback(String token, Feedback feedback) {

        String feedbackWithContactType = getFeedbackWithContactType(feedback);

        log(feedbackWithContactType, "Feedback");
        
        HttpEntity<String> response = restTemplate.exchange(authProperties.getIncidentsUrl(), HttpMethod.POST,
                                                            buildHttpEntity(feedbackWithContactType, token),
                                                            String.class);


        if(!response.getHeaders().containsHeader(ENTITY_ID_HEADER)) {
            throw new RuntimeException(String.format("Response does not contain an '%s' header", ENTITY_ID_HEADER));
        }

        String feedbackGuid = StringUtils.substringBetween(response.getHeaders().get(ENTITY_ID_HEADER).get(0),"(", ")");

        log(response, "Feedback created: "+feedbackGuid);

        return feedbackGuid;
    }

    public String retrieveCaseId(String token, String feedbackGuid) {

        HttpEntity<Map> response = restTemplate.exchange(authProperties.getCaseidUrl(), HttpMethod.GET,
                                                            buildHttpEntity(null, token),
                                                            Map.class, feedbackGuid);

        log(response, "Ticket Number");

        if(!response.getBody().containsKey(TICKETNUMBER_FIELD)) {
            throw new RuntimeException(String.format("Response does not contain a '%s' field", TICKETNUMBER_FIELD));
        }

        return (String) response.getBody().get(TICKETNUMBER_FIELD);

    }

    String getFeedbackWithContactType(Feedback feedback) {

        String feedbackWithContactType = null;

        try {

            String feedbackAsJsonString = objectMapper.writeValueAsString(feedback);
            Map map = objectMapper.readValue(feedbackAsJsonString, Map.class);
            map.put(CUSTOMERID_CONTACT_PROPERTY, authProperties.getIncidentsContactType());
            feedbackWithContactType = objectMapper.writeValueAsString(map);

        } catch (IOException e) {
            log.error("Error hacking feedback object to add customerid contact field (it should not happen)", e);
        }

        return feedbackWithContactType;
    }

    private <T> HttpEntity<T> buildHttpEntity(T request, String token) {
        return new HttpEntity<>(request, headersBuilder.buildHeaders(token));
    }


    private void log(Object object, String name) {
        if (log.isDebugEnabled()) {
            try {
                log.debug("{}: [{}]", name, objectMapper.writeValueAsString(object));
            } catch (JsonProcessingException e) {
                log.error("Unable to log {}", name, e);
            }
        }
    }
}
