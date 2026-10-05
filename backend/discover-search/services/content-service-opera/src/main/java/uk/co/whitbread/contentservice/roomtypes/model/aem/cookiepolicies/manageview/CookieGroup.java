package uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookieGroup implements Serializable {
    private static final long serialVersionUID = 8072417874211520221L;
    private String cookieName;
    private String title;
    private String description;
    private Boolean isAlwaysActive;
    private String toggleLabel;
}