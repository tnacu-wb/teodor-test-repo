package uk.co.whitbread.hotel.register.exceptions.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import uk.co.whitbread.hotel.register.exceptions.CdhServiceException;

class CustomErrorDecoderTest {

    @Mock
    private Response response;

    private ErrorDecoder errorDecoder;

    @BeforeEach
    void setUp() {
        try (AutoCloseable mocks = openMocks(this)) {
            errorDecoder = new CustomErrorDecoder();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize mocks", e);
        }
    }

    @Test
    void decode_badRequest() {
        // Arrange
        when(response.status()).thenReturn(400);
        when(response.reason()).thenReturn("Bad Request");

        // Act
        Exception exception = errorDecoder.decode("methodKey", response);

        // Assert
        assertInstanceOf(CdhServiceException.class, exception);
        assertEquals("Bad Request", exception.getMessage());
    }

    @Test
    void decode_notFound() {
        // Arrange
        when(response.status()).thenReturn(404);
        when(response.reason()).thenReturn("Record Not Found");

        // Act
        Exception exception = errorDecoder.decode("methodKey", response);

        // Assert
        assertInstanceOf(CdhServiceException.class, exception);
        assertEquals("Record Not Found", exception.getMessage());
    }

    @Test
    void decode_internalServerError() {
        // Arrange
        when(response.status()).thenReturn(500);
        when(response.reason()).thenReturn("Internal Server Error");

        // Act
        Exception exception = errorDecoder.decode("methodKey", response);

        // Assert
        assertInstanceOf(CdhServiceException.class, exception);
        assertEquals("Internal Server Error", exception.getMessage());
    }
}