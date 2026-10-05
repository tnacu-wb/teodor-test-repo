package uk.co.whitbread.marketing.utils;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.marketing.client.customerhub.model.ContentPermissionData;
import uk.co.whitbread.marketing.client.permissionmanagement.model.PermissionManagementGetResponse;
import uk.co.whitbread.marketing.model.BrandPermission;
import uk.co.whitbread.marketing.model.CustomerCountryOfResidence;
import uk.co.whitbread.marketing.model.SourceSystemFixture;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.Customer;
import uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.properties.PermissionManagementApiProperties;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PermissionManagementTransformerTest {

    private static final String SOURCE_SYSTEM = "Whitbread-Digital";
    static final String CONTACT_CHANNEL_ID = "CHNL286_5d753e6e-53d7-4727-9172-1b9fd3df3acf";
    private PermissionManagementTransformer sut;

    @Mock
    private PermissionManagementApiProperties permissionManagementApiPropertiesMock;


    @BeforeEach
    void setUp() {
        when(permissionManagementApiPropertiesMock.getSourceSystem()).thenReturn(SOURCE_SYSTEM);
        sut = spy(new PermissionManagementTransformer(permissionManagementApiPropertiesMock));
    }

    @Test
    void testTransformToPermissionManagementGetRequest() {
        PreferencesGetRequest preferencesGetRequest =
                PreferencesGetRequest
                        .builder()
                        .business(false)
                        .contactType(ContactType.email)
                        .contactValue("liam.wilson@whitbread.com")
                        .brandCodes(new String[]{"PINN"})
                        .build();
        var permissionManagementGetRequest = sut.transformToPermissionManagementGetRequest(preferencesGetRequest);
        assertEquals("PINN", permissionManagementGetRequest.getBrandCodes()[0]);
        assertEquals("Email", permissionManagementGetRequest.getContactChannel().getContactChannelType());
        assertEquals("liam.wilson@whitbread.com", permissionManagementGetRequest.getContactChannel().getContactChannelValue());
        assertEquals(SOURCE_SYSTEM, permissionManagementGetRequest.getSourceSystem());
    }

    @Test
    void tesToPermissionManagementGetRequest() {
      var brandCode = "PINN";
      var emailAddress = "john.doe@whitbread.com";
      var request = PreferencesAnonymousGetRequest.builder()
              .emailAddress(emailAddress)
              .brandCode(brandCode)
              .build();
      var permissionManagementGetRequest = sut.toPermissionManagementGetRequest(request);
      assertEquals(brandCode, permissionManagementGetRequest.getBrandCodes()[0]);
      assertEquals(ContactType.email.getType(), permissionManagementGetRequest.getContactChannel().getContactChannelType());
      assertEquals(emailAddress, permissionManagementGetRequest.getContactChannel().getContactChannelValue());
      assertEquals(SOURCE_SYSTEM, permissionManagementGetRequest.getSourceSystem());
    }

    @Test
    void toPreferencesAnonymousGetResponse() {
      var response = new PermissionManagementGetResponse();
      var permission = new BrandPermission();
      permission.setOptIn(true);
      permission.setSecondOptIn(true);
      response.setBrandPermissions(List.of(permission));
      var preferencesAnonymousGetResponse = sut.toPreferencesAnonymousGetResponse(response, "gb", "en");
      assertTrue(preferencesAnonymousGetResponse.isOptIn());
      assertTrue(preferencesAnonymousGetResponse.isSecondOptIn());
      assertFalse(preferencesAnonymousGetResponse.isSecondOptInReq());
      assertFalse(preferencesAnonymousGetResponse.isSecondPartyOptIn());
      assertFalse(preferencesAnonymousGetResponse.isThirdPartyVendorsOptIn());
    }

  @ParameterizedTest
  @MethodSource("provideTestCasesForMarketingCheckbox")
  void testToPreferencesAnonymousGetResponseSuppressCheckbox(String language,
      String countryOfResidence,
      boolean optIn, List<String> customerLinks, boolean expectedSuppress) {

    var brandPermission = new BrandPermission();
    brandPermission.setOptIn(optIn);
    brandPermission.setSecondOptIn(true);
    brandPermission.setSecondOptInReq(true);
    brandPermission.setContentPermission(new ContentPermissionData(true, true));

    var permissionResponse = new PermissionManagementGetResponse();
    permissionResponse.setBrandPermissions(List.of(brandPermission));

    if (customerLinks != null) {
      permissionResponse.setCustomerLinks(customerLinks.stream()
          .map(c -> CustomerCountryOfResidence.builder().countryOfResidence(c).build())
          .toList());
    }

    var result = sut.toPreferencesAnonymousGetResponse(permissionResponse, countryOfResidence,
        language);

    assertEquals(expectedSuppress, result.isSuppressMarketingCheckbox());
  }

  @ParameterizedTest
  @MethodSource("provideTestCasesForMarketingCheckbox")
  void testConvertPermissionManagementResponseSuppressCheckbox(String language, String countryOfResidence,
      boolean optIn, List<String> customerLinks, boolean expectedSuppress) {

    var brandPermission = new BrandPermission();
    brandPermission.setOptIn(optIn);
    brandPermission.setSecondOptIn(true);
    brandPermission.setSecondOptInReq(true);
    brandPermission.setContentPermission(new ContentPermissionData(true, true));

    var permissionResponse = new PermissionManagementGetResponse();
    permissionResponse.setBrandPermissions(List.of(brandPermission));

    if (customerLinks != null) {
      permissionResponse.setCustomerLinks(customerLinks.stream()
          .map(c -> CustomerCountryOfResidence.builder().countryOfResidence(c).build())
          .toList());
    }

    var result = sut.convertPermissionManagementResponseToGetResponse(permissionResponse, countryOfResidence,
        language);

    assertEquals(expectedSuppress, result.getPermissions().getFirst().isSuppressMarketingCheckbox());
  }

  static Stream<Arguments> provideTestCasesForMarketingCheckbox() {
    return Stream.of(
        // DE language, optIn true/false
        of("de", "de", true, List.of(), false),
        of("de", "de", false, List.of(), true),
        of("de", "fr", true, List.of("fr"), false),
        of("de", "fr", false, List.of("fr"), true),
        of("de", "fr", true, List.of("de"), false),
        of("de", "fr", false, List.of("de"), true),

        // EN language, optIn true, direct DE country
        of("en", "de", true, List.of(), false),
        // EN language, optIn false, direct DE country
        of("en", "de", false, List.of(), true),

        // EN language, optIn true, DE country in customer links
        of("en", "fr", true, List.of("de"), false),
        // EN language, optIn false, DE country in customer links
        of("en", "fr", false, List.of("de"), true),

        // EN language, optIn true, no DE anywhere
        of("en", "fr", true, List.of("fr"), true),
        of("en", "fr", true, List.of(), true),

        // EN language, optIn false, no DE anywhere
        of("en", "fr", false, List.of("fr"), false),
        of("en", "fr", false, List.of(), false),

        // EN language, customerLinks null, optIn true/false
        of("en", "de", true, null, false),
        of("en", "de", false, null, true),
        of("en", "fr", true, null, true),
        of("en", "fr", false, null, false),

        // Non DE/EN language fallback tests (default case)
        of("fr", "de", true, List.of(), false),
        of("fr", "fr", false, List.of("de"), false),
        of("es", "de", false, List.of("fr"), false),

        // Null or empty countryOfResidence (defaulting to GB internally)
        // treated as GB + DE customer link
        of("en", null, true, List.of("de"), false),
        // treated as GB without DE anywhere
        of("en", "", false, List.of("fr"), false),
        // GB without DE anywhere
        of("en", null, false, null, false),

        // Null or empty language (defaulting to EN internally)
        // language default EN, country DE
        of(null, "de", true, List.of(), false),
        // language default EN, DE in customer links
        of("", "fr", false, List.of("de"), true),
        // default EN language, default GB country, optIn true, no DE anywhere
        of(null, null, true, null, true),
        // default EN language, default GB country, optIn false, no DE anywhere
        of("", "", false, List.of(), false)
    );
  }

    @Test
    void testTransformToPermissionManagementUpdatePermissionsRequest() {
        Customer customer = Customer
                .builder()
                .nationality("GB")
                .countryOfResidence("DE")
                .firstName("Liam")
                .lastName("Wilson")
                .title("Mr")
                .userId("liam.wilson1234")
                .language("en")
                .build();
        UpdatePreferencesRequest updatePreferencesRequest =
                UpdatePreferencesRequest
                        .builder()
                        .secondPartyOptIn(true)
                        .thirdPartyVendorsOptIn(false)
                        .brandCodes(new String[]{"PINN"})
                        .optIn(true)
                        .doubleOptIn(true)
                        .customer(customer)
                        .build();
        var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(updatePreferencesRequest, ContactType.email, "liam.wilson@whitbread.co.uk");
        assertEquals("DE", permissionManagementUpdateRequest.getCountryOfResidence());
        assertTrue(permissionManagementUpdateRequest.isOptIn());
        assertTrue(permissionManagementUpdateRequest.isSecondPartyContent());
        assertFalse(permissionManagementUpdateRequest.isThiryPartyContent());
        assertEquals("liam.wilson@whitbread.co.uk", permissionManagementUpdateRequest.getEmail());
        assertEquals("Liam", permissionManagementUpdateRequest.getFirstName());
        assertEquals("liam.wilson1234", permissionManagementUpdateRequest.getUserId());
        assertEquals(SOURCE_SYSTEM, permissionManagementUpdateRequest.getSourceSystem());
    }

    @Test
    void testTransformToPermissionManagementUpdatePermissionsRequestFiltersPINN() {
        Customer customer = Customer
                .builder()
                .nationality("GB")
                .countryOfResidence("DE")
                .firstName("Liam")
                .lastName("Wilson")
                .title("Mr")
                .userId("liam.wilson1234")
                .language("en")
                .build();
        UpdatePreferencesRequest updatePreferencesRequest =
                UpdatePreferencesRequest
                        .builder()
                        .secondPartyOptIn(true)
                        .thirdPartyVendorsOptIn(false)
                        .brandCodes(new String[]{"PINN", "BARB", "BREW"})
                        .optIn(true)
                        .doubleOptIn(true)
                        .customer(customer)
                        .build();
        var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(updatePreferencesRequest, ContactType.email, "liam.wilson@whitbread.co.uk");
        assertEquals("DE", permissionManagementUpdateRequest.getCountryOfResidence());
        assertTrue(permissionManagementUpdateRequest.isOptIn());
        assertTrue(permissionManagementUpdateRequest.isSecondPartyContent());
        assertFalse(permissionManagementUpdateRequest.isThiryPartyContent());
        assertEquals("liam.wilson@whitbread.co.uk", permissionManagementUpdateRequest.getEmail());
        assertEquals("Liam", permissionManagementUpdateRequest.getFirstName());
        assertEquals("liam.wilson1234", permissionManagementUpdateRequest.getUserId());
        assertEquals(1, permissionManagementUpdateRequest.getBrandCode().length);
        assertEquals("PINN",Arrays.stream(permissionManagementUpdateRequest.getBrandCode()).findFirst().get());
        assertEquals(SOURCE_SYSTEM, permissionManagementUpdateRequest.getSourceSystem());
    }

    @Test
    void testTransformToPermissionManagementUpdatePermissionsRequestTypePhone() {
        Customer customer = Customer
                .builder()
                .nationality("GB")
                .countryOfResidence("DE")
                .firstName("Liam")
                .lastName("Wilson")
                .title("Mr")
                .userId("liam.wilson1234")
                .language("en")
                .build();
        UpdatePreferencesRequest updatePreferencesRequest =
                UpdatePreferencesRequest
                        .builder()
                        .secondPartyOptIn(true)
                        .thirdPartyVendorsOptIn(false)
                        .brandCodes(new String[]{"PINN"})
                        .optIn(true)
                        .doubleOptIn(true)
                        .customer(customer)
                        .build();
        var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(updatePreferencesRequest, ContactType.phone, "07891194022");
        assertEquals("DE", permissionManagementUpdateRequest.getCountryOfResidence());
        assertTrue(permissionManagementUpdateRequest.isOptIn());
        assertTrue(permissionManagementUpdateRequest.isSecondPartyContent());
        assertFalse(permissionManagementUpdateRequest.isThiryPartyContent());
        assertNull(permissionManagementUpdateRequest.getEmail());
        assertEquals("Liam", permissionManagementUpdateRequest.getFirstName());
        assertEquals("liam.wilson1234", permissionManagementUpdateRequest.getUserId());
        assertEquals(SOURCE_SYSTEM, permissionManagementUpdateRequest.getSourceSystem());
    }
    
    @Test
    void testUpdatePermissionsRequestSourceSystem() {
    	UpdatePreferencesRequest updatePreferencesRequest = SourceSystemFixture.buildRequestSourceSystemWEB();
    	
		var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");
		
		// Locale: UK ,Journey : PermissionManagement, Channel : WEB
		assertEquals("PERMISSIONCENTRE_WEB_UK", permissionManagementUpdateRequest.getSourceSystem());
    }

	@Test
	void testUpdatePermissionsRequestSourceSystemBB() {
		// Locale: DE ,Journey : SignUp, Channel : BB
		UpdatePreferencesRequest updatePreferencesRequest = SourceSystemFixture.buildRequestSourceSystemBB();
    	
		var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");
		assertEquals("SIGNUP_BB_DE", permissionManagementUpdateRequest.getSourceSystem());

	}

	@Test
	void testUpdatePermissionsRequestSourceSystemMobile() {
		
		UpdatePreferencesRequest updatePreferencesRequest = SourceSystemFixture.buildRequestSourceSystemMobileAndroid();
    	
		
		var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");
		assertEquals("NEWSLETTERSIGNUP_APPS_ANDROID_UK", permissionManagementUpdateRequest.getSourceSystem());

	}
    
    @Test
    void testUpdatePermissionsRequestSourceSystemMobileiOS() {
    	
    	UpdatePreferencesRequest updatePreferencesRequest = SourceSystemFixture.buildRequestSourceSystemMobileiOS();
    	
		var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");
		assertEquals("SIGNUP_APPS_IOS_DE", permissionManagementUpdateRequest.getSourceSystem());
		
    }

	@Test
	void testUpdatePermissionsRequest() {

		UpdatePreferencesRequest updatePreferencesRequest = SourceSystemFixture.buildBasePreferencesRequest();
    	
		var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");

		// Here SourceSytem should be Whitbread-Digital as Sourcedetails not provided by
		// customer
		assertEquals(SOURCE_SYSTEM, permissionManagementUpdateRequest.getSourceSystem());

	}

  @Test
  void testTransformToPermissionManagementUpdatePermissionsRequestTypeEmailContactChannelId() {
    Customer customer = Customer
        .builder()
        .nationality("GB")
        .countryOfResidence("DE")
        .firstName("Liam")
        .lastName("Wilson")
        .title("Mr")
        .userId("liam.wilson1234")
        .language("en")
        .build();
    UpdatePreferencesRequest updatePreferencesRequest =
        UpdatePreferencesRequest
            .builder()
            .secondPartyOptIn(true)
            .thirdPartyVendorsOptIn(false)
            .brandCodes(new String[]{"PINN"})
            .optIn(true)
            .doubleOptIn(true)
            .customer(customer)
            .build();
    var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(updatePreferencesRequest, ContactType.email_contact_channel_id, CONTACT_CHANNEL_ID);
    assertEquals("DE", permissionManagementUpdateRequest.getCountryOfResidence());
    assertTrue(permissionManagementUpdateRequest.isOptIn());
    assertTrue(permissionManagementUpdateRequest.isSecondPartyContent());
    assertFalse(permissionManagementUpdateRequest.isThiryPartyContent());
    assertNull(permissionManagementUpdateRequest.getEmail());
    assertEquals(CONTACT_CHANNEL_ID, permissionManagementUpdateRequest.getEmailContactChannelId());
    assertEquals("Liam", permissionManagementUpdateRequest.getFirstName());
    assertEquals("liam.wilson1234", permissionManagementUpdateRequest.getUserId());
    assertEquals(SOURCE_SYSTEM, permissionManagementUpdateRequest.getSourceSystem());
  }


    @Test
    void testUpdatePermissionsRequestDoubleOptIn() {
        Customer customer = Customer
                .builder()
                .nationality("GB")
                .countryOfResidence("DE")
                .firstName("Liam")
                .lastName("Wilson")
                .title("Mr")
                .userId("liam.wilson1234")
                .language("en")
                .build();
        UpdatePreferencesRequest updatePreferencesRequest =
                UpdatePreferencesRequest
                        .builder()
                        .secondPartyOptIn(true)
                        .thirdPartyVendorsOptIn(false)
                        .brandCodes(new String[]{"PINN"})
                        .optIn(true)
                        .doubleOptIn(true)
                        .customer(customer)
                        .build();
		var permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");
		
		

		// Language:English and DoubleOptIn: true//Invalid Language
		assertEquals("English", permissionManagementUpdateRequest.getSourceLanguage());
		assertTrue(permissionManagementUpdateRequest.isSecondOptInReq());
		
		updatePreferencesRequest.setDoubleOptIn(false);
		updatePreferencesRequest.getCustomer().setLanguage("en");
		permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");

		
		// Language:English and DoubleOptIn: false
		assertEquals("English", permissionManagementUpdateRequest.getSourceLanguage());
		assertFalse(permissionManagementUpdateRequest.isSecondOptInReq());
		
		updatePreferencesRequest.setDoubleOptIn(false);
		updatePreferencesRequest.getCustomer().setLanguage("de");
		permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");
		
		
		// Language:German and DoubleOptIn: false
		assertEquals("German", permissionManagementUpdateRequest.getSourceLanguage());
		assertFalse(permissionManagementUpdateRequest.isSecondOptInReq());
		
		updatePreferencesRequest.setDoubleOptIn(true);
		updatePreferencesRequest.getCustomer().setLanguage("de");
		permissionManagementUpdateRequest = sut.transformToPermissionManagementUpdatePermissionsRequest(
				updatePreferencesRequest, ContactType.phone, "07891194022");

		// Language:German and DoubleOptIn: True
		assertEquals("German", permissionManagementUpdateRequest.getSourceLanguage());
		assertTrue(permissionManagementUpdateRequest.isSecondOptInReq());
		
    }

    @Test
    void testTransformToPermissionManagementConfirmDoubleOptInRequest() {
        ConfirmDoubleOptInRequest confirmDoubleOptInRequest =
                ConfirmDoubleOptInRequest
                        .builder()
                        .customerId("customerId")
                        .brandCodes(new String []{"PINN"})
                        .build();
        var permissionManagementConfirmDoubleOptIn = sut.transformToPermissionManagementConfirmDoubleOptInRequest(confirmDoubleOptInRequest, ContactType.email, CONTACT_CHANNEL_ID);
        assertEquals("customerId", permissionManagementConfirmDoubleOptIn.getCustomerId());
        assertEquals(CONTACT_CHANNEL_ID, permissionManagementConfirmDoubleOptIn.getSubscriptionData()[0].getContactChannelId());
        assertEquals("Email", permissionManagementConfirmDoubleOptIn.getSubscriptionData()[0].getContactChannelType());
        assertEquals("PINN", permissionManagementConfirmDoubleOptIn.getSubscriptionData()[0].getBrandCodes()[0]);
        assertEquals(SOURCE_SYSTEM, permissionManagementConfirmDoubleOptIn.getSourceSystem());
    }

    @Test
    void testTransformToPermissionManagementConfirmDoubleOptInRequestFilterPinn() {
        ConfirmDoubleOptInRequest confirmDoubleOptInRequest =
                ConfirmDoubleOptInRequest
                        .builder()
                        .customerId("customerId")
                        .brandCodes(new String[]{"PINN", "BARB", "BREW"})
                        .build();
        var permissionManagementConfirmDoubleOptIn = sut.transformToPermissionManagementConfirmDoubleOptInRequest(confirmDoubleOptInRequest, ContactType.email, CONTACT_CHANNEL_ID);
        assertEquals("customerId", permissionManagementConfirmDoubleOptIn.getCustomerId());
        assertEquals(CONTACT_CHANNEL_ID, permissionManagementConfirmDoubleOptIn.getSubscriptionData()[0].getContactChannelId());
        assertEquals("Email", permissionManagementConfirmDoubleOptIn.getSubscriptionData()[0].getContactChannelType());
        assertEquals("PINN", permissionManagementConfirmDoubleOptIn.getSubscriptionData()[0].getBrandCodes()[0]);
        assertEquals(1, permissionManagementConfirmDoubleOptIn.getSubscriptionData().length);
        assertEquals(SOURCE_SYSTEM, permissionManagementConfirmDoubleOptIn.getSourceSystem());
    }

    @Test
    void testTransformToPermissionManagementUnsubscribeRequest() {
        UnsubscribeRequest unsubscribeRequest =
                UnsubscribeRequest
                        .builder()
                        .customerId("customerId")
                        .brandCodes(new String []{"PINN"})
                        .build();
        var permissionManagementUnsubscribeRequest = sut.transformToPermissionManagementUnsubscribeRequest(unsubscribeRequest, ContactType.email, CONTACT_CHANNEL_ID);
        assertEquals("customerId", permissionManagementUnsubscribeRequest.getCustomerId());
        assertEquals(CONTACT_CHANNEL_ID, permissionManagementUnsubscribeRequest.getSubscriptionData()[0].getContactChannelId());
        assertEquals("Email", permissionManagementUnsubscribeRequest.getSubscriptionData()[0].getContactChannelType());
        assertEquals("PINN", permissionManagementUnsubscribeRequest.getSubscriptionData()[0].getBrandCodes()[0]);
        assertEquals(SOURCE_SYSTEM, permissionManagementUnsubscribeRequest.getSourceSystem());
    }

}
