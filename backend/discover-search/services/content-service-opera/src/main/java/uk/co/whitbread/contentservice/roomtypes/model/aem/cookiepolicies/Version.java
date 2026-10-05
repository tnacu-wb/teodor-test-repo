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
public class Version implements Serializable {
    private static final long serialVersionUID = -1415540192942215135L;
    private String value;
}