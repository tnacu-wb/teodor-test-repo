package uk.co.whitbread.payment.orchestrator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"uk.co.whitbread"})
@Slf4j
public class PaymentOrchestrationServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(PaymentOrchestrationServiceApplication.class, args);
    log.info("Payment Orchestration Service started successfully");
  }
}
