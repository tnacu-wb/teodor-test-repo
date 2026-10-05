package uk.co.whitbread.marketing.client.oauth;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.wiremock.spring.ConfigureWireMock;
import uk.co.whitbread.marketing.MarketingServiceApplication;
import uk.co.whitbread.marketing.config.TestCacheUtil;
import uk.co.whitbread.marketing.properties.OauthProperties;

@DirtiesContext
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = MarketingServiceApplication.class)
@ConfigureWireMock(name = "wiremockOauthCache", filesUnderClasspath = "wiremock-oauth")
@TestPropertySource(properties = {"spring.cache.type=simple"})
class OauthCacheProviderIT {

    @MockitoBean
    private OauthFeignClient oauthFeignClient;

    @MockitoBean
    private OauthProperties oauthProperties;

    @Autowired
    private OauthCacheProvider target;

    @Autowired
    private TestCacheUtil testCacheUtil;

    @BeforeEach
    void setup() {
        Mockito.when(oauthProperties.getTokenUrl()).thenReturn("http://someurl");
        Mockito.when(oauthProperties.getClientId()).thenReturn("any_client_id");
        Mockito.when(oauthProperties.getClientSecret()).thenReturn("any_client_secret");
        Mockito.when(oauthProperties.getGrantType()).thenReturn("any_grant_type");
        Mockito.when(oauthProperties.getScope()).thenReturn("any_scope");
        testCacheUtil.deleteCache();
    }

    @Test
    void shouldReturnBearerTokenFromCache() {
        // GIVEN
        OauthResponse response = new OauthResponse();
        response.setAccessToken("12345");

        // WHEN
        Mockito.when(oauthFeignClient.getBearerToken(getMap(),"http://someurl"))
            .thenReturn(response);
        final String bearerToken = target.getBearerToken();
        final String bearerTokenCached = target.getBearerToken();

        // AND
        testCacheUtil.deleteCache();
        final String newBearerToken = target.getBearerToken();

        // THEN
        Mockito.verify(oauthFeignClient, Mockito.times(2)).getBearerToken(getMap(),"http://someurl");

        Assertions.assertThat(bearerToken).isEqualTo("12345");
        Assertions.assertThat(bearerTokenCached).isEqualTo("12345");
        Assertions.assertThat(newBearerToken).isEqualTo("12345");
    }

    MultiValueMap<String, String> getMap() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("client_id", oauthProperties.getClientId());
        requestBody.add("grant_type", oauthProperties.getGrantType());
        requestBody.add("client_secret", oauthProperties.getClientSecret());
        requestBody.add("scope", oauthProperties.getScope());
        return requestBody;
    }
}
