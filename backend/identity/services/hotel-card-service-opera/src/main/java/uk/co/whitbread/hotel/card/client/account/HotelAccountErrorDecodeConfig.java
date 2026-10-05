package uk.co.whitbread.hotel.card.client.account;

import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

@Slf4j
public class HotelAccountErrorDecodeConfig {

    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Bean
    ErrorDecoder getErrorDecoder() {
        return (methodKey, response) -> {
            HttpStatus status = HttpStatus.valueOf(response.status());
            
            // For 4xx client errors, return a non-retryable exception
            // This prevents the circuit breaker from opening for client errors
            if (status.is4xxClientError()) {
                log.debug("Client error {} received for {}, not triggering circuit breaker", 
                    response.status(), methodKey);
                return new feign.FeignException.FeignClientException(
                    response.status(), 
                    String.format("Client error %d for %s", response.status(), methodKey),
                    response.request(),
                    null,
                    response.headers()
                );
            }
            
            // For 5xx server errors, use default decoder which will trigger circuit breaker
            if (status.is5xxServerError()) {
                log.warn("Server error {} received for {}, may trigger circuit breaker", 
                    response.status(), methodKey);
            }
            
            return defaultErrorDecoder.decode(methodKey, response);
        };
    }
}
