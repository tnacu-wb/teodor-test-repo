package uk.co.whitbread.company.employee.client;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
public class FeignErrorDecodeConfigTest {

    FeignErrorDecodeConfig feignErrorDecodeConfig = new FeignErrorDecodeConfig();

    @Mock
    Response.Body responseBody;

    @Test
    public void testErrorDecode_500() throws IOException {
        Request request = createPostFeignRequest();
        Response response = Response.builder()
                .request(request)
                .body(responseBody)
                .headers(emptyMap())
                .status(500)
                .build();
        when(responseBody.asInputStream())
                .thenReturn(new ByteArrayInputStream("responseBody".getBytes(UTF_8)));

        ErrorDecoder errorDecoder = feignErrorDecodeConfig.getErrorDecoder();
        Exception result = errorDecoder.decode("methodKey", response);

        assertThat("Not Null", errorDecoder, notNullValue());
        assertThat("", result, instanceOf(FeignException.class));
    }

    @Test
    public void testErrorDecode_404() throws IOException {
        Request request = createPostFeignRequest();
        Response response = Response.builder()
                .request(request)
                .body(responseBody)
                .headers(emptyMap())
                .status(400)
                .build();

        ErrorDecoder errorDecoder = feignErrorDecodeConfig.getErrorDecoder();
        Exception result = errorDecoder.decode("methodKey", response);

        assertThat("Not Null", errorDecoder, notNullValue());
        assertThat("", result, instanceOf(FeignNonServerErrorException.class));
    }

    @Test
    public void testErrorDecode_IOException() throws IOException {
        Request request = createPostFeignRequest();
        Response response = Response.builder()
                .request(request)
                .body(responseBody)
                .headers(emptyMap())
                .status(400)
                .build();

        ErrorDecoder errorDecoder = feignErrorDecodeConfig.getErrorDecoder();
        Exception result = errorDecoder.decode("methodKey", response);

        assertThat("Not Null", errorDecoder, notNullValue());
        assertThat("", result, instanceOf(FeignNonServerErrorException.class));
    }

    private Request createPostFeignRequest() {
        return Request.create(POST, "url", emptyMap(), "requestBody".getBytes(UTF_8), UTF_8);
    }
}
