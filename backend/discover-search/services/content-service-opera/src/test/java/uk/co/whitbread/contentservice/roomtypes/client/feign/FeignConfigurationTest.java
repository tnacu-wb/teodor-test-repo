package uk.co.whitbread.contentservice.roomtypes.client.feign;

import uk.co.whitbread.contentservice.roomtypes.exception.FeignNonServerException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignClientProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static feign.Request.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeignConfigurationTest {
    @Mock
    private FeignProperties feignProperties;

    FeignConfiguration feignConfiguration = new FeignConfiguration(feignProperties);

    @InjectMocks
    private FeignConfiguration target;

    @Mock
    private FeignClientProperties aemFeignClientProperties;

    @Mock
    Response.Body responseBody;

    @Test
    public void basicAuthRequestInterceptor() {

        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);
        when(aemFeignClientProperties.getUsername())
                .thenReturn("user");
        when(aemFeignClientProperties.getPassword())
                .thenReturn("pass");

        final BasicAuthRequestInterceptor basicAuthRequestInterceptor = target.basicAuthRequestInterceptor();
        Assertions.assertThat(basicAuthRequestInterceptor).isNotNull();
    }

    @Test
    public void basicAuthRequestInterceptorShouldReturnErrorWhenNoPasswordIsGiven() {

        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);
        when(aemFeignClientProperties.getUsername())
                .thenReturn("user");

        assertThrows(Exception.class, () -> target.basicAuthRequestInterceptor(), "password");
    }

    @Test
    public void testErrorDecode_500() throws IOException {
        Request request = createDummyFeignRequest();
        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(500).build();
        when(responseBody.asInputStream()).thenReturn(new ByteArrayInputStream("bodytext".getBytes(UTF_8)));
        ErrorDecoder errorDecoder = feignConfiguration.getErrorDecoder();

        assertThat("Not Null", errorDecoder, notNullValue());
        Exception result = errorDecoder.decode("methodkey", response);
        assertThat("", result, instanceOf(FeignException.class));
    }

    private Request createDummyFeignRequest() {
        Request request = Request.create(POST, "url", emptyMap(), "requestBody".getBytes(UTF_8), UTF_8);
        return request;
    }

    @Test
    public void testErrorDecode_404() throws IOException {
        Request request = createDummyFeignRequest();
        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(400).build();
        ErrorDecoder errorDecoder = feignConfiguration.getErrorDecoder();
        assertThat("Not Null", errorDecoder, notNullValue());

        Exception result = errorDecoder.decode("methodkey", response);

        assertThat("", result, instanceOf(FeignNonServerException.class));
    }

    @Test
    public void testErrorDecode_IOException() throws IOException {
        Request request = createDummyFeignRequest();
        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(400).build();
        ErrorDecoder errorDecoder = feignConfiguration.getErrorDecoder();
        assertThat("Not Null", errorDecoder, notNullValue());

        Exception result = errorDecoder.decode("methodkey", response);

        assertThat("", result, instanceOf(FeignNonServerException.class));
    }
}