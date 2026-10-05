package uk.co.whitbread.business.tether.config;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.business.tether.exception.PibaGuidException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static feign.Request.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PibaGuidErrorDecodeConfigTest {
    
    public static final String NOT_NULL = "Not Null";
    
    public static final String METHOD_KEY = "methodkey";
    
    public static final String EMPTY = "";
    
    PibaGuidErrorDecodeConfig errorDecodeConfig = new PibaGuidErrorDecodeConfig();

    @Mock
    Response.Body responseBody;

    @Test
    public void testErrorDecode_500() throws IOException {

        Request request = createDummyFeignRequest();

        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(500).build();

        when(responseBody.asInputStream()).thenReturn(new ByteArrayInputStream("bodytext".getBytes(UTF_8)));

        ErrorDecoder errorDecoder = errorDecodeConfig.getErrorDecoder();
        
        Exception result = errorDecoder.decode(METHOD_KEY, response);

        assertThat(NOT_NULL, errorDecoder, notNullValue());

        assertThat(EMPTY, result, instanceOf(FeignException.class));

    }
    
    @Test
    public void testErrorDecode_404() throws IOException {

        Request request = createDummyFeignRequest();

        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(400).build();

        ErrorDecoder errorDecoder = errorDecodeConfig.getErrorDecoder();
    
        Exception result = errorDecoder.decode(METHOD_KEY, response);

        assertThat(NOT_NULL, errorDecoder, notNullValue());

        assertThat(EMPTY, result, instanceOf(PibaGuidException.class));

    }

    @Test
    public void testErrorDecode_IOException() throws IOException {

        Request request = createDummyFeignRequest();

        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(400).build();

        ErrorDecoder errorDecoder = errorDecodeConfig.getErrorDecoder();
    
        Exception result = errorDecoder.decode(METHOD_KEY, response);

        assertThat(NOT_NULL, errorDecoder, notNullValue());

        assertThat(EMPTY, result, instanceOf(PibaGuidException.class));

    }
    
    private Request createDummyFeignRequest() {
        
        return Request.create(POST, "url", emptyMap(), "requestBody".getBytes(UTF_8), UTF_8);
    }

}
