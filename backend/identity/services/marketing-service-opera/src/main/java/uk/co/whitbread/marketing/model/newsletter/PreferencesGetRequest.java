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
public class PreferencesGetRequest {

    boolean business;
    private ContactType contactType;
    private String contactValue;
    private String contactChannelId;
    @NotNull
    private String[] brandCodes;
    private String countryOfResidence;
    private String language;

}
