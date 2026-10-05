package uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.CookieGroup;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManageView implements Serializable {
    private static final long serialVersionUID = 3407075767081063089L;
    private String title;
    private String description;
    private String saveSettingsButtonText;
    private String alwaysActiveText;
    private List<CookieGroup> cookieGroup;
}