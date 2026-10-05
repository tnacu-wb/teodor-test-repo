package uk.co.whitbread.company;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest(classes = CompanyEntityServiceApplication.class)
@EnableWireMock(@ConfigureWireMock(port = 8081, filesUnderClasspath = "stubs"))
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }
}