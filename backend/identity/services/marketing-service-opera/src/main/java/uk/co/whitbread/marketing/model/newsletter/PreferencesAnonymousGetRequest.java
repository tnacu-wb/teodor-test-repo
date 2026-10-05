package uk.co.whitbread.marketing.model.newsletter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreferencesAnonymousGetRequest {

    private String emailAddress;
    @NotNull
    private String brandCode;
    private String countryOfResidence;
    private String language;
}
