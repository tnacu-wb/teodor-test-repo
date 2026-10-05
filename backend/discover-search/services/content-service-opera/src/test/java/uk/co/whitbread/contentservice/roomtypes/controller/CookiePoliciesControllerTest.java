package uk.co.whitbread.contentservice.roomtypes.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.exception.NoCookiePoliciesDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.*;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.CookieDurationConfig;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.IntroView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.ManageView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.CookieGroup;
import uk.co.whitbread.contentservice.roomtypes.service.CookiePoliciesService;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CookiePoliciesControllerTest {

    @Mock
    private CookiePoliciesService cookiePoliciesService;

    @InjectMocks
    private CookiePoliciesController underTestController;

    @Test
    public void getCookiePoliciesSuccessResponse() {

        CookiePolicies mockCookiePolicies = CookiePolicies.builder()
                .version("55")
                .brand(BrandCode.pi.name())
                .introView(IntroView.builder()
                        .title("Cookie Intro Title")
                        .description("<p>Intro description</p>")
                        .manageButtonText("Manage Cookies")
                        .acceptAllButtonText("Accept all Cookies")
                        .necessaryOnlyButtonText("Necessary Only")
                        .build())
                .config(CookieDurationConfig.builder().cookieOptInExpiryDays(365).cookieOptOutExpiryDays(365).build())
                .manageView(ManageView.builder()
                        .title("Manage Title")
                        .description("<p>Manage description</p>")
                        .saveSettingsButtonText("Manage Saving Button")
                        .alwaysActiveText("Manage View Active Test")
                        .cookieGroup(Collections.singletonList(CookieGroup.builder()
                                .cookieName("dtm.adobe.functional")
                                .title("This is a new title for functional cookie")
                                .description("This is a new description for functional cookie")
                                .isAlwaysActive(true)
                                .toggleLabel("Toggle Label for functional").build()))
                        .build()).build();

        CookiePoliciesResponse cookiePoliciesResponse = new CookiePoliciesResponse();
        cookiePoliciesResponse.setCookiePolicies(mockCookiePolicies);
        when(cookiePoliciesService.getCookiePolicies(LanguageCode.en.name(), BrandCode.pi.name(), SubBrandCode.none.name()))
                .thenReturn(cookiePoliciesResponse);

        final CookiePoliciesResponse underTest = underTestController.getCookiePolicies(LanguageCode.en, BrandCode.pi, SubBrandCode.none);

        assertThat(underTest.getCookiePolicies().getVersion()).isEqualTo("55");
        assertThat(underTest.getCookiePolicies().getBrand()).isEqualTo(BrandCode.pi.name());

        assertThat(underTest.getCookiePolicies().getIntroView().getTitle()).isEqualTo("Cookie Intro Title");
        assertThat(underTest.getCookiePolicies().getIntroView().getDescription()).isEqualTo("<p>Intro description</p>");
        assertThat(underTest.getCookiePolicies().getIntroView().getManageButtonText()).isEqualTo("Manage Cookies");
        assertThat(underTest.getCookiePolicies().getIntroView().getAcceptAllButtonText()).isEqualTo("Accept all Cookies");

        assertThat(underTest.getCookiePolicies().getManageView().getTitle()).isEqualTo("Manage Title");
        assertThat(underTest.getCookiePolicies().getManageView().getDescription()).isEqualTo("<p>Manage description</p>");
        assertThat(underTest.getCookiePolicies().getManageView().getSaveSettingsButtonText()).isEqualTo("Manage Saving Button");
        assertThat(underTest.getCookiePolicies().getManageView().getAlwaysActiveText()).isEqualTo("Manage View Active Test");

        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getCookieName()).isEqualTo("dtm.adobe.functional");
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getTitle()).isEqualTo("This is a new title for functional cookie");
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getDescription()).isEqualTo("This is a new description for functional cookie");
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getIsAlwaysActive()).isTrue();
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getToggleLabel()).isEqualTo("Toggle Label for functional");

        assertThat(underTest.getCookiePolicies().getConfig().getCookieOptInExpiryDays()).isEqualTo(365);
        assertThat(underTest.getCookiePolicies().getConfig().getCookieOptOutExpiryDays()).isEqualTo(365);
    }

    @Test
    public void getCookiePoliciesSuccessResponse_subBrand() {

        CookiePolicies mockCookiePolicies = CookiePolicies.builder()
                .version("1")
                .brand(BrandCode.pi.name())
                .introView(IntroView.builder()
                        .title("BF Cookie Intro Title")
                        .description("<p>BF Intro description</p>")
                        .manageButtonText("BF Manage Cookies")
                        .acceptAllButtonText("BF Accept all Cookies")
                        .necessaryOnlyButtonText("BF Necessary Only")
                        .build())
                .config(CookieDurationConfig.builder().cookieOptInExpiryDays(365).cookieOptOutExpiryDays(365).build())
                .manageView(ManageView.builder()
                        .title("BF Manage Title")
                        .description("<p>BF Manage description</p>")
                        .saveSettingsButtonText("BF Manage Saving Button")
                        .alwaysActiveText("BF Manage View Active Test")
                        .cookieGroup(Collections.singletonList(CookieGroup.builder()
                                .cookieName("bf.dtm.adobe.functional")
                                .title("BF This is a new title for functional cookie")
                                .description("BF This is a new description for functional cookie")
                                .isAlwaysActive(true)
                                .toggleLabel("BF Toggle Label for functional").build()))
                        .build()).build();

        CookiePoliciesResponse cookiePoliciesResponse = new CookiePoliciesResponse();
        cookiePoliciesResponse.setCookiePolicies(mockCookiePolicies);
        when(cookiePoliciesService.getCookiePolicies(LanguageCode.en.name(), BrandCode.restaurant.name(), SubBrandCode.beefeater.name()))
                .thenReturn(cookiePoliciesResponse);

        final CookiePoliciesResponse underTest = underTestController.getCookiePolicies(LanguageCode.en, BrandCode.restaurant, SubBrandCode.beefeater);

        assertThat(underTest.getCookiePolicies().getVersion()).isEqualTo("1");
        assertThat(underTest.getCookiePolicies().getBrand()).isEqualTo(BrandCode.pi.name());

        assertThat(underTest.getCookiePolicies().getIntroView().getTitle()).isEqualTo("BF Cookie Intro Title");
        assertThat(underTest.getCookiePolicies().getIntroView().getDescription()).isEqualTo("<p>BF Intro description</p>");
        assertThat(underTest.getCookiePolicies().getIntroView().getManageButtonText()).isEqualTo("BF Manage Cookies");
        assertThat(underTest.getCookiePolicies().getIntroView().getAcceptAllButtonText()).isEqualTo("BF Accept all Cookies");

        assertThat(underTest.getCookiePolicies().getManageView().getTitle()).isEqualTo("BF Manage Title");
        assertThat(underTest.getCookiePolicies().getManageView().getDescription()).isEqualTo("<p>BF Manage description</p>");
        assertThat(underTest.getCookiePolicies().getManageView().getSaveSettingsButtonText()).isEqualTo("BF Manage Saving Button");
        assertThat(underTest.getCookiePolicies().getManageView().getAlwaysActiveText()).isEqualTo("BF Manage View Active Test");

        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getCookieName()).isEqualTo("bf.dtm.adobe.functional");
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getTitle()).isEqualTo("BF This is a new title for functional cookie");
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getDescription()).isEqualTo("BF This is a new description for functional cookie");
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getIsAlwaysActive()).isTrue();
        assertThat(underTest.getCookiePolicies().getManageView().getCookieGroup().get(0).getToggleLabel()).isEqualTo("BF Toggle Label for functional");

        assertThat(underTest.getCookiePolicies().getConfig().getCookieOptInExpiryDays()).isEqualTo(365);
        assertThat(underTest.getCookiePolicies().getConfig().getCookieOptOutExpiryDays()).isEqualTo(365);
    }

    @Test
    public void noCookiePoliciesExist() {
        when(cookiePoliciesService.getCookiePolicies(LanguageCode.en.name(), BrandCode.pi.name(), SubBrandCode.none.name()))
                .thenThrow(NoCookiePoliciesDataFoundException.class);
        assertThrows(NoCookiePoliciesDataFoundException.class, () -> underTestController.getCookiePolicies(LanguageCode.en, BrandCode.pi, SubBrandCode.none));
    }
}
