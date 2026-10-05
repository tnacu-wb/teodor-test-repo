package uk.co.whitbread.marketing.utils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.marketing.exception.ValidationException;
import uk.co.whitbread.marketing.model.ContactChannel;
import uk.co.whitbread.marketing.model.ContactType;
import uk.co.whitbread.marketing.model.EditSubscription;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.ContactSubType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesEditRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.marketing.model.newsletter.ContactType.email;
import static uk.co.whitbread.marketing.model.newsletter.ContactType.phone;

public class RequestUtilsTest {

    private RequestUtils underTest;
    private static final String[] BRAND_CODES = new String[]{"PINN", "WINN"};

    @BeforeEach
    public void setUp() throws Exception {
        underTest = new RequestUtils();
    }

    @Test
    public void shouldGetCustomerIdFromEmail() {
        //Given
        String email = "email";
        String customerId = null;

        //When
        String result = underTest.getCustomerId(email, customerId);

        //Then
        assertThat(result, is(equalTo("email")));
    }

    @Test
    public void shouldGetCustomerIdFromCustomerId() {
        //Given
        String email = "email";
        String customerId = "customerId";

        //When
        String result = underTest.getCustomerId(email, customerId);

        //Then
        assertThat(result, is(equalTo("customerId")));
    }

    @Test
    public void shouldValidateRequest() {
        //Given
        MarketingSubscriptionRequest request = new MarketingSubscriptionRequest();

        assertThrows(ValidationException.class,
            () -> underTest.validateRequest(request),
            "EmailAddress or CustomerId must be present in request");

        Matchers.hasProperty("errorCode", is("001"));

    }

    @Test
    public void test_editPermissionRequest_invalidBrandCodes() {
        NewsletterPreferencesEditRequest editRequest = new NewsletterPreferencesEditRequest();
        List<EditSubscription> subscriptions = new ArrayList<>();
        EditSubscription subscription = createEditSubscription();
        subscription.setBrandCodes(new String[]{"PINN", "WIN"});
        subscriptions.add(subscription);
        editRequest.setSubscriptionData(subscriptions);

        assertThrows(ValidationException.class,
            () -> underTest.validateEditRequest(editRequest),
            "Invalid Brand code.Ensure brand codes supplied are valid");

    }

    @Test
    public void test_editPermissionRequest_noBrandCodes() {
        NewsletterPreferencesEditRequest editRequest = new NewsletterPreferencesEditRequest();
        List<EditSubscription> subscriptions = new ArrayList<>();
        EditSubscription subscription = createEditSubscription();
        subscription.setBrandCodes(new String[]{});
        subscriptions.add(subscription);
        editRequest.setSubscriptionData(subscriptions);

        assertThrows(ValidationException.class,
            () -> underTest.validateEditRequest(editRequest),
            "At least one brand code must be present");
    }

    @Test
    public void test_editPermissionRequest_nullContactValueAndID() {
        NewsletterPreferencesEditRequest editRequest = new NewsletterPreferencesEditRequest();
        List<EditSubscription> subscriptions = new ArrayList<>();
        EditSubscription subscription = createEditSubscription();
        subscription.setContactChannelId(null);
        subscription.setContactChannelValue(null);
        subscriptions.add(subscription);
        editRequest.setSubscriptionData(subscriptions);

        assertThrows(ValidationException.class,
            () -> underTest.validateEditRequest(editRequest),
            "Either contactChannelValue or contactChannelId must be present");

    }

    @Test
    public void test_editPermissionRequest_contactTypeTelephoneSubTypeNull() {
        NewsletterPreferencesEditRequest editRequest = new NewsletterPreferencesEditRequest();
        List<EditSubscription> subscriptions = new ArrayList<>();
        EditSubscription subscription = createEditSubscription();
        subscription.setContactChannelType(ContactType.Telephone);
        subscription.setContactChannelSubType("");
        subscriptions.add(subscription);
        editRequest.setSubscriptionData(subscriptions);

        assertThrows(ValidationException.class,
            () -> underTest.validateEditRequest(editRequest),
            "ContactChannelSubType must not be null or empty when ContactChannelType is \"Telephone\"");

    }

    private EditSubscription createEditSubscription() {
        return EditSubscription.builder().brandCodes(BRAND_CODES)
                .contactChannelValue("name.surname@email.com")
                .contactChannelType(ContactType.Email)
                .contactChannelId("contactId")
                .build();
    }


    @Test
    public void test_validateGetPreferencesRequest_nullContactValueAndID() {
        NewsletterPreferencesGetRequest getRequest = createGetPreferencesRequest();
        ContactChannel contactChannel = ContactChannel.builder().contactChannelType(ContactType.Email).build();
        getRequest.setContactChannel(contactChannel);

        assertThrows(ValidationException.class,
            () -> underTest.validateGetPreferencesRequest(getRequest),
            "Either contactChannelValue or contactChannelId must be present");
    }

    @Test
    public void test_validateGetPreferencesRequest_noBrandCodes() {
        NewsletterPreferencesGetRequest getRequest = createGetPreferencesRequest();
        getRequest.setBrandCodes(new String[]{});

        assertThrows(ValidationException.class,
            () -> underTest.validateGetPreferencesRequest(getRequest),
            "At least one brand code must be present");
    }

    @Test
    public void test_validateGetPreferencesRequest_invalidBrandCodes() {
        NewsletterPreferencesGetRequest getRequest = createGetPreferencesRequest();
        getRequest.setBrandCodes(new String[]{"PINN", "DINN"});

        assertThrows(ValidationException.class,
            () -> underTest.validateGetPreferencesRequest(getRequest),
            "Invalid Brand code.Ensure brand codes supplied are valid");
    }

    @Test
    public void test_validateGetPreferencesRequest() {
        PreferencesGetRequest request = PreferencesGetRequest.builder()
                .brandCodes(new String[]{"PINN"})
                .contactType(uk.co.whitbread.marketing.model.newsletter.ContactType.email)
                .contactValue("test@validemail.com")
                .build();
        underTest.validateGetPreferencesRequest(request);
    }

    @Test
    public void test_validateGetPreferencesRequestWithPhone() {
        PreferencesGetRequest request = PreferencesGetRequest.builder()
                .brandCodes(new String[]{"PINN"})
                .contactType(phone)
                .contactValue("+447777777777")
                .build();
        underTest.validateGetPreferencesRequest(request);
    }

    @Test
    public void test_validateGetPreferencesRequestWithInvalidEmail() {
        PreferencesGetRequest request = PreferencesGetRequest.builder()
                .brandCodes(new String[]{"PINN"})
                .contactType(uk.co.whitbread.marketing.model.newsletter.ContactType.email)
                .contactValue("test@validemail.c")
                .build();

        assertThrows(ValidationException.class,
            () -> underTest.validateGetPreferencesRequest(request),
            "Invalid email");
    }

    @Test
    public void test_validateEditPreferencesRequestWithPhone() {
        PreferencesEditRequest request = PreferencesEditRequest
                .builder()
                .brandCodes(new String[]{"PINN"})
                .contactSubType(ContactSubType.landline)
                .optIn(true)
                .secondPartyOptIn(true)
                .thirdPartyVendorsOnpIn(true)
                .userId("test.id")
                .build();

        underTest.validateEditPermissions(request, "+447777777777", phone);
    }

    @Test
    public void test_validateGetPreferencesRequestWithEmail() {
        PreferencesGetRequest request = PreferencesGetRequest.builder()
            .brandCodes(new String[]{"PINN"})
            .contactType(uk.co.whitbread.marketing.model.newsletter.ContactType.email)
            .contactValue("a.a@company.software")
            .build();

        underTest.validateGetPreferencesRequest(request);
    }

    @Test
    public void shouldReturnValidationErrorWhenEditPreferencesRequestHasEmptyBrandCode() {
        PreferencesEditRequest request = PreferencesEditRequest
                .builder()
                .brandCodes(new String[]{})
                .contactSubType(ContactSubType.landline)
                .optIn(true)
                .secondPartyOptIn(true)
                .thirdPartyVendorsOnpIn(true)
                .userId("test.id")
                .build();

        assertThrows(ValidationException.class,
            () -> underTest.validateEditPermissions(request, "+447777777777", phone),
            "At least one brand code must be present");
    }

    @Test
    public void shouldReturnValidationErrorWhenEditPreferencesRequestWithInvalidEmail() {
        PreferencesEditRequest request = PreferencesEditRequest
                .builder()
                .brandCodes(new String[]{"PINN"})
                .optIn(true)
                .secondPartyOptIn(true)
                .thirdPartyVendorsOnpIn(true)
                .userId("test.id")
                .build();

        assertThrows(ValidationException.class,
            () -> underTest.validateEditPermissions(request, "test@m.c", email),
            "Invalid email");
    }

    private NewsletterPreferencesGetRequest createGetPreferencesRequest(){
        NewsletterPreferencesGetRequest getRequest = new NewsletterPreferencesGetRequest();
        ContactChannel contactChannel = ContactChannel.builder()
                .contactChannelType(ContactType.Email)
                .contactChannelValue("email@email.com")
                .contactChannelId("contact-id-test")
                .build();
        getRequest.setContactChannel(contactChannel);
        getRequest.setBrandCodes(BRAND_CODES);
        return getRequest;
    }

}