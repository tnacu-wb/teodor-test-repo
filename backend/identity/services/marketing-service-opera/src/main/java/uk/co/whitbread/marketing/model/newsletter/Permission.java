package uk.co.whitbread.marketing.model.newsletter;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    private String brandCode;
    private String brand;
    private boolean optIn;
    @JsonProperty("2ndOptInReq")
    private boolean secondOptInReq;
    @JsonProperty("2ndOptIn")
    private boolean secondOptIn;
    private boolean secondPartyOptIn;
    private boolean thirdPartyVendorsOptIn;
    private boolean suppressMarketingCheckbox;
}
