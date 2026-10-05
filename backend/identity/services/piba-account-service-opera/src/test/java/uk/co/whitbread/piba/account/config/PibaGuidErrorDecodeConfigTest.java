package uk.co.whitbread.piba.account.config;

import feign.FeignException;
import feign.Request;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.piba.account.PibaAccountServiceApplication;
import uk.co.whitbread.piba.account.exception.InValidTokenException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static feign.Request.HttpMethod.POST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { PibaAccountServiceApplication.class })
public class PibaGuidErrorDecodeConfigTest {
    
    public static final String NOT_NULL = "Not Null";
    public static final String METHOD_KEY = "methodkey";
    public static final String EMPTY = "";
    PibaGuidErrorDecodeConfig errorDecodeConfig = new PibaGuidErrorDecodeConfig();

    @MockitoBean
    private CacheManager cacheManager;

    @Mock
    Response.Body responseBody;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testErrorDecode_500() throws IOException {
        Request request = createDummyFeignRequest();
        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(500).build();
        when(responseBody.asInputStream()).thenReturn(new ByteArrayInputStream("bodytext".getBytes(UTF_8)));
        ErrorDecoder errorDecoder = errorDecodeConfig.getErrorDecoder();
        Exception result = errorDecoder.decode(METHOD_KEY, response);

        assertThat(NOT_NULL, errorDecoder, notNullValue());
        assertThat(EMPTY, result, instanceOf(FeignException.class));
    }
    
    @Test
     void testErrorDecode_404() {
        Request request = createDummyFeignRequest();
        Response response = Response.builder().request(request).body(responseBody).headers(emptyMap()).status(400).build();
        ErrorDecoder errorDecoder = errorDecodeConfig.getErrorDecoder();
        Exception result = errorDecoder.decode(METHOD_KEY, response);

        assertThat(NOT_NULL, errorDecoder, notNullValue());
        assertThat(EMPTY, result, instanceOf(InValidTokenException.class));
    }
    
    private Request createDummyFeignRequest() {
        return Request.create(POST, "url", emptyMap(), "requestBody".getBytes(UTF_8), UTF_8);
    }
}
