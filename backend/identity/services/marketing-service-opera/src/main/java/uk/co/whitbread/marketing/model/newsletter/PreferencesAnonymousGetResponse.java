package uk.co.whitbread.marketing.model.newsletter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreferencesAnonymousGetResponse {

    private boolean optIn;
    private boolean secondPartyOptIn;
    private boolean thirdPartyVendorsOptIn;
    private boolean secondOptInReq;
    private boolean secondOptIn;
    private boolean suppressMarketingCheckbox;
}
