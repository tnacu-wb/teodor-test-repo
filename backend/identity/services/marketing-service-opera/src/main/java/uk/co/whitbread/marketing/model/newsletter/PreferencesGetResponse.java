package uk.co.whitbread.marketing.model.newsletter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.marketing.model.LoyaltyAccount;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreferencesGetResponse {

    private List<Permission> permissions;
    private List<LoyaltyAccount> loyaltyAccounts;
    private boolean valid;
    private boolean deleted;
    private String contactChannelId;
    private String contactChannelValue;
}
