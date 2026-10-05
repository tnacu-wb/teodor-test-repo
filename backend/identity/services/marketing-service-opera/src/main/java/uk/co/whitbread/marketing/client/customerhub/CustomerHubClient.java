package uk.co.whitbread.marketing.client.customerhub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.marketing.client.common.ClientErrorHandler;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesResponse;
import uk.co.whitbread.marketing.client.oauth.OauthCacheProvider;
import uk.co.whitbread.marketing.exception.CDHError;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.properties.CustomerHubProperties;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerHubClient {

    @Qualifier("customerHubRestTemplate")
    private final RestTemplate customerHubRestTemplate;
    private final JsonMapper objectMapper;
    private final OauthCacheProvider oauthCacheProvider;
    private final CustomerHubProperties customerHubProperties;

    public void editNewsletterPreferences(CustomerHubNewsletterPreferencesEditRequest editRequest) {

        log.info("Customer Hub request for editing customerHubUpdatePermissions preferences, customerId: {}", editRequest.getCustomerId());

        CustomerHubNewsletterPreferencesResponse response = callCustomerHub(buildHttpEntity(editRequest),
                customerHubProperties.getEditMarketingPreferencesUrl(), CustomerHubNewsletterPreferencesResponse.class);

        log.info(response.getMessage(), "Customer Hub response for editing customerHubUpdatePermissions preferences");

    }

    public NewsletterPreferencesGetResponse getNewsletterPreferences(CustomerHubNewsletterPreferencesGetRequest request) {

        log.info("Customer Hub request to get customers newsletter preferences, requestId: {} ", request.getRequestId());

        NewsletterPreferencesGetResponse response = callCustomerHub(buildHttpEntity(request),
                customerHubProperties.getGetMarketingPreferencesUrl(), NewsletterPreferencesGetResponse.class);
        log.info("Received Customer Hub response for get newsletter preferences request for contact channel Id {}", response.getContactChannelId());
        return response;
    }

    private <T> T callCustomerHub(HttpEntity entity, String url, Class<T> responseType) {
        log.info("Calling Customer Hub {}.", url);
        try {
            return customerHubRestTemplate.exchange(
                    url,
                    POST,
                    entity,
                    responseType).getBody();
        } catch (HttpStatusCodeException e) {
            final CDHError cdhError = ClientErrorHandler.getCdhError(e, objectMapper, log);
            log.error("HttpStatusCodeException: {}", cdhError, e);
            throw new CDHException(e.getStatusCode().value(), cdhError.getMessage(), cdhError.getErrorCode());
        }
    }


    protected <T> HttpEntity<T> buildHttpEntity(T request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", oauthCacheProvider.getBearerToken()));
        headers.set(customerHubProperties.getSubscriptionKeyHeaderName(),
                customerHubProperties.getSubscriptionKey());

        return new HttpEntity<>(
                request,
                headers);
    }
}
