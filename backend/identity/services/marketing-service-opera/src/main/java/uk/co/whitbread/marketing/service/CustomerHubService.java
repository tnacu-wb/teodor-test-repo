package uk.co.whitbread.marketing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.marketing.client.customerhub.CustomerHubClient;
import uk.co.whitbread.marketing.client.customerhub.CustomerHubClientLegacy;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.utils.Converter;

@Slf4j
@RequiredArgsConstructor
@Service
@Deprecated
public class CustomerHubService {

    private final Converter converter;
    private final CustomerHubClientLegacy customerHubClientLegacy;
    private final CustomerHubClient customerHubClient;

    @Deprecated
    public void editNewsletterPreferences(NewsletterPreferencesEditRequest editRequest) {
        CustomerHubNewsletterPreferencesEditRequest request = converter.convertToCustomerHubNewsletterPrefEditReq(editRequest);
        customerHubClient.editNewsletterPreferences(request);
    }

    @Deprecated
    public void editNewsletterPreferences(UpdatePreferencesRequest updatePreferencesRequest, String contactValue, ContactType contactType) {
        var newsletterPreferencesEditRequest = converter.convertToCustomerHubNewsletterPrefEditReq(updatePreferencesRequest, contactValue, contactType);
        CustomerHubNewsletterPreferencesEditRequest request = converter.convertToCustomerHubNewsletterPrefEditReq(newsletterPreferencesEditRequest);
        customerHubClient.editNewsletterPreferences(request);
    }

    @Deprecated
    public void updateNewsletterPreferences(String customerId, NewsletterPreferencesUpdateRequest updateRequest) {

        CustomerHubNewsletterPreferencesUpdateRequest request = converter.convertToCustomerHubNewsletterPrefUpdateReq(customerId, updateRequest);

        customerHubClientLegacy.updateNewsletterPreferences(request);

    }

    @Deprecated
    public NewsletterPreferencesGetResponse getNewsletterPreferences(NewsletterPreferencesGetRequest getRequest) {

        CustomerHubNewsletterPreferencesGetRequest request = converter.convertToCustomerHubNewsletterPreferencesGetRequest(getRequest);
        NewsletterPreferencesGetResponse response = customerHubClient.getNewsletterPreferences(request);

        // This code is to be deprecated soon. CDH changed their contract from valid to IsValid
        response.setValid(response.isIsValid());
        return response;
    }

    @Deprecated
    public PreferencesGetResponse getPreferences(PreferencesGetRequest getRequest) {

        CustomerHubNewsletterPreferencesGetRequest request = converter.convertToCustomerHubPreferencesGetRequest(getRequest);
        final NewsletterPreferencesGetResponse newsletterPreferences = customerHubClient.getNewsletterPreferences(request);
        return converter.convertCustomerHubToPreferencesGetResponse(newsletterPreferences);

    }
}
