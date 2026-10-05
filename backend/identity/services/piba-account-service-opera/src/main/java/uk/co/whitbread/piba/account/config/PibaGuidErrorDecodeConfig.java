package uk.co.whitbread.piba.account.config;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import uk.co.whitbread.piba.account.exception.InValidTokenException;

import java.io.IOException;

@Slf4j
public class PibaGuidErrorDecodeConfig {

    private static final String HTTP_STATUS_RECEIVED_MESSAGE = "Status %d received , content: %s";

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Bean
    ErrorDecoder getErrorDecoder() {
        return (s, response) -> {

            // Do not open the circuit breaker if not a 5xx
            if (!HttpStatus.valueOf(response.status()).is5xxServerError()) {
                return new InValidTokenException(String.format(HTTP_STATUS_RECEIVED_MESSAGE, response.status(), extractContent(response)));
            }

            return defaultErrorDecoder.decode(s, response);
        };
    }

    private String extractContent(Response response) {
        String content = "";

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
