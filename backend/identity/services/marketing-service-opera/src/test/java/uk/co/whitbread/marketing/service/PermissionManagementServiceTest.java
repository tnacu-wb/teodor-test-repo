package uk.co.whitbread.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.marketing.client.permissionmanagement.PermissionManagementApiClient;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementConfirmDoubleOptIn;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUnsubscribeRequest;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementUpdateRequest;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.utils.PermissionManagementTransformer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionManagementServiceTest {

    private PermissionManagementService permissionManagementService;

    @Mock
    private PermissionManagementTransformer permissionManagementTransformer;

    @Mock
    private PermissionManagementApiClient permissionManagementApiClient;

    @BeforeEach
    void setup() {
        permissionManagementService = new PermissionManagementService(permissionManagementTransformer, permissionManagementApiClient);
    }

    @Test
    void getPreferences_ShouldCallPermissionManagementApi() {

        PermissionManagementGetRequest permissionManagementGetRequestMock = mock(PermissionManagementGetRequest.class);
        when(permissionManagementTransformer.transformToPermissionManagementGetRequest(any())).thenReturn(permissionManagementGetRequestMock);

        permissionManagementService.getPreferences(new PreferencesGetRequest());

        //Then
        verify(permissionManagementApiClient).getNewsletterPreferences(permissionManagementGetRequestMock);
    }

    @Test
    void getPreferencesUsingContactChannelId_ShouldCallPermissionManagementApi() {

        PermissionManagementGetRequest permissionManagementGetRequestMock = mock(PermissionManagementGetRequest.class);
        when(permissionManagementTransformer.transformToPermissionManagementGetRequest(any())).thenReturn(permissionManagementGetRequestMock);

        permissionManagementService.getPreferencesUsingContactChannelId(new PreferencesGetRequest());

        //Then
        verify(permissionManagementApiClient).getNewsletterPreferences(permissionManagementGetRequestMock);
    }

    @Test
    void getPreferencesAnonymous_ShouldCallPermissionManagementApi() {

        PermissionManagementGetRequest permissionManagementGetRequestMock = mock(PermissionManagementGetRequest.class);
        when(permissionManagementTransformer.toPermissionManagementGetRequest(any())).thenReturn(permissionManagementGetRequestMock);

        permissionManagementService.getPreferencesAnonymous(new PreferencesAnonymousGetRequest());

        //Then
        verify(permissionManagementApiClient).getNewsletterPreferences(permissionManagementGetRequestMock);
    }

    @Test
    void updatePreferences_ShouldCallPermissionManagementApi() {

        PermissionManagementUpdateRequest permissionManagementUpdateRequestMock = mock(PermissionManagementUpdateRequest.class);
        when(permissionManagementTransformer.transformToPermissionManagementUpdatePermissionsRequest(any(), any(), any())).thenReturn(permissionManagementUpdateRequestMock);

        permissionManagementService.updatePreferences(new UpdatePreferencesRequest(), "liam.wilsoN@whitbread.com", ContactType.email);

        //Then
        verify(permissionManagementApiClient).updateNewsletterPreferences(permissionManagementUpdateRequestMock);
    }

    @Test
    void confirmDoubleOptIn_ShouldCallPermissionManagementApi() {

        PermissionManagementConfirmDoubleOptIn permissionManagementConfirmDoubleOptInRequestMock = mock(PermissionManagementConfirmDoubleOptIn.class);
        when(permissionManagementTransformer.transformToPermissionManagementConfirmDoubleOptInRequest(any(), any(), any())).thenReturn(permissionManagementConfirmDoubleOptInRequestMock);

        permissionManagementService.confirmDoubleOptIn(new ConfirmDoubleOptInRequest(), "liam.wilsoN@whitbread.com", ContactType.email);

        //Then
        verify(permissionManagementApiClient).confirmDoubleOptIn(permissionManagementConfirmDoubleOptInRequestMock);
    }

    @Test
    void unsubscribe_ShouldCallPermissionManagementApi() {

        PermissionManagementUnsubscribeRequest permissionManagementUnsubscribeRequestMock = mock(PermissionManagementUnsubscribeRequest.class);
        when(permissionManagementTransformer.transformToPermissionManagementUnsubscribeRequest(any(), any(), any())).thenReturn(permissionManagementUnsubscribeRequestMock);

        permissionManagementService.unsubscribe(new UnsubscribeRequest(), "liam.wilsoN@whitbread.com", ContactType.email);

        //Then
        verify(permissionManagementApiClient).unsubscribe(permissionManagementUnsubscribeRequestMock);
    }

}