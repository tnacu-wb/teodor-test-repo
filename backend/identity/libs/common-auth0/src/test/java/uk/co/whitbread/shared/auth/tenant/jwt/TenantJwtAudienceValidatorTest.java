package uk.co.whitbread.shared.auth.tenant.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@ExtendWith(MockitoExtension.class)
class TenantJwtAudienceValidatorTest {

  @InjectMocks
  private TenantJwtAudienceValidator tenantJwtAudienceValidator;
  @Mock
  private TenantRepository tenantRepository;

  @Test
  void validate_existingTenant_shouldReturnSuccess() throws MalformedURLException {
    //Arrange
    var tokenAudience = "https://api.whitbread.uk";
    var tokenIssuer = "https://auth0.whitbread.uk";
    var auth0Tenant = new Tenant();
    auth0Tenant.setAudience(tokenAudience);
    var jwt = Mockito.mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of(tokenAudience));
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(tenantRepository.findById(tokenIssuer)).thenReturn(auth0Tenant);
    //Act
    var validatorResult = tenantJwtAudienceValidator.validate(jwt);
    //Assert
    assertFalse(validatorResult.hasErrors());
    verifyNoMoreInteractions(tenantRepository);
    verifyNoMoreInteractions(jwt);
  }

  @Test
  void validate_noTenant_shouldReturnFailure() throws MalformedURLException {
    //Arrange
    var tokenAudience = "https://api.whitbread.uk";
    var tokenIssuer = "https://auth0.whitbread.uk";
    var auth0Tenant = new Tenant();
    auth0Tenant.setAudience(tokenAudience);
    var jwt = Mockito.mock(Jwt.class);
    when(jwt.getAudience()).thenReturn(List.of("AUDIENCE_1"));
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(tenantRepository.findById(tokenIssuer)).thenReturn(null);
    //Act
    var validatorResult = tenantJwtAudienceValidator.validate(jwt);
    //Assert
    assertTrue(validatorResult.hasErrors());
    verifyNoMoreInteractions(tenantRepository);
    verifyNoMoreInteractions(jwt);
  }
}