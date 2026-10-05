package uk.co.whitbread.content;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;

@ConfigureWireMock(name = "wiremockServer")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ContentEntityServiceApplication.class)
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @BeforeEach
  public void setUp() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
  }

}