package uk.co.whitbread.kiosk;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = KioskServiceApplication.class)
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }
}