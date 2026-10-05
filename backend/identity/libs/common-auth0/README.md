# Opera World - Security Enabler

This repository encapsulates common components that enable security by leveraging Spring Security libraries.

## Requirements

- **Java 25** or higher
- **Spring Boot 4.0.3** or higher
- **Maven 3.9+** recommended

For more details on the thought process, please visit the [Confluence page](https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3561062440/Auth0+the+API).

# Usage

Add library dependency in the `pom.xml`:

  ```xml
  <dependency>
    <groupId>uk.co.whitbread.shared</groupId>
    <artifactId>common-auth0</artifactId>
    <version>CHANGE_ME</version>
  </dependency>
  ```

Declare the Auth0 tenant/s via `applicaiton.yaml`:

  ```yaml
  auth:
    tenants:
      - name: "CCUI Auth0 Tenant"
        audience: https://api.dev.whitbread.digital
        namespace: https://ccui.opera.whitbread.digital
        issuer: https://dev-whitbread-digital.eu.auth0.com/
        jwkUri: https://dev-whitbread-digital.eu.auth0.com/.well-known/jwks.json
      - name: "Some other Tenant"
        audience: https://api.dev.whitbread.digital
        namespace: https://pi.whitbread.digital
        issuer: https://pi-dev-whitbread-digital.eu.auth0.com/
        jwkUri: https://pi-dev-whitbread-digital.eu.auth0.com/.well-known/jwks.json
  ```

Enable the authentication mechanism and define the endpoints that need to be secured and the ones which don’t:

  ```java
  @EnableAuth
  public class SecurityConfig {

    @Bean
    public ApplicationEndpointConfigurer applicationEndpoint() {
      return registry -> registry
          .requestMatchers(HttpMethod.POST, "/eckoh", "/**/some_other_path/**")
          .permitAll() // allow unsecured access to POST /eckoh (subject to CSRF)
          .requestMatchers(HttpMethod.GET, "/eckoh")
          .permitAll() // allow unsecured access to GET /eckoh
          .anyRequest()
          .authenticated(); // secure other endpoints
    }
  }
  ```

Bearer tokens can be supplied using the standard `Authorization` header or the legacy
`WB-Authorization` header. If both are present, `Authorization` is used first.

Audience validation is currently disabled while services complete an `id_token`
migration. Issuer validation remains enabled.
   
  Fine-grained access control at the method level can be achieved through Spring’s annotation-based security: @PreFilter, @PostFilter, @PreAuthorize, and @PostAuthorize.
  
  ```java
  // in case roles are defined as custom claims
@PreAuthorize("hasAuthority(roleIdList, resourceId)")
// default claims parsing where scopes (persmissions) are used as granted authorities
@PreAuthorize("hasAuthority('SCOPE_manager:ccui') or hasAuthority('SCOPE_agent:ccui')")
  ```

### CCUI permission mapping

- https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/3733028950/PI3+All+pods+Role-based+Access+Rules+for+CCUI

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/maven-plugin/)
* [Create an OCI image](https://docs.spring.io/spring-boot/maven-plugin/build-image.html)
* [OAuth2 Resource Server](https://docs.spring.io/spring-boot/reference/web/spring-security.html#web.security.oauth2.server)
* [Spring Security](https://docs.spring.io/spring-boot/reference/web/spring-security.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
