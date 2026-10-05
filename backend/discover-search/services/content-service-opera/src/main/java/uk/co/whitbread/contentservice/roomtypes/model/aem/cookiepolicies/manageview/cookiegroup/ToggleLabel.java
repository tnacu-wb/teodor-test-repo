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
public class ToggleLabel implements Serializable {
    private static final long serialVersionUID = -4749650538329898378L;
    private String value;
}