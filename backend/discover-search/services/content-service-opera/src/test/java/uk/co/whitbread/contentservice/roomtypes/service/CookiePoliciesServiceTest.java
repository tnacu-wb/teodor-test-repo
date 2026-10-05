package uk.co.whitbread.contentservice.roomtypes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.exception.NoCookiePoliciesDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.CookiePoliciesResponse;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.SubBrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.CookieDurationConfig;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.IntroView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.ManageView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.Version;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.CookieGroup;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMCookiePoliciesService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

@ExtendWith(MockitoExtension.class)
public class CookiePoliciesServiceTest {

    @Mock
    private AEMCookiePoliciesService aemCookiePoliciesService;

    @Mock
    private AEMResponseConverter aemResponseConverter;

    @InjectMocks
    private CookiePoliciesService underTestService;

    @Test
    public void getCookiePolicies() {
        AEMCookiePolicies aemCookiePoliciesItem = AEMCookiePolicies.builder().version(new Version("1")).build();
        CookiePolicies mockCookiePolicies = CookiePolicies.builder()
                .version("1")
                .brand(BrandCode.pi.name())
                .config(CookieDurationConfig.builder().cookieOptInExpiryDays(365).cookieOptOutExpiryDays(365).build())
                .introView(IntroView.builder()
                        .title("BF Cookie Intro Title")
                        .description("<p>BF Intro description</p>")
                        .manageButtonText("BF Manage Cookies")
                        .acceptAllButtonText("BF Accept all Cookies")
                        .necessaryOnlyButtonText("Necessary Only")
                        .build())
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
        when(aemCookiePoliciesService.getCookiePolicies(CountryCode.gb.name(), BrandCode.pi.name(), SubBrandCode.none.name()))
                .thenReturn(Collections.singletonList(aemCookiePoliciesItem));

        when(aemResponseConverter.convertAEMCookiePolicies(Collections.singletonList(aemCookiePoliciesItem)))
                .thenReturn(mockCookiePolicies);

        final CookiePoliciesResponse underTest = underTestService.getCookiePolicies(CountryCode.gb.name(), BrandCode.pi.name(), SubBrandCode.none.name());

        assertThat(underTest.getCookiePolicies().getVersion()).isEqualTo("1");
        assertThat(underTest.getCookiePolicies().getBrand()).isEqualTo(BrandCode.pi.name());

        assertThat(underTest.getCookiePolicies().getIntroView().getTitle()).isEqualTo("BF Cookie Intro Title");
        assertThat(underTest.getCookiePolicies().getIntroView().getDescription()).isEqualTo("<p>BF Intro description</p>");
        assertThat(underTest.getCookiePolicies().getIntroView().getManageButtonText()).isEqualTo("BF Manage Cookies");
        assertThat(underTest.getCookiePolicies().getIntroView().getAcceptAllButtonText()).isEqualTo("BF Accept all Cookies");
        assertThat(underTest.getCookiePolicies().getIntroView().getNecessaryOnlyButtonText()).isEqualTo("Necessary Only");

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
    public void getNonCookiePoliciesFailure() {
        when(aemCookiePoliciesService.getCookiePolicies(CountryCode.gb.name(), BrandCode.pi.name(), SubBrandCode.none.name()))
                .thenThrow(NoCookiePoliciesDataFoundException.class);
        assertThrows(NoCookiePoliciesDataFoundException.class,
            () -> underTestService.getCookiePolicies(CountryCode.gb.name(), BrandCode.pi.name(), SubBrandCode.none.name()));
    }
}