package uk.co.whitbread.marketing.model.permissionmanagement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.marketing.model.newsletter.ContactSubType;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePreferencesRequest {

    private boolean optIn;
    private boolean doubleOptIn;
    private boolean secondPartyOptIn;
    private boolean thirdPartyVendorsOptIn;
    @NotNull
    private String[] brandCodes;
    @NotNull
    @Valid
    private Customer customer;
    private ContactSubType contactSubType;
    @Valid
    private SourceDetails sourceDetails;
}
