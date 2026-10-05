package uk.co.whitbread.shared.auth.tenant.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@ExtendWith(MockitoExtension.class)
class TenantJwtIssuerValidatorTest {

  @InjectMocks
  private TenantJwtIssuerValidator tenantJwtIssuerValidator;
  @Mock
  private TenantRepository tenantRepository;
  @Mock
  private Map<String, JwtIssuerValidator> validators;

  @Test
  void validate_withExistingTenant_shouldReturnOk() throws MalformedURLException {
    //Arrange
    var tokenIssuer = "https://auth0.whitbread.uk";
    var jwt = Mockito.mock(Jwt.class);
    var auth0Tenant = new Tenant();
    auth0Tenant.setIssuer(tokenIssuer);
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(jwt.getClaim("iss")).thenReturn(tokenIssuer);
    when(tenantRepository.findById(tokenIssuer)).thenReturn(auth0Tenant);
    //Act
    var validatorResult = tenantJwtIssuerValidator.validate(jwt);
    //Assert
    assertFalse(validatorResult.hasErrors());
    verifyNoMoreInteractions(jwt);
    verifyNoMoreInteractions(tenantRepository);
  }

  @Test
  void validate_withNoTenant_shouldReturnOk() throws MalformedURLException {
    //Arrange
    var tokenIssuer = "https://auth0.whitbread.uk";
    var jwt = Mockito.mock(Jwt.class);
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(tenantRepository.findById(tokenIssuer)).thenReturn(null);
    //Act
    //Assert
    assertThrows(IllegalArgumentException.class, () -> tenantJwtIssuerValidator.validate(jwt));
    verifyNoMoreInteractions(jwt);
    verifyNoMoreInteractions(tenantRepository);
  }
}