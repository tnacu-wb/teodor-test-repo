package uk.co.whitbread.shared.auth.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import uk.co.whitbread.shared.auth.exception.InvalidSessionException;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.properties.TokenProperties;
import uk.co.whitbread.shared.auth.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationFilterTest {
    private static final String SESSION_ID="session-id";

    private AuthenticationFilter objectUnderTest;

    @Mock
    private TokenService mockTokenService;

    @Mock
    private HttpServletRequest mockServletRequest;
    @Mock
    private HttpServletResponse mockServletResponse;
    @Mock
    private FilterChain mockFilterChain;

    @Mock
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver mockResolver;

    private TokenProperties tokenProperties = new TokenProperties();

    @Before
    public void setup(){
        List<String> urlPathRegex= Arrays.asList(".*\\/piba\\/.*");
        tokenProperties.setUrlPathRegex(urlPathRegex);
        objectUnderTest= new AuthenticationFilter(mockTokenService, tokenProperties,mockResolver);
    }

    @Test
    public  void testTokenIsValid() throws IOException, ServletException {

        when(mockServletRequest.getRequestURI()).thenReturn("/piba/registration/info/A13213");
        when(mockTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.of(SESSION_ID));
        when(mockServletRequest.getHeader("Authorization") ).thenReturn("Authorization");

        objectUnderTest.doFilter(mockServletRequest,mockServletResponse,mockFilterChain);

        verify(mockTokenService).retrieveAndVerifyToken(anyString());
        verify(mockFilterChain).doFilter(any(),any());
    }


    @Test
    public  void testPathIsNotListedForTokenValidation() throws IOException, ServletException {

        when(mockServletRequest.getRequestURI()).thenReturn("/hotel/registration/info/A13213");

        objectUnderTest.doFilter(mockServletRequest,mockServletResponse,mockFilterChain);

        verify(mockTokenService,never()).retrieveAndVerifyToken(anyString());
        verify(mockFilterChain).doFilter(any(),any());
    }

    @Test
    public  void testTokenIsNotValid() throws IOException, ServletException {

        when(mockServletRequest.getRequestURI()).thenReturn("/piba/registration/info/A13213");

        when(mockServletRequest.getHeader("Authorization") ).thenReturn("Authorization");

        when(mockTokenService.retrieveAndVerifyToken(anyString())).thenThrow(TokenVerificationException.class);
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setStatus(HttpStatus.UNAUTHORIZED);

        when(mockResolver.resolveException(any(),any(),any(),any())).thenReturn(modelAndView);
        objectUnderTest.doFilter(mockServletRequest,mockServletResponse,mockFilterChain);
        verify(mockResolver,times(1)).resolveException(any(),any(),any(),any(TokenVerificationException.class));
    }

    @Test
    public  void testTokenValidationReturnsEmptySessionIdThrowsException() throws IOException, ServletException {

        when(mockServletRequest.getRequestURI()).thenReturn("/piba/registration/info/A13213");

        when(mockServletRequest.getHeader("Authorization") ).thenReturn("Authorization");

        when(mockTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.empty());

        objectUnderTest.doFilter(mockServletRequest,mockServletResponse,mockFilterChain);
        verify(mockResolver,times(1)).resolveException(any(),any(),any(),any(InvalidSessionException.class));
    }

    @Test
    public  void testAuthorizationValueNotExistsThrowsException() throws IOException, ServletException {

        when(mockServletRequest.getRequestURI()).thenReturn("/piba/registration/info/A13213");
        when(mockServletRequest.getHeader("Authorization") ).thenReturn("");
        objectUnderTest.doFilter(mockServletRequest,mockServletResponse,mockFilterChain);
        verify(mockResolver,times(1)).resolveException(any(),any(),any(),any(InvalidSessionException.class));
    }
}
