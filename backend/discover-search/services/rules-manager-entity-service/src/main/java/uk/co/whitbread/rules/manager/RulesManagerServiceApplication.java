package uk.co.whitbread.rules.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class RulesManagerServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RulesManagerServiceApplication.class, args);
    Hooks.enableAutomaticContextPropagation();
  }
}
