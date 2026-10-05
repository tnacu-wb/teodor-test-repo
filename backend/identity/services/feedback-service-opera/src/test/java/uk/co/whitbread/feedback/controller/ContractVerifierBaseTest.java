package uk.co.whitbread.feedback.controller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;
import uk.co.whitbread.feedback.FeedbackApplication;
import uk.co.whitbread.feedback.client.AuthClient;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FeedbackApplication.class)
@ConfigureWireMock(name = "wiremockServer")
public abstract class ContractVerifierBaseTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private AuthClient mockedAuthClient;

    @BeforeEach
    public void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        when(mockedAuthClient.getAuthorisationCode()).thenReturn("I_AM_A_WONDERFUL_TOKEN");
    }
}
