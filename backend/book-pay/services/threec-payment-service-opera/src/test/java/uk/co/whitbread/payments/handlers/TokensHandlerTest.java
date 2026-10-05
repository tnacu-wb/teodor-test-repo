package uk.co.whitbread.payments.handlers;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.model.CreateTokenRequest;
import uk.co.whitbread.payments.model.CreateTokenResponse;
import uk.co.whitbread.payments.model.UpdateTokenRequest;
import uk.co.whitbread.payments.model.UpdateTokenResponse;
import uk.co.whitbread.payments.service.TokensService;
import uk.co.whitbread.payments.service.impl.DefaultValidationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokensHandlerTest {

    @Mock
    private TokensService tokensService;

    @Mock
    private DefaultValidationService defaultValidationService;

    @InjectMocks
    private TokensHandler tokensHandler;

    public TokensHandlerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTokenReturnsOkResponseForValidRequest() {
        CreateTokenRequest createTokenRequest = mock(CreateTokenRequest.class);
        CreateTokenResponse createTokenResponse = mock(CreateTokenResponse.class);

        doNothing().when(defaultValidationService).validate(createTokenRequest);
        when(tokensService.createToken(createTokenRequest)).thenReturn(Mono.just(createTokenResponse));

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(CreateTokenRequest.class)).thenReturn(Mono.just(createTokenRequest));

        var response = tokensHandler.createToken(serverRequest).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
        verify(tokensService).createToken(createTokenRequest);
    }

    @Test
    void createTokenThrowsExceptionForInvalidRequest() {
        CreateTokenRequest createTokenRequest = mock(CreateTokenRequest.class);

        doThrow(new RuntimeException("Validation failed")).when(defaultValidationService).validate(createTokenRequest);

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(CreateTokenRequest.class)).thenReturn(Mono.just(createTokenRequest));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                tokensHandler.createToken(serverRequest).block()
        );

        assertEquals("Validation failed", exception.getMessage());
    }

    @Test
    void updateTokenReturnsOkResponseForValidRequest() {
        UpdateTokenRequest updateTokenRequest = mock(UpdateTokenRequest.class);
        UpdateTokenResponse updateTokenResponse = mock(UpdateTokenResponse.class);

        doNothing().when(defaultValidationService).validate(updateTokenRequest);
        when(tokensService.updateToken(updateTokenRequest)).thenReturn(Mono.just(updateTokenResponse));

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(UpdateTokenRequest.class)).thenReturn(Mono.just(updateTokenRequest));

        var response = tokensHandler.updateToken(serverRequest).block();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.statusCode());
        verify(tokensService).updateToken(updateTokenRequest);
    }

    @Test
    void updateTokenThrowsExceptionForInvalidRequest() {
        UpdateTokenRequest updateTokenRequest = mock(UpdateTokenRequest.class);

        doThrow(new RuntimeException("Validation failed")).when(defaultValidationService).validate(updateTokenRequest);

        ServerRequest serverRequest = mock(ServerRequest.class);
        when(serverRequest.bodyToMono(UpdateTokenRequest.class)).thenReturn(Mono.just(updateTokenRequest));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                tokensHandler.updateToken(serverRequest).block());

        assertEquals("Validation failed", exception.getMessage());
    }
}