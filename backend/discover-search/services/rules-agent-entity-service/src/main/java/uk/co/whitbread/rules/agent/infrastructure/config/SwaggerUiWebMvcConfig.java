package uk.co.whitbread.rules.agent.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.webjars.WebJarVersionLocator;

/**
 * Explicitly registers Swagger UI static resources from the webjar.
 * Required because the global exception handler in commons-entity-exceptions
 * intercepts NoResourceFoundException before the default resource handler can serve them.
 */
@Configuration
public class SwaggerUiWebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String version = new WebJarVersionLocator().version("swagger-ui");
    registry.addResourceHandler("/swagger-ui/**")
        .addResourceLocations(
            "classpath:/META-INF/resources/webjars/swagger-ui/" + version + "/");
    registry.addResourceHandler("/webjars/**")
        .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/swagger-ui.html", "/swagger-ui/index.html");
  }
}




