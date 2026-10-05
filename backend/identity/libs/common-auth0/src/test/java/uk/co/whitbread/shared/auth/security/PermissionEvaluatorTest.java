package uk.co.whitbread.shared.auth.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.auth.security.model.RbacRuleHasAccessResponse;
import uk.co.whitbread.shared.auth.tenant.SecurityProperties;

@ExtendWith(MockitoExtension.class)
class PermissionEvaluatorTest {

  private PermissionEvaluator permissionEvaluator;
  @Mock
  private AuthenticatedUserService authenticatedUserService;
  @Mock
  private SecurityProperties securityProperties;
  private static MockWebServer mockRulesEngine;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeAll
  static void setUp() throws IOException {
    mockRulesEngine = new MockWebServer();
    mockRulesEngine.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockRulesEngine.shutdown();

  }

  @BeforeEach
  void init() {
    String baseUrl = String.format("http://%s:%s",
        mockRulesEngine.getHostName(), mockRulesEngine.getPort());
    WebClient webClient = WebClient.create(baseUrl);
    permissionEvaluator = new PermissionEvaluator(authenticatedUserService, webClient, securityProperties);
  }

  @Test
  void hasAccess_havingRoles_shouldAllow() throws IOException {
    //Arrange
    var rbacRuleHasAccessResponse = RbacRuleHasAccessResponse.builder()
        .hasAccess(true)
        .build();
    mockRulesEngine.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(rbacRuleHasAccessResponse))
        .addHeader("Content-Type", "application/json"));
    var grantedAuthorities = List.of("ROLE_MANAGER", "ROLE_AGENT");
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(grantedAuthorities);
    when(securityProperties.getIsAllowedAccessForRoleIdsEndpoint())
        .thenReturn("/rules_engine_rbac_endpoint");
    //Act
    var hasAccess = permissionEvaluator.hasAccess("RESOURCE_1");

    //Assert
    assertTrue(hasAccess);
    verifyNoMoreInteractions(authenticatedUserService);
    verifyNoMoreInteractions(securityProperties);
  }

  @Test
  void hasAccess_havingRoles_shouldDeny() throws IOException {
    //Arrange
    var rbacRuleHasAccessResponse = RbacRuleHasAccessResponse.builder()
        .hasAccess(false)
        .build();
    mockRulesEngine.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(rbacRuleHasAccessResponse))
        .addHeader("Content-Type", "application/json"));
    var grantedAuthorities = List.of("ROLE_MANAGER", "ROLE_AGENT");
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(grantedAuthorities);
    when(securityProperties.getIsAllowedAccessForRoleIdsEndpoint())
        .thenReturn("/rules_engine_rbac_endpoint");
    //Act
    var hasAccess = permissionEvaluator.hasAccess("RESOURCE_1");

    //Assert
    assertFalse(hasAccess);
    verifyNoMoreInteractions(authenticatedUserService);
    verifyNoMoreInteractions(securityProperties);
  }

  @Test
  void hasAccess_noRoles_shouldDeny() throws IOException {
    //Arrange
    var rbacRuleHasAccessResponse = RbacRuleHasAccessResponse.builder()
        .hasAccess(false)
        .build();
    mockRulesEngine.enqueue(new MockResponse()
        .setBody(objectMapper.writeValueAsString(rbacRuleHasAccessResponse))
        .addHeader("Content-Type", "application/json"));
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(Collections.emptyList());
    when(securityProperties.getIsAllowedAccessForRoleIdsEndpoint())
        .thenReturn("/rules_engine_rbac_endpoint");
    //Act
    var hasAccess = permissionEvaluator.hasAccess("RESOURCE_1");

    //Assert
    assertFalse(hasAccess);
    verifyNoMoreInteractions(authenticatedUserService);
    verifyNoMoreInteractions(securityProperties);
  }

  @Test
  void hasAccess__shouldThrowError() {
    //Arrange
    mockRulesEngine.enqueue(
        new MockResponse().setResponseCode(500)
            .setHeader("content-type", "application/json"));
    var grantedAuthorities = List.of("ROLE_MANAGER", "ROLE_AGENT");
    when(authenticatedUserService.getAuthenticatedUserAuthorities())
        .thenReturn(grantedAuthorities);
    when(securityProperties.getIsAllowedAccessForRoleIdsEndpoint())
        .thenReturn("/rules_engine_rbac_endpoint");
    //Act

    //Assert
    assertThrows(Exception.class, () -> permissionEvaluator.hasAccess("RESOURCE_1"));
    verifyNoMoreInteractions(authenticatedUserService);
    verifyNoMoreInteractions(securityProperties);
  }
}