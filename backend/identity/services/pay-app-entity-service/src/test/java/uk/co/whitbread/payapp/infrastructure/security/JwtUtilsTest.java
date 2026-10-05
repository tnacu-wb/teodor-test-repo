package uk.co.whitbread.payapp.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.payapp.infrastructure.rest.client.payapp.exceptions.InvalidTokenException;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationParticipant;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

  private static final Integer BART_EMPLOYEE_ID = 1;

  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @InjectMocks
  private JwtUtils jwtUtils;

  @Test
  void parseJwtToken__shouldSucceed() {
    // Arrange
    var account = Account.builder()
        .bartEmployeeId("employeeId")
        .bartId("companyId")
        .email("email")
        .build();
    var jwt = mock(Jwt.class); // Mock the Jwt object
    var authenticatedUser = new CustomJwtAuthenticationToken(jwt, null, account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(authenticatedUser);

    // Act
    var jwtToken = jwtUtils.parseToken();

    // Assert
    assertNotNull(jwtToken);
    assertEquals("employeeId", jwtToken.getEmployeeId());
    assertEquals("companyId", jwtToken.getCompanyId());
    assertEquals("email", jwtToken.getEmail());
  }

  @ParameterizedTest
  @MethodSource("provideInvalidAccounts")
  void parseJwtToken__WhenInvalidAccount__shouldThrowError(Account account) {
    // Arrange
    var jwt = mock(Jwt.class);
    var authenticatedUser = new CustomJwtAuthenticationToken(jwt, null, account);
    when(authenticatedUserService.getAuthenticatedUser()).thenReturn(authenticatedUser);

    // Act & Assert
    assertThrows(InvalidTokenException.class, () -> jwtUtils.parseToken());
  }


  @Test
  void hasRights_ForTravelManagerRole_shouldReturnTrue() {
    // Arrange
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(createCustomJwtAuthenticationToken("SUPER"));

    // Act
    var hasRights = jwtUtils.hasRights(createApplicationResponseWithOtherParticipants());

    // Assert
    assertTrue(hasRights);
  }

  @Test
  void hasRights_ForBookerRole_shouldReturnTrue() {
    // Arrange
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(createCustomJwtAuthenticationToken("BOOKER"));

    // Act
    var hasRights = jwtUtils.hasRights(createApplicationResponseWithOtherParticipants());

    // Assert
    assertTrue(hasRights);
  }


  @Test
  void hasRights_ForStayerRole_shouldReturnFalse() {
    // Arrange
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(createCustomJwtAuthenticationToken("STAYER"));

    // Act
    var hasRights = jwtUtils.hasRights(createApplicationResponseWithOtherParticipants());

    // Assert
    assertFalse(hasRights);
  }


  @Test
  void hasRights_ForSelfRoleIncludedInParticipants_shouldReturnTrue() {
    // Arrange
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(createCustomJwtAuthenticationToken("SELF"));

    // Act
    var hasRights = jwtUtils.hasRights(createApplicationResponseWhitParticipants());

    // Assert
    assertTrue(hasRights);
  }

  ApplicationResponse createApplicationResponseWhitParticipants() {
    return ApplicationResponse.builder()
        .participants(List.of(
            ApplicationParticipant.builder().participantId(BART_EMPLOYEE_ID).build()
        ))
        .companyId(1)
        .build();
  }


  ApplicationResponse createApplicationResponseWithOtherParticipants() {
    return ApplicationResponse.builder()
        .participants(List.of(
            ApplicationParticipant.builder().participantId(3).build()
        ))
        .companyId(1)
        .build();
  }

  static Stream<Account> provideInvalidAccounts() {
    return Stream.of(
        Account.builder().bartEmployeeId("employeeId").bartId("companyId").email(null).build(),
        Account.builder().bartEmployeeId("employeeId").bartId(null).email("email").build(),
        Account.builder().bartEmployeeId(null).bartId("companyId").email("email").build()
    );
  }

  CustomJwtAuthenticationToken createCustomJwtAuthenticationToken(String accessLevel) {
    var account = Account.builder()
        .accessLevel(accessLevel)
        .email("email")
        .bartId("1")
        .bartEmployeeId(BART_EMPLOYEE_ID.toString())
        .build();
    var jwt = Jwt.withTokenValue("token")
        .header("alg", "RS256")
        .header("typ", "JWT")
        .claim("sub", "user123")
        .claim("role", "USER")
        .build();
    return new CustomJwtAuthenticationToken(jwt, account);
  }

}
