package uk.co.whitbread.contentservice.roomtypes.client.feign;

import feign.Response;
import feign.Util;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.FeignNonServerException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class FeignConfiguration {

    private final FeignProperties feignProperties;

    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor(
                feignProperties.getAem().getUsername(),
                feignProperties.getAem().getPassword()
        );
    }

    private static final String HTTP_STATUS_RECEIVED_MESSAGE = "Status %d received , content: %s";

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Bean
    ErrorDecoder getErrorDecoder() {
        return (s, response) -> {

            // Do not open the circuit breaker if not a 5xx
            if (!HttpStatus.valueOf(response.status()).is5xxServerError()) {
            return new FeignNonServerException(String.format(HTTP_STATUS_RECEIVED_MESSAGE, response.status(), extractContent(response)));
            }

            return defaultErrorDecoder.decode(s, response);
        };
    }

    private String extractContent(Response response) {
        var content = "";
        try {
            if (response.body() != null) {
                content = Util.toString(response.body().asReader());
            }
        } catch (IOException ex) {
            // Ignore exception on body read
            log.info("Error getting response body", ex);
        }
        return content;
    }
}