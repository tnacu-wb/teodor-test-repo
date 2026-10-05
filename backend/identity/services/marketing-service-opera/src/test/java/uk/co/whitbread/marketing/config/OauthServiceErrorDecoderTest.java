package uk.co.whitbread.marketing.config;

import feign.Request;
import feign.Response;
import org.apache.http.entity.ContentType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.exception.OauthClientException;
import uk.co.whitbread.marketing.utils.TestObjectMapperFactory;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class OauthServiceErrorDecoderTest {

    @InjectMocks
    private OauthServiceErrorDecoder target;

  @BeforeEach
    void setup() {
    JsonMapper objectMapper = TestObjectMapperFactory.create();
        ReflectionTestUtils.setField(target, "objectMapper", objectMapper);
    }

    @Test
    void shouldReturn401() {
        String json = "{\"error\":\"invalid_client\",\"error_description\":\"AADSTS7000215: Invalid client secret is provided.\\r\\nTrace ID: 3468e59c-599d-4056-b31c-a93e2b2cfa00\\r\\nCorrelation ID: 59f45791-01cc-49db-a127-30aae4c40a64\\r\\nTimestamp: 2020-06-23 15:26:48Z\",\"error_codes\":[7000215],\"timestamp\":\"2020-06-23 15:26:48Z\",\"trace_id\":\"3468e59c-599d-4056-b31c-a93e2b2cfa00\",\"correlation_id\":\"59f45791-01cc-49db-a127-30aae4c40a64\",\"error_uri\":\"https://login.microsoftonline.com/error?code=7000215\"}";

        Request request = getRequest();

        Map<String, Collection<String>> headersResponse = new HashMap<>();
        headersResponse.put("Content-Type", Collections.singleton(ContentType.APPLICATION_JSON.toString()));
        final Response response = Response.builder()
                .body(json, Charset.defaultCharset())
                .status(401)
                .reason("UNAUTHORIZED")
                .headers(headersResponse)
                .request(request)
                .build();
        OauthClientException expectedException = new OauthClientException(401,"invalid_client","3002");

        final Exception decode = target.decode("Feign error 401", response);
        Assertions.assertThat(decode).isEqualTo(expectedException);
    }

    @Test
    void shouldReturnJsonParseException() {
        String json = "\"error\":\"invalid_client\",\"error_description\":\"AADSTS7000215: Invalid client secret is provided.\\r\\nTrace ID: 3468e59c-599d-4056-b31c-a93e2b2cfa00\\r\\nCorrelation ID: 59f45791-01cc-49db-a127-30aae4c40a64\\r\\nTimestamp: 2020-06-23 15:26:48Z\",\"error_codes\":[7000215],\"timestamp\":\"2020-06-23 15:26:48Z\",\"trace_id\":\"3468e59c-599d-4056-b31c-a93e2b2cfa00\",\"correlation_id\":\"59f45791-01cc-49db-a127-30aae4c40a64\",\"error_uri\":\"https://login.microsoftonline.com/error?code=7000215\"}";

        Request request = getRequest();

        Map<String, Collection<String>> headersResponse = new HashMap<>();
        headersResponse.put("Content-Type", Collections.singleton(ContentType.APPLICATION_JSON.toString()));
        final Response response = Response.builder()
                .body(json, Charset.defaultCharset())
                .status(401)
                .reason("UNAUTHORIZED")
                .headers(headersResponse)
                .request(request)
                .build();
        OauthClientException expectedException =
                new OauthClientException(401,"Error parsing Exception coming from service Feign error 401","3002");

        final Exception decode = target.decode("Feign error 401", response);
        Assertions.assertThat(decode).isEqualTo(expectedException);
    }

    @Test
    void shouldReturnUnknownException() {
        String json = "{\"error\":\"invalid_client\",\"error_description\":\"AADSTS7000215: Invalid client secret is provided.\\r\\nTrace ID: 3468e59c-599d-4056-b31c-a93e2b2cfa00\\r\\nCorrelation ID: 59f45791-01cc-49db-a127-30aae4c40a64\\r\\nTimestamp: 2020-06-23 15:26:48Z\",\"error_codes\":[7000215],\"timestamp\":\"2020-06-23 15:26:48Z\",\"trace_id\":\"3468e59c-599d-4056-b31c-a93e2b2cfa00\",\"correlation_id\":\"59f45791-01cc-49db-a127-30aae4c40a64\",\"error_uri\":\"https://login.microsoftonline.com/error?code=7000215\"}";
        ReflectionTestUtils.setField(target, "objectMapper", null);

        Request request = getRequest();

        Map<String, Collection<String>> headersResponse = new HashMap<>();
        headersResponse.put("Content-Type", Collections.singleton(ContentType.APPLICATION_JSON.toString()));
        final Response response = Response.builder()
                .body(json, Charset.defaultCharset())
                .status(400)
                .reason("BAD REQUEST")
                .headers(headersResponse)
                .request(request)
                .build();
        OauthClientException expectedException =
                new OauthClientException(400,"Error while trying to extract feign client response content Feign error 400","3002");

        final Exception decode = target.decode("Feign error 400", response);
        Assertions.assertThat(decode).isEqualTo(expectedException);
    }

    private Request getRequest() {
        Map<String, Collection<String>> headers = new HashMap<>();
        headers.put("Content-Type", Collections.singleton(ContentType.APPLICATION_FORM_URLENCODED.toString()));
        return Request.create(Request.HttpMethod.POST,
                    "http://teste.com",headers,"request".getBytes(), Charset.defaultCharset());
    }

}
