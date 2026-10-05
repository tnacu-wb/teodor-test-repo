package uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookieDurationConfig implements Serializable {
    private static final long serialVersionUID = 8072417874111520221L;
    private int cookieOptInExpiryDays;
    private int cookieOptOutExpiryDays;
}