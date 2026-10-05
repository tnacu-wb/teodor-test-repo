package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.CookieDurationConfig;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.IntroView;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.ManageView;

import java.io.Serializable;

@Builder
@Data
public class CookiePolicies implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private String version;
    private String brand;
    private CookieDurationConfig config;
    private IntroView introView;
    private ManageView manageView;
}
