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
public class ManageViewDescription implements Serializable {
    private static final long serialVersionUID = 6452759019762730749L;
    private String value;
}