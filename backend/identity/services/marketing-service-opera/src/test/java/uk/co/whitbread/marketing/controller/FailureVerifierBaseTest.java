package uk.co.whitbread.marketing.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;
import uk.co.whitbread.marketing.MarketingServiceApplication;

@DirtiesContext
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MarketingServiceApplication.class)
@ConfigureWireMock(name = "wiremockFailureVerifier", filesUnderClasspath = "wiremock-failure-verifier")
@ActiveProfiles({"disable-caching"})
public abstract class FailureVerifierBaseTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

}

