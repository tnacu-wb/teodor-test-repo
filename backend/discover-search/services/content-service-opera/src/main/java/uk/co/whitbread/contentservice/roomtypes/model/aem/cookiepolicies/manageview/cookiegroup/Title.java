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
public class Title implements Serializable {
    private static final long serialVersionUID = -242270888172163029L;
    private String value;
}