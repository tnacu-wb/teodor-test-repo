package uk.co.whitbread.shared.auth.config;

import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.shared.auth.builder.ApplicationEndpointConfigurer;
import uk.co.whitbread.shared.auth.tenant.SecurityProperties;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;
import uk.co.whitbread.shared.auth.tenant.TenantService;
import uk.co.whitbread.shared.auth.tenant.jwt.TenantJWSKeySelector;
import uk.co.whitbread.shared.auth.tenant.jwt.TenantJwtAudienceValidator;
import uk.co.whitbread.shared.auth.tenant.jwt.TenantJwtGrantedAuthoritiesConverter;
import uk.co.whitbread.shared.auth.tenant.jwt.TenantJwtIssuerValidator;

@EnableConfigurationProperties(SecurityProperties.class)
public class AuthConfiguration {

  private static final String HTTP_AUTH_HEADER = "WB-Authorization";

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      ApplicationEndpointConfigurer configurer,
      JwtDecoder jwtDecoder,
      CustomJwtAuthenticationConverter customJwtAuthenticationConverter
  )
      throws Exception {

    http.cors(Customizer.withDefaults());
    http.authorizeHttpRequests(registry -> configurer.securityAuthorizedUrl(registry));
    http.csrf(csrf -> csrf.disable());
    http.oauth2ResourceServer(oauth2 ->
        oauth2.jwt(jwt -> {
          jwt.decoder(jwtDecoder);
          jwt.jwtAuthenticationConverter(customJwtAuthenticationConverter);
        })
    );

    return http.build();
  }

  @Bean
  public TenantRepository inMemoryTenantRepository(SecurityProperties securityProperties) {
    var tenantRepository = new TenantRepository();
    securityProperties.getTenants().forEach(tenantRepository::save);
    return tenantRepository;
  }

  @Bean
  public JwtDecoder jwtDecoder(TenantRepository tenantRepository) {
    var keySelector = new TenantJWSKeySelector(tenantRepository);
    var jwtProcessor = new DefaultJWTProcessor<>();
    jwtProcessor.setJWTClaimsSetAwareJWSKeySelector(keySelector);
    var jwtIssuerValidator = new TenantJwtIssuerValidator(tenantRepository);
    var jwtAudienceValidator = new TenantJwtAudienceValidator(tenantRepository);
    var validator = new DelegatingOAuth2TokenValidator<>(
        JwtValidators.createDefault(),
        jwtIssuerValidator
//TODO:: temporary eliminating audience to allow id_token based access until complete migration
//        jwtAudienceValidator
    );
    var decoder = new NimbusJwtDecoder(jwtProcessor);
    decoder.setJwtValidator(validator);
    return decoder;
  }

  @Bean
  public CustomJwtAuthenticationConverter customJwtAuthenticationConverter(TenantRepository tenantRepository) {
    var tenantJwtGrantedAuthoritiesConverter = new TenantJwtGrantedAuthoritiesConverter(tenantRepository);
    var tenantService = new TenantService(tenantRepository);
    var customJwtConverter = new CustomJwtAuthenticationConverter(tenantService);
    customJwtConverter.setJwtGrantedAuthoritiesConverter(tenantJwtGrantedAuthoritiesConverter);
    return customJwtConverter;
  }

  @Bean
  BearerTokenResolver bearerTokenResolver() {
    DefaultBearerTokenResolver authorizationHeaderResolver = new DefaultBearerTokenResolver();
    DefaultBearerTokenResolver wbAuthorizationHeaderResolver = new DefaultBearerTokenResolver();
    wbAuthorizationHeaderResolver.setBearerTokenHeaderName(HTTP_AUTH_HEADER);

    return request -> {
      String token = authorizationHeaderResolver.resolve(request);
      return token != null ? token : wbAuthorizationHeaderResolver.resolve(request);
    };
  }

  @Bean
  @ConditionalOnProperty(prefix = "auth", name = "rulesEngineHost")
  public WebClient rulesServiceWebClient(SecurityProperties securityProperties,
      WebClient.Builder webClientBuilder) {
    return webClientBuilder
        .baseUrl(securityProperties.getRulesEngineHost())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
