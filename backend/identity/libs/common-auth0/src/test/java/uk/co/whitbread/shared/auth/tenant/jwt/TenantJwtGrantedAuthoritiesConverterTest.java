package uk.co.whitbread.shared.auth.tenant.jwt;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import uk.co.whitbread.shared.auth.tenant.Tenant;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@ExtendWith(MockitoExtension.class)
class TenantJwtGrantedAuthoritiesConverterTest {

  @InjectMocks
  private TenantJwtGrantedAuthoritiesConverter tenantJwtGrantedAuthoritiesConverter;
  @Mock
  private TenantRepository tenantRepository;

  @Test
  void convert_withNoClaims_shouldConvertJwt() throws MalformedURLException {
    var tokenIssuer = "https://auth0.whitbread.uk";
    var namespace = "NAMESPACE";
    var auth0Tenant = new Tenant();
    auth0Tenant.setNamespace(namespace);
    var authorityClaimSuffix = "/role";
    //Arrange
    var jwt = Mockito.mock(Jwt.class);
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(jwt.getClaim(namespace + authorityClaimSuffix)).thenReturn(Collections.emptyList());
    when(tenantRepository.findById(tokenIssuer)).thenReturn(auth0Tenant);
    //Act
    tenantJwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
    tenantJwtGrantedAuthoritiesConverter.setAuthorityClaimSuffix(authorityClaimSuffix);
    var converterResult = tenantJwtGrantedAuthoritiesConverter.convert(jwt);
    //Assert
    assertNotNull(converterResult);
    assertTrue(converterResult.isEmpty());
    verifyNoMoreInteractions(jwt);
    verifyNoMoreInteractions(tenantRepository);
  }

  @Test
  void convert_withListOfClaims_shouldConvertJwt() throws MalformedURLException {
    var tokenIssuer = "https://auth0.whitbread.uk";
    var namespace = "NAMESPACE";
    var jwtExtractedClaims = List.of("ADMIN_ROLE");
    var auth0Tenant = new Tenant();
    auth0Tenant.setNamespace(namespace);
    var authorityClaimSuffix = "/role";
    //Arrange
    var jwt = Mockito.mock(Jwt.class);
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(jwt.getClaim(namespace + authorityClaimSuffix)).thenReturn(jwtExtractedClaims);
    when(tenantRepository.findById(tokenIssuer)).thenReturn(auth0Tenant);
    //Act
    tenantJwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
    tenantJwtGrantedAuthoritiesConverter.setAuthorityClaimSuffix(authorityClaimSuffix);
    var converterResult = tenantJwtGrantedAuthoritiesConverter.convert(jwt);
    //Assert
    assertNotNull(converterResult);
    assertEquals(jwtExtractedClaims, converterResult.stream().map(GrantedAuthority::getAuthority).toList());
    verifyNoMoreInteractions(jwt);
    verifyNoMoreInteractions(tenantRepository);
  }

  @Test
  void convert_withSpaceSeparatedClaims_shouldConvertJwt() throws MalformedURLException {
    var tokenIssuer = "https://auth0.whitbread.uk";
    var namespace = "NAMESPACE";
    var jwtExtractedClaims = "MANAGER_ROLE AGENT_ROLE";
    var auth0Tenant = new Tenant();
    auth0Tenant.setNamespace(namespace);
    var authorityClaimSuffix = "/role";
    //Arrange
    var jwt = Mockito.mock(Jwt.class);
    when(jwt.getIssuer()).thenReturn(new URL(tokenIssuer));
    when(jwt.getClaim(namespace + authorityClaimSuffix)).thenReturn(jwtExtractedClaims);
    when(tenantRepository.findById(tokenIssuer)).thenReturn(auth0Tenant);
    //Act
    tenantJwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
    tenantJwtGrantedAuthoritiesConverter.setAuthorityClaimSuffix(authorityClaimSuffix);
    var converterResult = tenantJwtGrantedAuthoritiesConverter.convert(jwt);
    //Assert
    assertNotNull(converterResult);
    assertEquals(Arrays.stream(jwtExtractedClaims.split(" ")).toList(),
        converterResult.stream().map(GrantedAuthority::getAuthority).toList());
    verifyNoMoreInteractions(jwt);
    verifyNoMoreInteractions(tenantRepository);
  }
}