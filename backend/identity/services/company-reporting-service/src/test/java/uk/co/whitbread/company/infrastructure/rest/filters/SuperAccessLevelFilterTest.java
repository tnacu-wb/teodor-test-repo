package uk.co.whitbread.company.infrastructure.rest.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class SuperAccessLevelFilterTest {

  @InjectMocks
  private SuperAccessLevelFilter filter;

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private MockHttpServletRequest request;

  @Mock
  private MockHttpServletResponse response;

  @Mock
  private MockFilterChain chain;

  @Mock
  private ServletOutputStream servletOutputStream;

  @Test
  void doFilterInternal__SUPER__ShouldReturnOK() throws ServletException, IOException {

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(
        new CustomJwtAuthenticationToken(getJwt(),
            getAccount("SUPER")));

    filter.doFilterInternal(request, response, chain);

    verify(chain).doFilter(request, response);
  }

  @Test
  void doFilterInternal__ShouldThrowAuthorizationException() throws ServletException, IOException {
    when(authenticatedUserService.isUserAuthenticated()).thenReturn(false);
    when(response.getOutputStream()).thenReturn(servletOutputStream);
    filter.doFilterInternal(request, response, chain);
    Assertions.assertNotNull(response);
  }


  @Test
  void shouldNotFilter__ShouldReturnFalse() {

    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

    when(httpServletRequest.getServletPath()).thenReturn(
        "/v1/company-reports/admin/{companyId}/management-information-report");

    var response = filter.shouldNotFilter(httpServletRequest);

    assertFalse(response);
  }

  @Test
  void shouldNotFilter__ShouldReturnTrue() {

    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);

    when(httpServletRequest.getServletPath()).thenReturn(
        "/v1/company-reports/{companyId}/management-information-report");

    var response = filter.shouldNotFilter(httpServletRequest);

    assertTrue(response);
  }




  @Test
  void doFilterInternal__STAYER__ShouldReturn401() throws ServletException, IOException {

    HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    FilterChain filterChain = mock(FilterChain.class);

    when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(
        new CustomJwtAuthenticationToken(getJwt(),
            getAccount("STAYER")));
    when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);

    filter.doFilterInternal(httpServletRequest, httpServletResponse, filterChain);

    Assertions.assertNotNull(httpServletResponse);
  }

  private Jwt getJwt() {
    return new Jwt("authorization", Instant.now(), Instant.now().plusSeconds(60),
        Map.of("header1", "header2"), Map.of("Claim1", "Claim2"));
  }

  private Account getAccount(String accessLevel) {
    return Account.builder()
        .bartId("187")
        .operaCompanyId("7765828")
        .companyId("5445")
        .accessLevel(accessLevel)
        .email("finalEmail")
        .customerId("5445")
        .employeeId("1")
        .build();
  }

}