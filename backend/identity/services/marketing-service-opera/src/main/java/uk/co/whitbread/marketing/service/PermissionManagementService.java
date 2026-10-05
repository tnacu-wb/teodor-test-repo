package uk.co.whitbread.marketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.marketing.client.permissionmanagement.PermissionManagementApiClient;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetResponse;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUpdateRequest;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetResponse;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.utils.PermissionManagementTransformer;

@Slf4j
@RequiredArgsConstructor
@Service
public class PermissionManagementService {

    private final PermissionManagementTransformer transformer;
    private final PermissionManagementApiClient permissionManagementApiClient;

    public PreferencesGetResponse getPreferencesUsingContactChannelId(PreferencesGetRequest request) {
        PermissionManagementGetRequest permissionManagementGetRequest = transformer.transformToPermissionManagementGetRequest(request);
        final PermissionManagementGetResponse permissionManagementGetResponse = permissionManagementApiClient
            .getNewsletterPreferences(permissionManagementGetRequest);
        return transformer.convertPermissionManagementResponseToGetResponse(permissionManagementGetResponse,
            null, null);
    }

    public PreferencesGetResponse getPreferences(PreferencesGetRequest request) {
        PermissionManagementGetRequest permissionManagementGetRequest = transformer.transformToPermissionManagementGetRequest(request);
        final PermissionManagementGetResponse permissionManagementGetResponse = permissionManagementApiClient
            .getNewsletterPreferences(permissionManagementGetRequest);
        return transformer.convertPermissionManagementResponseToGetResponse(permissionManagementGetResponse,
            request.getCountryOfResidence(), request.getLanguage());
    }

    public PreferencesAnonymousGetResponse getPreferencesAnonymous(PreferencesAnonymousGetRequest request) {
        var permissionManagementGetRequest = transformer.toPermissionManagementGetRequest(request);
        var permissionManagementGetResponse = permissionManagementApiClient
            .getNewsletterPreferences(permissionManagementGetRequest);
        return transformer.toPreferencesAnonymousGetResponse(permissionManagementGetResponse,
            request.getCountryOfResidence(), request.getLanguage());
    }

    public void updatePreferences(UpdatePreferencesRequest updatePreferencesRequest, String contactValue, ContactType contactType) {
        PermissionManagementUpdateRequest request = transformer.transformToPermissionManagementUpdatePermissionsRequest(updatePreferencesRequest, contactType, contactValue);
        permissionManagementApiClient.updateNewsletterPreferences(request);
    }

    public void confirmDoubleOptIn(ConfirmDoubleOptInRequest confirmDoubleOptInRequest, String contactValue, ContactType contactType) {
        var request = transformer.transformToPermissionManagementConfirmDoubleOptInRequest(confirmDoubleOptInRequest, contactType, contactValue);
        permissionManagementApiClient.confirmDoubleOptIn(request);
    }

    public void unsubscribe(UnsubscribeRequest unsubscribeRequest, String contactValue, ContactType contactType) {
        var request = transformer.transformToPermissionManagementUnsubscribeRequest(unsubscribeRequest, contactType, contactValue);
        permissionManagementApiClient.unsubscribe(request);
    }

}
