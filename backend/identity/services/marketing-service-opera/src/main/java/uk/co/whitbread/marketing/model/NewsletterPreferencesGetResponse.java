package uk.co.whitbread.marketing.model;

import lombok.Data;

import java.util.List;

@Data
public class NewsletterPreferencesGetResponse {

    private String contactChannelId;
    private String contactChannelValue;
    private String doNotContact;
    private String doNotContactAppliedDate;
    private boolean IsValid;
    private boolean valid;
    private boolean shared;
    private String lastQuarantineDate;
    private List<BrandPermission> brandPermissions;
    private List<LoyaltyAccount> loyaltyAccounts;
    private String[] sharedBy;
    private String modified;
    private boolean deleted;
}
