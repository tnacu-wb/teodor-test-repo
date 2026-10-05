package uk.co.whitbread.shared.auth.tenant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

  @Mock
  private TenantRepository tenantRepository;

  @InjectMocks
  private TenantService tenantService;

  @Test
  void validate_getNamespace_success() throws MalformedURLException {

    //Arrange
    var tokenNamespace = "https://premierinn.com";
    var tokenIssuer = "https://auth0.whitbread.uk";
    var auth0Tenant = new Tenant();
    auth0Tenant.setNamespace(tokenNamespace);
    when(tenantRepository.findById(tokenIssuer)).thenReturn(auth0Tenant);

    //Act
    var namespace = tenantService.getNamespace(tokenIssuer);

    //Assert
    assertEquals(tokenNamespace, namespace);
  }
}
