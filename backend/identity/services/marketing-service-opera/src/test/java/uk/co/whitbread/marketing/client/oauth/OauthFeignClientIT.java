package uk.co.whitbread.marketing.client.oauth;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.wiremock.spring.ConfigureWireMock;
import uk.co.whitbread.marketing.MarketingServiceApplication;
import uk.co.whitbread.marketing.exception.OauthClientException;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = MarketingServiceApplication.class)
@ConfigureWireMock(name = "wiremockOauthFeign", filesUnderClasspath = "wiremock-oauth")
@ActiveProfiles({"disable-caching"})
class OauthFeignClientIT {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private OauthFeignClient oauthFeignClient;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    }

    @Test
    void subscription_shouldGetOKResponse() {

        final var expectedBearerToken = new OauthResponse();
        expectedBearerToken.setAccessToken("123456.access.token");
        expectedBearerToken.setExpiresIn(3599);
        expectedBearerToken.setExtExpiresIn(3599);
        expectedBearerToken.setTokenType("Bearer");

        final OauthResponse bearerToken = oauthFeignClient.getBearerToken(getMap(), "/success/oauth2/v2.0/token");
        Assertions.assertThat(bearerToken).usingRecursiveComparison().isEqualTo(expectedBearerToken);
    }

    @Test
    void subscription_shouldGet400Response() {
        var expectedException = new OauthClientException(400,"unsupported_grant_type", "3002");
        try {
            oauthFeignClient.getBearerToken(getMap(), "error400");
            Assertions.fail("Test subscription_shouldGet400Response failed.");
        } catch (RuntimeException e) {
            Assertions.assertThat(e).usingRecursiveComparison().isEqualTo(expectedException);
        }
    }

    @Test
    void subscription_shouldGet401Response() {
        var expectedException = new OauthClientException(401, null, "3002");
        try {
            oauthFeignClient.getBearerToken(getMap(), "error401");
            Assertions.fail("Test subscription_shouldGet401Response failed.");
        } catch (RuntimeException e) {
            Assertions.assertThat(e).usingRecursiveComparison().isEqualTo(expectedException);
        }

    }

    MultiValueMap<String, String> getMap() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", "any_client_id");
        requestBody.add("grant_type", "any_client_secret");
        requestBody.add("client_secret", "any_grant_type");
        requestBody.add("scope", "any_scope");
        return requestBody;
    }
}
