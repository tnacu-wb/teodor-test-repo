package uk.co.whitbread.content.infrastructure.rest.client.cookies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COOKIE_POLICIES_EXCEPTION;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.adapter.CookiePoliciesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in.*;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.out.CookiePoliciesRequestAemDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class CookiePoliciesAemClientTests {

  @InjectMocks
  private CookiePoliciesAemClient cookiePoliciesAemClient;

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Test
  void getCookiePolicies__ShouldReturnOk() {
    //Arrange
    CookiePoliciesRequestAemDto cookiePoliciesRequestAemDto = createCookiePoliciesRequestAemDto();
    initWebClient();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CookiePoliciesInformationDto.class)).thenReturn(
        Mono.just(mockCookiePoliciesDto()));

    //Act
    final var cookiePoliciesAemResponse = cookiePoliciesAemClient.getCookiePolicies(
        cookiePoliciesRequestAemDto);

    //Assert
    assertThat(cookiePoliciesAemResponse, notNullValue());
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getBrand(), is("pi"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getIntroView().getTitle(),
        is("Cookies and how we use them"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getIntroView().getDescription(),
        is("Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you."));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getIntroView().getManageButtonText(),
        is("Manage cookies"));
    assertThat(
        cookiePoliciesAemResponse.getCookiePolicies().getIntroView().getAcceptAllButtonText(),
        is("Accept all cookies"));
    assertThat(
            cookiePoliciesAemResponse.getCookiePolicies().getIntroView().getNecessaryOnlyButtonText(),
            is("Necessary Only"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getTitle(),
        is("Manage cookies"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getDescription(),
        is("<p>Choose the cookies that work for you.</p>"));
    assertThat(
        cookiePoliciesAemResponse.getCookiePolicies().getManageView().getSaveSettingsButtonText(),
        is("Confirm settings"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getAlwaysActiveText(),
        is("Always active"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getCookieGroup().get(0)
            .getCookieName(),
        is("permissionEssential"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getCookieGroup().get(0)
            .getTitle(),
        is("Essential"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getCookieGroup().get(0)
            .getDescription(),
        is("<p>Some cookies are essential – our website wouldn’t work without them!</p>"));
    assertThat(
        cookiePoliciesAemResponse.getCookiePolicies().getManageView().getCookieGroup().get(0)
            .getIsAlwaysActive(),
        is(true));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getManageView().getCookieGroup().get(0)
            .getToggleLabel(),
        is("Essentials are always active."));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getConfig().getCookieOptInExpiryDays(), is("365"));
    assertThat(cookiePoliciesAemResponse.getCookiePolicies().getConfig().getCookieOptOutExpiryDays(), is("365"));
  }

  @Test
  void getCookiePolicies_ShouldReturnException() {
    CookiePoliciesRequestAemDto cookiePoliciesRequestAemDto = createCookiePoliciesRequestAemDto();
    initWebClient();
    var exception = new AemResponseException(
        AEM_COOKIE_POLICIES_EXCEPTION,
        "message",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> cookiePoliciesAemClient.getCookiePolicies(cookiePoliciesRequestAemDto));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(AEM_COOKIE_POLICIES_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(AEM_COOKIE_POLICIES_EXCEPTION.getCode()));
  }

  private CookiePoliciesInformationDto mockCookiePoliciesDto() {
    var cookieGroupDto = CookieGroupDto.builder()
        .cookieName("permissionEssential")
        .title("Essential")
        .description("<p>Some cookies are essential – our website wouldn’t work without them!</p>")
        .isAlwaysActive(true)
        .toggleLabel("Essentials are always active.")
        .build();

    var manageViewDto = ManageViewDto.builder()
        .title("Manage cookies")
        .description("<p>Choose the cookies that work for you.</p>")
        .saveSettingsButtonText("Confirm settings")
        .alwaysActiveText("Always active")
        .cookieGroup(List.of(cookieGroupDto))
        .build();

    var introViewDto = IntroViewDto.builder()
        .title("Cookies and how we use them")
        .description(
            "Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you.")
        .manageButtonText("Manage cookies")
        .acceptAllButtonText("Accept all cookies")
        .necessaryOnlyButtonText("Necessary Only")
        .build();

    var configDto = CookieDurationConfigDto.builder()
        .cookieOptInExpiryDays("365")
        .cookieOptOutExpiryDays("365")
        .build();

    var cookiePoliciesInformationDto = CookiePoliciesDto.builder()
        .brand("pi")
        .config(configDto)
        .introView(introViewDto)
        .manageView(manageViewDto)
        .build();

    return CookiePoliciesInformationDto.builder()
        .cookiePolicies(cookiePoliciesInformationDto)
        .build();
  }

  private CookiePoliciesRequestAemDto createCookiePoliciesRequestAemDto() {
    return CookiePoliciesRequestAemDto.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

  private void initWebClient() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
  }
}
