package uk.co.whitbread.content.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.content.domain.model.cookies.in.CookiePoliciesRequest;
import uk.co.whitbread.content.domain.model.cookies.out.*;
import uk.co.whitbread.content.domain.ports.secondary.CookiePoliciesOutPort;

@ExtendWith(MockitoExtension.class)
class CookiePoliciesInPortImplTest {

  @Mock
  private CookiePoliciesOutPort cookiePoliciesOutPort;

  @InjectMocks
  private CookiePoliciesInPortImpl cookiePoliciesInPort;

  @Test
  void getCookiePolicies__ShouldReturnOk() {
    //Arrange
    when(cookiePoliciesOutPort.getCookiePolicies(any(CookiePoliciesRequest.class))).thenReturn(mockCookiePolicies());

    //Act
    final var cookiePolicies = cookiePoliciesInPort.getCookiePolicies(createCookiePolicies());

    //Assert
    assertThat(cookiePolicies, notNullValue());
    assertThat(cookiePolicies.getCookiePolicies().getBrand(), is("pi"));
    assertThat(cookiePolicies.getCookiePolicies().getConfig().getCookieOptInExpiryDays(), is("365"));
    assertThat(cookiePolicies.getCookiePolicies().getConfig().getCookieOptOutExpiryDays(), is("365"));
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
  }

  private CookiePoliciesRequest createCookiePolicies() {
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
}
