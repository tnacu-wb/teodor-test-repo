package uk.co.whitbread.content.infrastructure.rest.client.cookies;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.AEM_COOKIE_POLICIES_EXCEPTION;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.*;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.adapter.CookiePoliciesAemClient;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper.CookiePoliciesMapper;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.mapper.CookiePoliciesRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.in.*;
import uk.co.whitbread.content.infrastructure.rest.client.cookies.model.out.CookiePoliciesRequestAemDto;

@ExtendWith(MockitoExtension.class)
class CookiePoliciesOutPortImplTests {

  @InjectMocks
  private CookiePoliciesOutPortImpl cookiePoliciesOutPort;

  @Mock
  private CookiePoliciesAemClient cookiePoliciesAemClient;

  @Mock
  private CookiePoliciesRequestMapper cookiePoliciesRequestMapper;

  @Mock
  private CookiePoliciesMapper cookiePoliciesMapper;

  @Test
  void getCookiePolicies__ShouldReturnOk() {
    //Arrange
    var cookiePoliciesRequest = createCookiePoliciesRequest();
    when(cookiePoliciesRequestMapper.toDto(any(CookiePoliciesRequest.class))).thenReturn(
        mockCookiePoliciesRequestAemDto());
    when(cookiePoliciesAemClient.getCookiePolicies(any(CookiePoliciesRequestAemDto.class))).thenReturn(
        mockCookiePoliciesDto());
    when(cookiePoliciesMapper.toDomainModel(any(CookiePoliciesInformationDto.class))).thenReturn(mockCookiePolicies());

    //Act
    var cookiePolicies = cookiePoliciesOutPort.getCookiePolicies(cookiePoliciesRequest);

    //Assert
    assertThat(cookiePolicies, notNullValue());
    assertThat(cookiePolicies.getCookiePolicies().getBrand(), is("pi"));
    assertThat(cookiePolicies.getCookiePolicies().getIntroView().getTitle(),
        is("Cookies and how we use them"));
    assertThat(cookiePolicies.getCookiePolicies().getIntroView().getDescription(),
        is("Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you."));
    assertThat(cookiePolicies.getCookiePolicies().getIntroView().getManageButtonText(),
        is("Manage cookies"));
    assertThat(cookiePolicies.getCookiePolicies().getIntroView().getAcceptAllButtonText(),
        is("Accept all cookies"));
    assertThat(cookiePolicies.getCookiePolicies().getIntroView().getNecessaryOnlyButtonText(),
        is("Necessary Only"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getTitle(), is("Manage cookies"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getDescription(),
        is("<p>Choose the cookies that work for you.</p>"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getSaveSettingsButtonText(),
        is("Confirm settings"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getAlwaysActiveText(),
        is("Always active"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getCookieGroup().get(0).getCookieName(),
        is("permissionEssential"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getCookieGroup().get(0).getTitle(),
        is("Essential"));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getCookieGroup().get(0).getDescription(),
        is("<p>Some cookies are essential – our website wouldn’t work without them!</p>"));
    assertThat(
        cookiePolicies.getCookiePolicies().getManageView().getCookieGroup().get(0).getIsAlwaysActive(),
        is(true));
    assertThat(cookiePolicies.getCookiePolicies().getManageView().getCookieGroup().get(0).getToggleLabel(),
        is("Essentials are always active."));
    assertThat(cookiePolicies.getCookiePolicies().getConfig().getCookieOptInExpiryDays(), is("365"));
    assertThat(cookiePolicies.getCookiePolicies().getConfig().getCookieOptOutExpiryDays(), is("365"));
  }

  @Test
  void getCookiePolicies__ShouldReturnException() {
    //Arrange
    String expectedMessage = "Unable to get cookie policies.";
    var cookiePoliciesRequest = createCookiePoliciesRequest();
    when(cookiePoliciesRequestMapper.toDto(any(CookiePoliciesRequest.class))).thenReturn(
        mockCookiePoliciesRequestAemDto());
    when(cookiePoliciesAemClient.getCookiePolicies(
        any(CookiePoliciesRequestAemDto.class))).thenThrow(
        new AemResponseException(AEM_COOKIE_POLICIES_EXCEPTION, expectedMessage, new Exception()));

    //Act
    var actual = assertThrows(AemResponseException.class,
        () -> cookiePoliciesOutPort.getCookiePolicies(cookiePoliciesRequest));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getMessage(), is(expectedMessage));

  }

  private CookiePoliciesRequest createCookiePoliciesRequest() {
    return CookiePoliciesRequest.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

  private CookiePoliciesInformation mockCookiePolicies() {
    var cookieGroup = CookieGroup.builder()
        .cookieName("permissionEssential")
        .title("Essential")
        .description("<p>Some cookies are essential – our website wouldn’t work without them!</p>")
        .isAlwaysActive(true)
        .toggleLabel("Essentials are always active.")
        .build();

    var manageView = ManageView.builder()
        .title("Manage cookies")
        .description("<p>Choose the cookies that work for you.</p>")
        .saveSettingsButtonText("Confirm settings")
        .alwaysActiveText("Always active")
        .cookieGroup(List.of(cookieGroup))
        .build();

    var introView = IntroView.builder()
        .title("Cookies and how we use them")
        .description(
            "Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you.")
        .manageButtonText("Manage cookies")
        .acceptAllButtonText("Accept all cookies")
        .necessaryOnlyButtonText("Necessary Only")
        .build();

    var config = CookieDurationConfig.builder()
        .cookieOptInExpiryDays("365")
        .cookieOptOutExpiryDays("365")
        .build();

    var cookiePoliciesInformation = CookiePolicies.builder()
        .brand("pi")
        .introView(introView)
        .manageView(manageView)
        .config(config)
        .build();

    return CookiePoliciesInformation.builder()
        .cookiePolicies(cookiePoliciesInformation)
        .build();
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
        .introView(introViewDto)
        .manageView(manageViewDto)
        .config(configDto)
        .build();

    return CookiePoliciesInformationDto.builder()
        .cookiePolicies(cookiePoliciesInformationDto)
        .build();
  }

  private CookiePoliciesRequestAemDto mockCookiePoliciesRequestAemDto() {
    return CookiePoliciesRequestAemDto.builder()
        .country("gb")
        .language("en")
        .brand("pi")
        .build();
  }

}
