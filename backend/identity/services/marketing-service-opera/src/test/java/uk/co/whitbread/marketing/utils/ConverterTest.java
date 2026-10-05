package uk.co.whitbread.marketing.utils;

import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.bart.marketing.api.Subscribe;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatus;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse2015;
import uk.co.whitbread.bart.unified.api.SharedDataRequestResponse;
import uk.co.whitbread.marketing.client.customerhub.model.ContactChannelData;
import uk.co.whitbread.marketing.client.customerhub.model.ContentPermissionData;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.client.customerhub.model.EditSubscriptionData;
import uk.co.whitbread.marketing.client.customerhub.model.SubscriptionData;
import uk.co.whitbread.marketing.mapper.RegionMapper;
import uk.co.whitbread.marketing.mapper.SubscriptionMapper;
import uk.co.whitbread.marketing.model.BrandCode;
import uk.co.whitbread.marketing.model.BrandPermission;
import uk.co.whitbread.marketing.model.ContactChannel;
import uk.co.whitbread.marketing.model.ContactType;
import uk.co.whitbread.marketing.model.EditSubscription;
import uk.co.whitbread.marketing.model.LoyaltyAccount;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.model.RegionsResponse;
import uk.co.whitbread.marketing.model.Subscription;
import uk.co.whitbread.marketing.model.newsletter.ContactSubType;
import uk.co.whitbread.marketing.model.newsletter.Permission;
import uk.co.whitbread.marketing.model.newsletter.PreferencesEditRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.properties.CustomerHubPropertiesLegacy;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ConverterTest {

    private static final String EMAIL_ADDRESS = "email@email.com";
    static final String CONTACT_VALUE_1 = "contact-value-1";
    static final String CONTACT_VALUE_2 = "contact-value-2";
    static final String SOURCE_SYSTEM = "source-system-test";
    static final String CUSTOMER_ID = "customer-id-test";
    static final String CORRELATION_ID = "correlation-id-test";
    static final String CONTACT_TYPE_1 = "Email";
    static final String CONTACT_TYPE_2 = "Telephone";
    static final String CONTACT_ID_1 = "contact-id-1";
    static final String CONTACT_ID_2 = "contact-id-2";
    static final String[] BRAND_CODES = {"brand-code-test1","brand-code-test2"};
    static final EditSubscription.ContentPermission CONTENT_PERMISSION = new EditSubscription.ContentPermission(true,false);
    static final ContentPermissionData CONTENT_PERMISSION_DATA = new ContentPermissionData(true,false);
    static final String REQUEST_ID = "request-id-test";
    static final ContactChannel CONTACT_CHANNEL = ContactChannel.builder().contactChannelType(ContactType.Email).contactChannelValue("email@emai.com").build();
    static final ContactChannelData CONTACT_CHANNEL_DATA = new ContactChannelData("Email", "email@emai.com", null);

    private JsonMapper objectMapper;
    private Converter sut;

    private final SubscriptionMapper subscriptionMapper = Mappers.getMapper(SubscriptionMapper.class);
    private final RegionMapper regionMapper = Mappers.getMapper(RegionMapper.class);

    @Mock private CustomerHubPropertiesLegacy customerHubPropertiesLegacyMock;
    @Mock private Subscription subscription1Mock;
    @Mock private Subscription subscription2Mock;
    @Mock private SubscriptionData subscriptionData1Mock;
    @Mock private SubscriptionData subscriptionData2Mock;
    @Mock private EditSubscription editSubscription1Mock;
    @Mock private EditSubscription editSubscription2Mock;
    @Mock private EditSubscriptionData editSubscriptionData1Mock;
    @Mock private EditSubscriptionData editSubscriptionData2Mock;
    @Mock private NewsletterPreferencesUpdateRequest newsletterPreferencesUpdateRequestMock;
    @Mock private NewsletterPreferencesEditRequest newsletterPreferencesEditRequestMock;
    @Mock private NewsletterPreferencesGetRequest newsletterPreferencesGetRequestMock;

    @BeforeEach
    void setUp() {
        objectMapper = TestObjectMapperFactory.create();
        sut = spy(new Converter(subscriptionMapper, customerHubPropertiesLegacyMock));

        when(customerHubPropertiesLegacyMock.getSourceSystem()).thenReturn(SOURCE_SYSTEM);
        when(newsletterPreferencesUpdateRequestMock.getCorrelationId()).thenReturn(CORRELATION_ID);
    }


    @Test
    void convertToRegionsResponse() throws Exception {
        SharedDataRequestResponse sharedDataRequestResponse = objectMapper.readValue(new File("src/test/resources/mapping/SharedDataRequestResponse.json"), SharedDataRequestResponse.class);

        RegionsResponse regionsResponse = regionMapper.toRegionsResponse(sharedDataRequestResponse);

        RegionsResponse expectedRegionResponse = objectMapper.readValue(new File("src/test/resources/mapping/RegionsResponse.json"), RegionsResponse.class);

        Assertions.assertThat(regionsResponse).usingRecursiveComparison().isEqualTo(expectedRegionResponse);

    }

    @Test
    void shouldMapSubscriptionInfoOrStatusRequest() throws Exception {
        //Given
        MarketingSubscriptionInfoRequest request = objectMapper.readValue(
                new File("src/test/resources/mapping/MarketingSubscriptionInfoRequest.json"), MarketingSubscriptionInfoRequest.class);

        SubscriptionStatus expectedBartRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/SubscriptionStatus.json"),
                SubscriptionStatus.class);

        //When
        SubscriptionStatus bartRequest = sut.convert(request);

        //Then
        Assertions.assertThat(expectedBartRequest).usingRecursiveComparison().isEqualTo(bartRequest);
    }

    @Test
    void shouldMapSubscriptionInfoResponse() throws Exception {

        SubscriptionStatusResponse subscriptionStatusResponse = objectMapper.readValue(
                new File("src/test/resources/mapping/SubscriptionStatusResponse.json"), SubscriptionStatusResponse.class);

        MarketingSubscriptionInfoResponse expectedResponse = objectMapper.readValue(
                new File("src/test/resources/mapping/MarketingSubscriptionInfoResponse.json"),
                MarketingSubscriptionInfoResponse.class);

        //When
        MarketingSubscriptionInfoResponse response = sut.convert(subscriptionStatusResponse);

        Assertions.assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    }


    @Test
    void shouldMapSubscriptionStatusResponse() {
        //Given
        SubscriptionStatusResponse subscriptionStatusResponse = new SubscriptionStatusResponse();
        SubscriptionStatusResponse2015 result = new SubscriptionStatusResponse2015();
        result.setEmailAddress(EMAIL_ADDRESS);
        result.setSubscribed(true);

        subscriptionStatusResponse.setSubscriptionStatusResult(result);
        //When
        MarketingSubscriptionInfoResponse response = sut.convert(subscriptionStatusResponse);

        //Then
        MatcherAssert.assertThat(response.getEmailAddress(), is(EMAIL_ADDRESS));
        MatcherAssert.assertThat(response.getSubscribedStatus(), is(true));
    }

    @Test
    void shouldMapSubscriptionRequest() throws Exception {
        //Given
        MarketingSubscriptionRequest request = objectMapper.readValue(
                new File("src/test/resources/mapping/MarketingSubscriptionRequest.json"), MarketingSubscriptionRequest.class);

        Subscribe expectedBartRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/Subscribe.json"),
                Subscribe.class);

        //When
        Subscribe bartRequest = sut.convert(request);

        //Then
        Assertions.assertThat(bartRequest).usingRecursiveComparison().isEqualTo(expectedBartRequest);

    }

    @Test
    void shouldMapSubscriptionResponse() {
        //Given
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        SubscriptionResponse result = new SubscriptionResponse();
        result.setSuccessful(true);
        subscribeResponse.setSubscribeResult(result);
        //When
        MarketingSubscriptionResponse response = sut.convert(subscribeResponse);

        //Then
        MatcherAssert.assertThat(response.getSuccess(), is(true));
    }

    @Test
    void shouldConvertToCustomerHubNewsletterPrefs() throws Exception {
        //Given
        MarketingSubscriptionInfoRequest request = objectMapper.readValue(
                new File("src/test/resources/mapping/MarketingSubscriptionInfoRequest.json"), MarketingSubscriptionInfoRequest.class);

        SubscriptionStatus expectedBartRequest = objectMapper.readValue(
                new File("src/test/resources/mapping/SubscriptionStatus.json"),
                SubscriptionStatus.class);

        //When
        SubscriptionStatus bartRequest = sut.convert(request);

        //Then
        Assertions.assertThat(bartRequest).usingRecursiveComparison().isEqualTo(expectedBartRequest);

    }


    @Test
    void shouldMapSubscriptionData() {

        //Given
        when(subscription1Mock.getContactType()).thenReturn(ContactType.Email);
        when(subscription1Mock.getContactValue()).thenReturn(CONTACT_VALUE_1);
        when(subscription1Mock.getSubscribe()).thenReturn(TRUE);
        when(subscription1Mock.getBrandCode()).thenReturn(BrandCode.PINN);

        when(subscription2Mock.getContactType()).thenReturn(ContactType.Telephone);
        when(subscription2Mock.getContactValue()).thenReturn(CONTACT_VALUE_2);
        when(subscription2Mock.getSubscribe()).thenReturn(FALSE);
        when(subscription2Mock.getBrandCode()).thenReturn(BrandCode.PHUB);


        when(newsletterPreferencesUpdateRequestMock.getSubscriptions()).thenReturn(asList(subscription1Mock, subscription2Mock));


        //When
        List<SubscriptionData> result = sut.mapSubscriptionData(newsletterPreferencesUpdateRequestMock);


        //Then
        assertNotNull(result);
        assertEquals(2, result.size());
        SubscriptionData subscriptionData1 = result.get(0);
        SubscriptionData subscriptionData2 = result.get(1);
        assertNotNull(subscriptionData1);
        assertNotNull(subscriptionData2);
        assertEquals(BrandCode.PINN.name(), subscriptionData1.getBrandCode());
        assertEquals(BrandCode.PHUB.name(), subscriptionData2.getBrandCode());
        assertEquals(ContactType.Email.name(), subscriptionData1.getContactChannelType());
        assertEquals(ContactType.Telephone.name(), subscriptionData2.getContactChannelType());
        assertEquals(CONTACT_VALUE_1, subscriptionData1.getContactChannelValue());
        assertEquals(CONTACT_VALUE_2, subscriptionData2.getContactChannelValue());
        assertTrue(subscriptionData1.getContactChannelPermission());
        assertFalse(subscriptionData2.getContactChannelPermission());
    }

    @Test
    void shouldConvertToCustomerHubNewsletterPrefUpdateReq() {

        //Given
        doReturn(asList(subscriptionData1Mock, subscriptionData2Mock)).when(sut).mapSubscriptionData(newsletterPreferencesUpdateRequestMock);

        //When
        CustomerHubNewsletterPreferencesUpdateRequest result = sut.convertToCustomerHubNewsletterPrefUpdateReq(CUSTOMER_ID, newsletterPreferencesUpdateRequestMock);

        //Then
        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getCustomerId());
        assertEquals(SOURCE_SYSTEM, result.getSourceSystem());
        assertNotNull(result.getUpdateDateTime());
        assertEquals(asList(subscriptionData1Mock, subscriptionData2Mock), result.getSubscriptionData());
        assertEquals(CORRELATION_ID, result.getCorrelationId());

    }

    @Test
    void shouldConvertToCustomerHubNewsletterPreferencesEditRequest() {

        //Given
        doReturn(asList(editSubscriptionData1Mock, editSubscriptionData2Mock)).when(sut).mapEditSubscriptionData(newsletterPreferencesEditRequestMock);
        doReturn(CUSTOMER_ID).when(newsletterPreferencesEditRequestMock).getCustomerId();

        //When
        CustomerHubNewsletterPreferencesEditRequest result = sut.convertToCustomerHubNewsletterPrefEditReq(newsletterPreferencesEditRequestMock);

        //Then
        assertNotNull(result);
        assertEquals(CUSTOMER_ID, result.getCustomerId());
        assertEquals(SOURCE_SYSTEM, result.getSourceSystem());
        assertNotNull(result.getPermissionDateTime());
        assertEquals(asList(editSubscriptionData1Mock, editSubscriptionData2Mock), result.getSubscriptionData());
    }

    @Test
    void shouldMapEditSubscriptionData() {

        setUpData();

        List<EditSubscriptionData> result = sut.mapEditSubscriptionData(newsletterPreferencesEditRequestMock);

        assertNotNull(result);
        assertEquals(2, result.size());
        EditSubscriptionData subscriptionData1 = result.get(0);
        EditSubscriptionData subscriptionData2 = result.get(1);
        assertNotNull(subscriptionData1);
        assertNotNull(subscriptionData2);

        assertArrayEquals(BRAND_CODES, subscriptionData1.getBrandCodes());
        assertEquals(CONTACT_TYPE_1, subscriptionData1.getContactChannelType());
        assertEquals(CONTACT_ID_1, subscriptionData1.getContactChannelId());
        assertNull(subscriptionData1.getContactChannelValue());
        assertNull(subscriptionData1.getContentPermission());

        assertArrayEquals(BRAND_CODES, subscriptionData2.getBrandCodes());
        assertEquals(CONTACT_TYPE_2, subscriptionData2.getContactChannelType());
        assertEquals(CONTACT_ID_2, subscriptionData2.getContactChannelId());
        assertNull(subscriptionData2.getContactChannelValue());
        assertEquals(CONTENT_PERMISSION_DATA, subscriptionData2.getContentPermission());
        assertTrue(subscriptionData1.getContactChannelPermission());
        assertFalse(subscriptionData2.getContactChannelPermission());
    }

    private void setUpData() {
        when(editSubscription1Mock.getContactChannelType()).thenReturn(ContactType.Email);
        when(editSubscription1Mock.getContactChannelValue()).thenReturn(CONTACT_VALUE_1);
        when(editSubscription1Mock.getContactChannelId()).thenReturn(CONTACT_ID_1);
        when(editSubscription1Mock.getContactChannelPermission()).thenReturn(TRUE);
        when(editSubscription1Mock.getBrandCodes()).thenReturn(BRAND_CODES);
        when(editSubscription1Mock.getContentPermission()).thenReturn(null);

        when(editSubscription2Mock.getContactChannelType()).thenReturn(ContactType.Telephone);
        when(editSubscription2Mock.getContactChannelValue()).thenReturn(CONTACT_VALUE_2);
        when(editSubscription2Mock.getContactChannelId()).thenReturn(CONTACT_ID_2);
        when(editSubscription2Mock.getContactChannelPermission()).thenReturn(FALSE);
        when(editSubscription2Mock.getBrandCodes()).thenReturn(BRAND_CODES);
        when(editSubscription2Mock.getContentPermission()).thenReturn(CONTENT_PERMISSION);

        when(newsletterPreferencesEditRequestMock.getSubscriptionData()).thenReturn(asList(editSubscription1Mock, editSubscription2Mock));
    }

    @Test
    void shouldConvertToCustomerHubNewsletterPreferencesGetRequest() {

        //Given
        doReturn(REQUEST_ID).when(newsletterPreferencesGetRequestMock).getRequestId();
        doReturn(BRAND_CODES).when(newsletterPreferencesGetRequestMock).getBrandCodes();
        doReturn(CONTACT_CHANNEL).when(newsletterPreferencesGetRequestMock).getContactChannel();

        //When
        CustomerHubNewsletterPreferencesGetRequest result = sut.convertToCustomerHubNewsletterPreferencesGetRequest(newsletterPreferencesGetRequestMock);

        //Then
        assertNotNull(result);
        assertEquals(REQUEST_ID, result.getRequestId());
        assertEquals(SOURCE_SYSTEM, result.getSourceSystem());
        assertNotNull(result.getRequestedDateTime());
        assertEquals(CONTACT_CHANNEL_DATA, result.getContactChannel());
    }

    @Test
    void shouldConvertLegacyBrandPermissionToBrandPermission() {

        //Given
        BrandPermission cdhBrandPermission = getCdhBrandPermission();

        final Permission expectedResult = getBrandPermission();

        //When
        final List<Permission> result = sut.convertBrandPermissions(singletonList(cdhBrandPermission));

        //Then
        Assertions.assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldConvertLegacyBrandPermissionToBrandPermissionWhenContentPermissionDataIsNull() {

        //Given
        var cdhBrandPermission = new BrandPermission();
        cdhBrandPermission.setBrandCode("PINN");
        cdhBrandPermission.setOptIn(true);
        cdhBrandPermission.setSecondOptInReq(true);
        cdhBrandPermission.setSecondOptIn(false);

        final Permission expectedResult = Permission
                .builder()
                .brandCode("PINN")
                .optIn(true)
                .secondPartyOptIn(false)
                .thirdPartyVendorsOptIn(false)
                .secondOptInReq(true)
                .secondOptIn(false)
                .build();

        //When
        final List<Permission> result = sut.convertBrandPermissions(singletonList(cdhBrandPermission));

        //Then
        Assertions.assertThat(result.getFirst()).usingRecursiveComparison().isEqualTo(expectedResult);
    }

    @Test
    void shouldConvertCustomerHubToPreferencesGetResponse() {
        LoyaltyAccount loyaltyAccount = new LoyaltyAccount();
        loyaltyAccount.setLoyaltyBrand("Premmier Inn");
        loyaltyAccount.setLoyaltySystemId("12345");

        NewsletterPreferencesGetResponse cdhResponse = new NewsletterPreferencesGetResponse();
        cdhResponse.setBrandPermissions(singletonList(getCdhBrandPermission()));
        cdhResponse.setDoNotContact("true");
        cdhResponse.setDeleted(false);
        cdhResponse.setValid(true);
        cdhResponse.setLoyaltyAccounts(singletonList(loyaltyAccount));

        PreferencesGetResponse expected = PreferencesGetResponse
                .builder()
                .permissions(singletonList(getBrandPermission()))
                .deleted(false)
                .loyaltyAccounts(singletonList(loyaltyAccount))
                .build();
        final PreferencesGetResponse result = sut.convertCustomerHubToPreferencesGetResponse(cdhResponse);

        Assertions.assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldConvertToCustomerHubPreferencesGetRequest() {
        final LocalDateTime now = LocalDateTime.now();
        final String requestId = UUID.randomUUID().toString();
        PreferencesGetRequest request = PreferencesGetRequest
                .builder()
                .brandCodes(new String[]{"PINN","BREW"})
                .contactType(uk.co.whitbread.marketing.model.newsletter.ContactType.email)
                .contactValue("test@validemail.com")
                .build();

        ContactChannelData contactChannelData = new ContactChannelData();
        contactChannelData.setContactChannelType("Email");
        contactChannelData.setContactChannelValue("test@validemail.com");

        CustomerHubNewsletterPreferencesGetRequest expected = CustomerHubNewsletterPreferencesGetRequest
                .builder()
                .brandCodes(new String[]{"PINN","BREW"})
                .requestedDateTime(now)
                .requestId(requestId)
                .sourceSystem("ACE")
                .ContactChannel(contactChannelData)
                .build();

        when(customerHubPropertiesLegacyMock.getSourceSystem()).thenReturn("ACE");

        final CustomerHubNewsletterPreferencesGetRequest result = sut.convertToCustomerHubPreferencesGetRequest(request);
        Assertions.assertThat(result).usingRecursiveComparison()
            .ignoringFields("requestedDateTime","requestId").isEqualTo(expected);
    }

    @Test
    void convertToCustomerHubPreferencesEditRequest() {
        PreferencesEditRequest editRequest = new PreferencesEditRequest();
        editRequest.setBrandCodes(new String[]{"PINN"});
        editRequest.setOptIn(true);
        editRequest.setSecondPartyOptIn(true);
        editRequest.setThirdPartyVendorsOnpIn(true);
        editRequest.setUserId("test.userid");
        editRequest.setContactSubType(ContactSubType.landline);

        ContentPermissionData contentPermissionData = new ContentPermissionData();
        contentPermissionData.setSecondParty(true);
        contentPermissionData.setThirdParty(true);
        EditSubscriptionData editSubscriptionData = new EditSubscriptionData();
        editSubscriptionData.setContactChannelType("Telephone");
        editSubscriptionData.setContactChannelSubType("Landline");
        editSubscriptionData.setContactChannelPermission(true);
        editSubscriptionData.setContactChannelValue("07777777777");
        editSubscriptionData.setBrandCodes(new String[]{"PINN"});
        editSubscriptionData.setContentPermission(contentPermissionData);
        final CustomerHubNewsletterPreferencesEditRequest expected = CustomerHubNewsletterPreferencesEditRequest.builder()
                .userId("test.userid")
                .subscriptionData(singletonList(editSubscriptionData))
                .sourceSystem("source-system-test")
                .build();

        final CustomerHubNewsletterPreferencesEditRequest result = sut.convertToCustomerHubPreferencesEditRequest(editRequest, "07777777777", "Telephone");

        Assertions.assertThat(result).usingRecursiveComparison().ignoringFields("permissionDateTime").isEqualTo(expected);
    }

    @Test
    void convertToCustomerHubPreferencesEditRequestWithoutSubType() {
        PreferencesEditRequest editRequest = new PreferencesEditRequest();
        editRequest.setBrandCodes(new String[]{"PINN"});
        editRequest.setOptIn(true);
        editRequest.setSecondPartyOptIn(true);
        editRequest.setThirdPartyVendorsOnpIn(true);
        editRequest.setUserId("test.userid");

        ContentPermissionData contentPermissionData = new ContentPermissionData();
        contentPermissionData.setSecondParty(true);
        contentPermissionData.setThirdParty(true);
        EditSubscriptionData editSubscriptionData = new EditSubscriptionData();
        editSubscriptionData.setContactChannelType("Email");
        editSubscriptionData.setContactChannelPermission(true);
        editSubscriptionData.setContactChannelValue("test@test.com");
        editSubscriptionData.setBrandCodes(new String[]{"PINN"});
        editSubscriptionData.setContentPermission(contentPermissionData);
        final CustomerHubNewsletterPreferencesEditRequest expected = CustomerHubNewsletterPreferencesEditRequest.builder()
                .userId("test.userid")
                .subscriptionData(singletonList(editSubscriptionData))
                .sourceSystem("source-system-test")
                .build();

        final CustomerHubNewsletterPreferencesEditRequest result = sut.convertToCustomerHubPreferencesEditRequest(editRequest, "test@test.com", "Email");

        Assertions.assertThat(result).usingRecursiveComparison().ignoringFields("permissionDateTime").isEqualTo(expected);
    }

    private uk.co.whitbread.marketing.model.BrandPermission getCdhBrandPermission() {
        final ContentPermissionData contentPermissionData = new ContentPermissionData();
        contentPermissionData.setSecondParty(true);
        contentPermissionData.setThirdParty(false);

        var cdhBrandPermission = new uk.co.whitbread.marketing.model.BrandPermission();
        cdhBrandPermission.setBrandCode("PINN");
        cdhBrandPermission.setBrand("Premmier Inn");
        cdhBrandPermission.setOptIn(true);
        cdhBrandPermission.setSecondOptInReq(true);
        cdhBrandPermission.setSecondOptIn(false);
        cdhBrandPermission.setContentPermission(contentPermissionData);
        return cdhBrandPermission;
    }

    private Permission getBrandPermission() {
        return Permission
                .builder()
                .brandCode("PINN")
                .brand("Premmier Inn")
                .optIn(true)
                .secondPartyOptIn(true)
                .thirdPartyVendorsOptIn(false)
                .secondOptInReq(true)
                .secondOptIn(false)
                .build();
    }

}
