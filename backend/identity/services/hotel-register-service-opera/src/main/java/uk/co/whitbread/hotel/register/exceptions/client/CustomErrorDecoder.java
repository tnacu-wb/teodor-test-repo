package uk.co.whitbread.hotel.register.exceptions.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Error occurred while calling {}: status {}, reason {}", methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 400 -> new CdhServiceException("Bad Request");
            case 404 -> new CdhServiceException("Record Not Found");
            case 500 -> new CdhServiceException("Internal Server Error");
            default -> defaultErrorDecoder.decode(methodKey, response);
        };
    }
}