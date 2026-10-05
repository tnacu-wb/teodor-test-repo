package uk.co.whitbread.marketing.model.newsletter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreferencesEditRequest {

    private String userId;
    private boolean optIn;
    private boolean secondPartyOptIn;
    private boolean thirdPartyVendorsOnpIn;

    private ContactSubType contactSubType;

    @NotNull
    private String[] brandCodes;

}
