package uk.co.whitbread.kiosk.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.kiosk.KioskServiceApplication;
import uk.co.whitbread.kiosk.infrastructure.config.SecurityConfig;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = KioskServiceApplication.class)
class KioskControllerSecurityIT {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void applicationContextLoads_withSecurityConfiguration() {
    // Validates that the Spring Boot application loads with the migrated security config
    assertNotNull(applicationContext, "Application context should not be null");
  }

  @Test
  void securityConfig_hasEnableMethodSecurityAnnotation() {
    // Verify @EnableMethodSecurity is present on SecurityConfig class
    // This is required for method-level security (@PreAuthorize) to work in Spring Security 7
    EnableMethodSecurity enableMethodSecurity =
        SecurityConfig.class.getAnnotation(EnableMethodSecurity.class);
    assertNotNull(
        enableMethodSecurity,
        "SecurityConfig must be annotated with @EnableMethodSecurity for @PreAuthorize to work in Spring Security 7");
  }

  @Test
  void applicationEndpointBeanExists() {
    // Verify the ApplicationEndpointConfigurer bean is registered
    // This validates the security configuration is being processed
    boolean hasEndpointConfigurer =
        applicationContext.containsBean("applicationEndpoint");
    assertTrue(
        hasEndpointConfigurer,
        "ApplicationEndpointConfigurer bean should be registered from SecurityConfig");
  }
}





