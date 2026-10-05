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
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesResponse;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.exception.CDHError;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.properties.CustomerHubPropertiesLegacy;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerHubClientLegacy {

    @Qualifier("customerHubRestTemplate")
    private final RestTemplate customerHubRestTemplate;
    private final JsonMapper objectMapper;
    private final CustomerHubPropertiesLegacy customerHubPropertiesLegacy;

    public void updateNewsletterPreferences(CustomerHubNewsletterPreferencesUpdateRequest updateRequest) {

        log.info("Customer Hub request for updating customerHubUpdatePermissions preferences, customerId: {}, correlationId: {}", updateRequest.getCustomerId(), updateRequest.getCorrelationId());

        CustomerHubNewsletterPreferencesResponse response = callCustomerHub(buildHttpEntity(updateRequest, RequestType.UPDATE),
                customerHubPropertiesLegacy.getUpdateMarketingPreferencesServiceUrl(), CustomerHubNewsletterPreferencesResponse.class);

        log.info(response.getMessage(), "Customer Hub response for updating customerHubUpdatePermissions preferences");

    }

    private <T> T callCustomerHub(HttpEntity entity, String url, Class<T> responseType) {
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


    protected <T> HttpEntity<T> buildHttpEntity(T request, RequestType requestType) {
        return new HttpEntity<>(
                request,
                buildHeaders(requestType));
    }

    protected HttpHeaders buildHeaders(RequestType requestType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (requestType.equals(RequestType.UPDATE)) {
            headers.set(customerHubPropertiesLegacy.getAuthKeyHeaderName(), customerHubPropertiesLegacy.getUpdateMarketingPreferencesAuthKey());
        }

        return headers;

    }

    public enum RequestType {
        UPDATE
    }
}
