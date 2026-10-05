package uk.co.whitbread.address.lookup;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;

@ConfigureWireMock(name = "wiremockServer")
@SpringBootTest(classes = AddressLookupServiceApplication.class)
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }
}