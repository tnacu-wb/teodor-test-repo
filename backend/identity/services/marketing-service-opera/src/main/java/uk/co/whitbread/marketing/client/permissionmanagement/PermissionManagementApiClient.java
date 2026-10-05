package uk.co.whitbread.marketing.client.permissionmanagement;

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
import uk.co.whitbread.marketing.client.oauth.OauthCacheProvider;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementConfirmDoubleOptIn;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetResponse;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUnsubscribeRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUpdateRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.RequestAcceptedResponse;
import uk.co.whitbread.marketing.exception.CDHError;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.properties.CustomerHubProperties;
import uk.co.whitbread.marketing.properties.PermissionManagementApiProperties;

import static org.springframework.http.HttpMethod.POST;

@Slf4j
@RequiredArgsConstructor
@Component
public class PermissionManagementApiClient {

    @Qualifier("permissionManagementApiRestTemplate")
    private final RestTemplate permissionManagementApiRestTemplate;
    private final JsonMapper objectMapper;
    private final OauthCacheProvider oauthCacheProvider;
    private final PermissionManagementApiProperties permissionManagementApiProperties;
    private final CustomerHubProperties customerHubProperties;

    public PermissionManagementGetResponse getNewsletterPreferences(PermissionManagementGetRequest request) {
        PermissionManagementGetResponse response = callPermissionManagementApi(
            buildHttpEntityForGetMarketingPreferencesRequest(request),
            permissionManagementApiProperties.getGetMarketingPreferencesV2Url(), PermissionManagementGetResponse.class);
        log.info("Received Permission Management API response for get newsletter preferences request for contact channel Id {}", response.getContactChannelId());
        return response;
    }

    public void updateNewsletterPreferences(PermissionManagementUpdateRequest permissionManagementUpdateRequest) {
        log.info("Received Permission Management API request for updating customer permissions.");

        RequestAcceptedResponse response = callPermissionManagementApi(buildHttpEntity(permissionManagementUpdateRequest),
                permissionManagementApiProperties.getUpdateMarketingPreferencesUrl(), RequestAcceptedResponse.class);

        log.info("Permission Management API response {} for updating customer permissions.", response);
    }

    public void confirmDoubleOptIn(PermissionManagementConfirmDoubleOptIn permissionManagementConfirmDoubleOptIn) {
        RequestAcceptedResponse response = callPermissionManagementApi(buildHttpEntity(permissionManagementConfirmDoubleOptIn),
                permissionManagementApiProperties.getConfirmDoubleOptInUrl(), RequestAcceptedResponse.class);
        log.info("Permission Management API response {} for confirming double opt in permissions.", response);
    }

    public void unsubscribe(PermissionManagementUnsubscribeRequest permissionManagementUnsubscribeRequest) {
        RequestAcceptedResponse response = callPermissionManagementApi(buildHttpEntity(permissionManagementUnsubscribeRequest),
                permissionManagementApiProperties.getUnsubscribeUrl(), RequestAcceptedResponse.class);
        log.info("Permission Management API response {} for unsubscribe.", response);
    }

    private <T> T callPermissionManagementApi(HttpEntity entity, String url, Class<T> responseType) {

        log.info("Sending POST request to {}.", url);
        try {
            return permissionManagementApiRestTemplate.exchange(
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
        headers.set(permissionManagementApiProperties.getSubscriptionKeyHeaderName(),
            permissionManagementApiProperties.getSubscriptionKey());

        return new HttpEntity<>(
            request,
            headers);
    }

    protected <T> HttpEntity<T> buildHttpEntityForGetMarketingPreferencesRequest(T request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", oauthCacheProvider.getBearerToken()));
        headers.set(permissionManagementApiProperties.getSubscriptionKeyHeaderName(),
            customerHubProperties.getSubscriptionKeyV2());

        return new HttpEntity<>(
                request,
                headers);
    }
}
