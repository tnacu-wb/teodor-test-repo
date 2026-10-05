package uk.co.whitbread.feedback.client.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import uk.co.whitbread.feedback.properties.AuthProperties;

import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class HeadersBuilder {

    private final AuthProperties authProperties;

    public HttpHeaders buildHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(APPLICATION_JSON));
        headers.setContentType(APPLICATION_JSON);
        headers.set("OData-MaxVersion", authProperties.getCrmApiVersion());
        headers.set("OData-Version", authProperties.getCrmApiVersion());
        headers.set("Authorization", "Bearer " + token);

        return headers;
    }
}
