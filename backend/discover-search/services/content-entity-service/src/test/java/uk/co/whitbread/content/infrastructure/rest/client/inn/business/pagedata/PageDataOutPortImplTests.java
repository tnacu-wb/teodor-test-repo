package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_PAGE_DATA_EXCEPTION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.DictionaryEnum;
import uk.co.whitbread.content.domain.model.inn.business.pagedata.in.PageDataRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.content.exceptions.ContentException;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.aem.PageDataAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.mapper.PageDataRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.PageDataRequestDto;

@ExtendWith(MockitoExtension.class)
class PageDataOutPortImplTests {

  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";

  @InjectMocks
  private PageDataOutPortImpl pageDataOutPort;

  @Mock
  private PageDataAemClient pageDataAemClient;

  @Mock
  private PageDataRequestMapper pageDataRequestMapper;

  private final Exception exception = new Exception();

  @Test
  void getPageData_ShouldReturnOK() {
    //given
    when(pageDataAemClient.getPageData((any(PageDataRequestDto.class)))).thenReturn(
        mockPageDataAEMResponse());
    when(pageDataRequestMapper.toDtoModel(any())).thenReturn(new PageDataRequestDto());

    //when
    var pageDataAemResponse = pageDataOutPort.getPageData(PageDataRequest.builder().dictionaries(
            of(DictionaryEnum.LAYOUT_DICTIONARY, DictionaryEnum.CARD_MANAGEMENT_DICTIONARY,
                DictionaryEnum.COMMON_ICONS_DICTIONARY, DictionaryEnum.USER_MANAGEMENT_DICTIONARY,
                DictionaryEnum.PROFILE_MANAGEMENT_DICTIONARY, DictionaryEnum.COMPANY_MANAGEMENT_DICTIONARY,
                DictionaryEnum.PAY_APPLICATION_DICTIONARY, DictionaryEnum.AUTH_DICTIONARY,
                DictionaryEnum.NOTIFICATIONS_DICTIONARY, DictionaryEnum.CONTACT_US_DICTIONARY
            ))
        .country(COUNTRY_GB).language(LANGUAGE_EN)
        .build());

    //then
    assertThat(pageDataAemResponse, notNullValue());
    assertThat(pageDataAemResponse, aMapWithSize(10));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("layoutEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("cardManagementEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("commonIconsEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("userManagementEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("profileManagementEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("companyManagementEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("payApplicationEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("authEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("notificationsEndpoint"));
    assertThat(pageDataAemResponse, IsMapContaining.hasKey("innbContactUsEndpoint"));

    assertThat(pageDataAemResponse.get("layoutEndpoint"),
        IsMapContaining.hasEntry("innbusinessLayout.manageAccount.cards.title", "Cards"));
    assertThat(pageDataAemResponse.get("layoutEndpoint"),
        IsMapContaining.hasEntry("innbusinessLayout.menu.spending.icon.active",
            "innbusinessLayout.menu.spending.icon.active"));
    assertThat(pageDataAemResponse.get("layoutEndpoint"),
        IsMapContaining.hasEntry("innbusinessLayout.menu.spending.label", "Spending"));

    assertThat(pageDataAemResponse.get("cardManagementEndpoint"),
        IsMapContaining.hasEntry("cardMgmt.addCard.selectEmployee.label", "Select employee"));
    assertThat(pageDataAemResponse.get("cardManagementEndpoint"),
        IsMapContaining.hasEntry("cardMgmt.activateCard.confirmation.backButton",
            "Back to card management"));
    assertThat(pageDataAemResponse.get("cardManagementEndpoint"),
        IsMapContaining.hasEntry("cardMgmt.columns.yourCard.check",
            "/content/dam/global/icons/common/tick-24.svg"));

    assertThat(pageDataAemResponse.get("commonIconsEndpoint"),
        IsMapContaining.hasEntry("icon.notification.dismiss",
            "/content/dam/global/icons/notifications/dismiss.svg"));
    assertThat(pageDataAemResponse.get("commonIconsEndpoint"),
        IsMapContaining.hasEntry("icon.notification.question",
            "/content/dam/global/icons/notifications/question.svg"));
    assertThat(pageDataAemResponse.get("commonIconsEndpoint"),
        IsMapContaining.hasEntry("icon.chevron.right.purple",
            "/content/dam/global/icons/common/chevron-right-purple.svg"));
    assertThat(pageDataAemResponse.get("userManagementEndpoint"),
            IsMapContaining.hasEntry("userMgmt.employee.edit.review.submit",
                    "Save changes"));
    assertThat(pageDataAemResponse.get("profileManagementEndpoint"),
            IsMapContaining.hasEntry("profile.title",
                    "Your Profile"));
    assertThat(pageDataAemResponse.get("companyManagementEndpoint"),
        IsMapContaining.hasEntry("coMngt.title", "Company details"));
    assertThat(pageDataAemResponse.get("payApplicationEndpoint"),
        IsMapContaining.hasEntry("apply.title", "Apply for InnBusiness Pay"));
    assertThat(pageDataAemResponse.get("authEndpoint"),
        IsMapContaining.hasEntry("auth.title", "Authentication"));
    assertThat(pageDataAemResponse.get("notificationsEndpoint"),
          IsMapContaining.hasEntry("notifications.title", "Notifications"));
    assertThat(pageDataAemResponse.get("innbContactUsEndpoint"),
          IsMapContaining.hasEntry("contactUs.title", "Contact us"));
  }

  @Test
  void getPageData_badInput_ShouldReturnException() {
    //Arrange
    var request = new PageDataRequest("null", "null",
        List.of(DictionaryEnum.LAYOUT_DICTIONARY, DictionaryEnum.CARD_MANAGEMENT_DICTIONARY,
                DictionaryEnum.USER_MANAGEMENT_DICTIONARY, DictionaryEnum.PROFILE_MANAGEMENT_DICTIONARY,
            DictionaryEnum.COMPANY_MANAGEMENT_DICTIONARY));
    var expectedMessage = String.format(
        "No data for this getPageData request for country=%s, language=%s," + " dictionaries=%s",
        request.getCountry(), request.getLanguage(), request.getDictionaries().size());
    when(pageDataRequestMapper.toDtoModel(any())).thenReturn(new PageDataRequestDto());
    when(pageDataAemClient.getPageData(any())).thenReturn(null);

    //Act
    var actual = assertThrows(ContentException.class, () -> pageDataOutPort.getPageData(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(), is(AEM_PAGE_DATA_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is(expectedMessage));
    assertThat(actual.getErrorCode(), is(AEM_PAGE_DATA_EXCEPTION.getCode()));
  }

  @Test
  void getPageData_ShouldReturnException() {
    //Arrange
    var expectedMessage = "Unable to get data.";
    when(pageDataRequestMapper.toDtoModel(any())).thenReturn(new PageDataRequestDto());
    when(pageDataAemClient.getPageData(any())).thenThrow(
        new AemResponseException(AEM_PAGE_DATA_EXCEPTION, "Unable to get data.", exception));
    var request = new PageDataRequest("null", "null",
        List.of(DictionaryEnum.LAYOUT_DICTIONARY, DictionaryEnum.COMMON_ICONS_DICTIONARY,
                DictionaryEnum.USER_MANAGEMENT_DICTIONARY, DictionaryEnum.PROFILE_MANAGEMENT_DICTIONARY,
            DictionaryEnum.COMPANY_MANAGEMENT_DICTIONARY));
    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> pageDataOutPort.getPageData(request));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getMessage(), is(expectedMessage));
  }


  private Map<String, Map<String, String>> mockPageDataAEMResponse() {
    Map<String, Map<String, String>> response = new HashMap<>();

    Map<String, String> layoutResponse = new HashMap<>();
    layoutResponse.put("innbusinessLayout.manageAccount.cards.title", "Cards");
    layoutResponse.put("innbusinessLayout.menu.spending.icon.active",
        "innbusinessLayout.menu.spending.icon.active");
    layoutResponse.put("innbusinessLayout.menu.spending.label", "Spending");
    response.put("layoutEndpoint", layoutResponse);

    Map<String, String> cardManagementResponse = new HashMap<>();
    cardManagementResponse.put("cardMgmt.addCard.selectEmployee.label", "Select employee");
    cardManagementResponse.put("cardMgmt.activateCard.confirmation.backButton",
        "Back to card management");
    cardManagementResponse.put("cardMgmt.columns.yourCard.check",
        "/content/dam/global/icons/common/tick-24.svg");
    response.put("cardManagementEndpoint", cardManagementResponse);

    Map<String, String> commonIconsResponse = new HashMap<>();
    commonIconsResponse.put("icon.notification.dismiss",
        "/content/dam/global/icons/notifications/dismiss.svg");
    commonIconsResponse.put("icon.notification.question",
        "/content/dam/global/icons/notifications/question.svg");
    commonIconsResponse.put("icon.chevron.right.purple",
        "/content/dam/global/icons/common/chevron-right-purple.svg");
    response.put("commonIconsEndpoint", commonIconsResponse);

    Map<String, String> userManagementResponse = new HashMap<>();
    userManagementResponse.put("userMgmt.employee.edit.review.submit", "Save changes");
    response.put("userManagementEndpoint", userManagementResponse);

    Map<String, String> profileManagementResponse = new HashMap<>();
    profileManagementResponse.put("profile.title", "Your Profile");
    response.put("profileManagementEndpoint", profileManagementResponse);

    Map<String, String> companyManagementResponse = new HashMap<>();
    companyManagementResponse.put("coMngt.title", "Company details");
    response.put("companyManagementEndpoint", companyManagementResponse);

    Map<String, String> payApplicationResponse = new HashMap<>();
    payApplicationResponse.put("apply.title", "Apply for InnBusiness Pay");
    response.put("payApplicationEndpoint", payApplicationResponse );

    Map<String, String> authResponse = new HashMap<>();
    authResponse.put("auth.title", "Authentication");
    response.put("authEndpoint", authResponse);

    Map<String, String> notificationsResponse = new HashMap<>();
    notificationsResponse.put("notifications.title", "Notifications");
    response.put("notificationsEndpoint", notificationsResponse);

    Map<String, String> innbContactUsResponse = new HashMap<>();
    innbContactUsResponse.put("contactUs.title", "Contact us");
    response.put("innbContactUsEndpoint", innbContactUsResponse);

    return response;
  }
}
