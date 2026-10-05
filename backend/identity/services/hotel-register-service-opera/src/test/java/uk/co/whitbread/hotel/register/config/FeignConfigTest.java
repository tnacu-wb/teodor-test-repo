package uk.co.whitbread.hotel.register.config;

import feign.codec.ErrorDecoder;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.hotel.register.exceptions.client.CustomErrorDecoder;

class FeignConfigTest {

    @Test
    void errorDecoder_returnsCustomErrorDecoder() {
        // Arrange
        FeignConfig feignConfig = new FeignConfig();

        // Act
        ErrorDecoder errorDecoder = feignConfig.errorDecoder();

        // Assert
        assertInstanceOf(CustomErrorDecoder.class, errorDecoder);
    }
}