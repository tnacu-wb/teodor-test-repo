package uk.co.whitbread.contentservice.roomtypes.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.*;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup.*;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class AEMCookiePolicies implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private Version version;
    private Brand brand;
    private CookieOptInExpiryDays cookieOptInExpiryDays;
    private CookieOptOutExpiryDays cookieOptOutExpiryDays;
    private IntroViewTitle introViewTitle;
    private IntroViewDescription introViewDescription;
    private IntroViewManageButtonText introViewManageButtonText;
    private IntroViewAcceptAllButtonText introViewAcceptAllButtonText;
    private IntroViewNecessaryOnlyButtonText introViewNecessaryOnlyButtonText;
    private ManageViewTitle manageViewTitle;
    private ManageViewDescription manageViewDescription;
    private ManageViewSaveSettingsButtonText manageViewSaveSettingsButtonText;
    private ManageViewAlwaysActiveText manageViewAlwaysActiveText;
    private CookieName cookieName;
    private Title title;
    private Description description;
    private IsAlwaysActive isAlwaysActive;
    private ToggleLabel toggleLabel;
}
