package uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_PAGE_DATA_EXCEPTION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.properties.AemProperties;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.aem.PageDataAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.DictionaryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.client.inn.business.pagedata.model.PageDataRequestDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PageDataAemClientTests {

  private PageDataAemClient pageDataAemClient;
  @Mock
  private WebClient webClient;
  @Mock
  private AemProperties aemProperties;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @BeforeEach
  public void init() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
          .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    this.pageDataAemClient = new PageDataAemClient(webClient, aemProperties);
  }

  @Test
  void getPageData_badInput_ShouldReturnException() {
    //given
    PageDataRequestDto pageDataAEMRequest = createPageDataRequest();
    when(aemProperties.getInnbCardManagementEndpoint())
          .thenReturn("etc/designs/global/dictionaries/innbusiness/card-management/i18n.jsondict.{language}");
    when(responseSpec.onStatus(any(), any())).thenThrow(
        new AemResponseException(
            AEM_PAGE_DATA_EXCEPTION,
            "Unable to get data",
          new Exception()));
    //when
    var actual =
        assertThrows(AemResponseException.class,
            () -> pageDataAemClient.getPageData(pageDataAEMRequest));

    //then
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_PAGE_DATA_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("Unable to get data"));
    assertThat(actual.getErrorCode(), is(AEM_PAGE_DATA_EXCEPTION.getCode()));
  }

  @Test
  void getMultipleLabels__ShouldReturnOK() {
    //given
    PageDataRequestDto pageDataRequestDto = PageDataRequestDto.builder()
          .country("gb")
          .language("en")
          .dictionaries(List.of(
                DictionaryEnumDto.CARD_MANAGEMENT_DICTIONARY,
                DictionaryEnumDto.LAYOUT_DICTIONARY,
                DictionaryEnumDto.COMMON_ICONS_DICTIONARY,
                DictionaryEnumDto.USER_MANAGEMENT_DICTIONARY,
                DictionaryEnumDto.PROFILE_MANAGEMENT_DICTIONARY,
                DictionaryEnumDto.COMPANY_MANAGEMENT_DICTIONARY,
                DictionaryEnumDto.HOMEPAGE_DICTIONARY,
                DictionaryEnumDto.SPENDING_REPORTING_DICTIONARY,
                DictionaryEnumDto.PAY_APPLICATION_DICTIONARY,
                DictionaryEnumDto.AUTH_DICTIONARY,
                DictionaryEnumDto.NOTIFICATIONS_DICTIONARY
          )).build();
    when(aemProperties.getInnbCommonIconsEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/common-icons/i18n.jsondict.{language}");
    when(aemProperties.getInnbCommonLayoutEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/innbusiness/common-layout/i18n.jsondict.{language}");
    when(aemProperties.getInnbCardManagementEndpoint()).thenReturn(
        "etc/designs/global/dictionaries/innbusiness/card-management/i18n.jsondict.{language}");
    when(aemProperties.getInnbUserManagementEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/user-management/i18n.jsondict.{language}");
    when(aemProperties.getInnbProfileManagementEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/profile-management/i18n.jsondict.{language}");
    when(aemProperties.getInnbCompanyManagementEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/company-management/i18n.jsondict.{language}");
    when(aemProperties.getHomePageEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/home/i18n.jsondict.{language}");
    when(aemProperties.getSpendingReportingEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/home/i18n.jsondict.{language}");
    when(aemProperties.getPayApplicationEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/pay-application/i18n.jsondict.{language}");
    when(aemProperties.getAuthEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/auth/i18n.jsondict.{language}");
    when(aemProperties.getNotificationsEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/notifications/i18n.jsondict.{language}");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
    }))
        .thenReturn(mockPageDataLayoutResponse())
        .thenReturn(mockPageCommonIconsResponse())
        .thenReturn(mockPageDataCardManagementResponse())
        .thenReturn(mockPageUserManagementResponse())
        .thenReturn(mockPageUserProfileResponse())
        .thenReturn(mockPageDataCompanyManagementResponse())
        .thenReturn(mockPageDataHomepageResponse())
        .thenReturn(mockPageDataSpendingReportingResponse())
        .thenReturn(mockPaymentApplicationResponse())
        .thenReturn(mockPageDataAuthResponse())
        .thenReturn(mockPageDataNotificationsResponse());

    //when
    final var pageDataAemAEMResponse = pageDataAemClient.getPageData(pageDataRequestDto);

    //then
    assertThat(pageDataAemAEMResponse, notNullValue());
    assertThat(pageDataAemAEMResponse, aMapWithSize(11));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("cardManagementEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("layoutEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("commonIconsEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("userManagementEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("profileManagementEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("companyManagementEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("homepageEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("spendingReportingEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("payApplicationEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("authEndpoint"));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("notificationsEndpoint"));
    verify(aemProperties, times(1)).getInnbCardManagementEndpoint();
    verify(aemProperties, times(1)).getInnbCommonLayoutEndpoint();
    verify(aemProperties, times(1)).getInnbCommonIconsEndpoint();
    verify(aemProperties, times(1)).getInnbUserManagementEndpoint();
    verify(aemProperties, times(1)).getInnbProfileManagementEndpoint();
    verify(aemProperties, times(1)).getInnbCompanyManagementEndpoint();
    verify(aemProperties, times(1)).getHomePageEndpoint();
    verify(aemProperties, times(1)).getSpendingReportingEndpoint();
    verify(aemProperties, times(1)).getPayApplicationEndpoint();
    verify(aemProperties, times(1)).getAuthEndpoint();
    verify(aemProperties, times(1)).getNotificationsEndpoint();
  }


  @Test
  void getContactUsLabel__ShouldReturnOK() {
    //given
    PageDataRequestDto pageDataRequestDto = PageDataRequestDto.builder()
          .country("gb")
          .language("en")
          .dictionaries(List.of(
                DictionaryEnumDto.CONTACT_US_DICTIONARY
          )).build();

    when(aemProperties.getInnbContactUsEndpoint()).thenReturn(
        "/etc/designs/global/dictionaries/innbusiness/contact-us/i18n.jsondict.{language}");
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
    }))
        .thenReturn(mockContactUsResponse());

    //when
    final var pageDataAemAEMResponse = pageDataAemClient.getPageData(pageDataRequestDto);

    //then
    assertThat(pageDataAemAEMResponse, notNullValue());
    assertThat(pageDataAemAEMResponse, aMapWithSize(1));
    assertThat(pageDataAemAEMResponse, IsMapContaining.hasKey("innbContactUsEndpoint"));
    verify(aemProperties, times(1)).getInnbContactUsEndpoint();
  }

  private Mono<Map<String, String>> mockPaymentApplicationResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("apply.title", "Apply for InnBusiness Pay");
    data.put("apply.companyDetails.description", "Company type, time trading and other details relevant to the credit check.");
    return Mono.just(data);
  }

  private PageDataRequestDto createPageDataRequest() {
    return PageDataRequestDto.builder()
        .country("gb")
        .language("en")
        .dictionaries(List.of(DictionaryEnumDto.CARD_MANAGEMENT_DICTIONARY))
        .build();
  }

  private Mono<Map<String, String>> mockPageDataLayoutResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("innbusinessLayout.manageAccount.cards.title", "Cards");
    data.put("innbusinessLayout.manageAccount.profile.text",
        "Enhance your stay with us! Complete your profile to enjoy customised services.");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageDataCardManagementResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("cardMgmt.addCard.selectEmployee.label", "Select employee");
    data.put("cardMgmt.delivery.where.options.cardHolderAddress",
        "Send this card direct to the card holder at an alternative address");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageCommonIconsResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("icon.payment.visa", "/content/dam/global/icons/payments/visa.svg");
    data.put("icon.chevron.up",
        "/content/dam/global/icons/common/chevron-up.svg");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageUserManagementResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("userMgmt.employee.edit.review.submit", "Save changes");
    data.put("userMgmt.employee.add.form.heading", "Personal details");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageUserProfileResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("profile.title", "Your Profile");
    data.put("profile.form.button.save", "Save changes");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageDataCompanyManagementResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("coMngt.allowances.meals.title", "Meals");
    data.put("coMngt.additionalDetails.coSector.title", "Company selector");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageDataHomepageResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("home.innbusinessPay.upcomingBookings.viewAllBookings.link", "View all bookings");
    data.put("home.innbusinessPay.upcomingBookings.heading", "Upcoming bookings");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageDataSpendingReportingResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("spending.reporting.subheading", "Spent this month");
    data.put("management.info.report.choose.dates", "Choose reporting dates");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageDataAuthResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("auth.linkIbPayAccount.error.invalidAccountNumber", "Please enter a valid card number or account number.");
    data.put("auth.linkIbPayAccount.memorableWord.rule1", "Be between 10 and 64 characters long");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockPageDataNotificationsResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("notification.contact.update.action", "<a href=\\\"#\\\">Update your profile</a>");
    data.put("notification.account.contact.update.subtitle", "Please update your Account and Contact details.");
    return Mono.just(data);
  }

  private Mono<Map<String, String>> mockContactUsResponse() {
    Map<String, String> data = new HashMap<>();
    data.put("contactUs.title", "Contact us");
    data.put("contactUs.subtitle",
        "Whether you need help with InnBusiness Pay, your booking or your account, we're here to help.");
    return Mono.just(data);
  }
}
