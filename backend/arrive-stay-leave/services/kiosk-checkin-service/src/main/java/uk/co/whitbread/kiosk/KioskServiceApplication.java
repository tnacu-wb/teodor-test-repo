package uk.co.whitbread.kiosk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
public class KioskServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(KioskServiceApplication.class, args);
  }
}
