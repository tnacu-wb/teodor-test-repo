package uk.co.whitbread.contentservice.roomtypes.model.aem.cookiepolicies.manageview.cookiegroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IsAlwaysActive implements Serializable {
    private static final long serialVersionUID = 5084163327563206879L;
    private String value;
}