package uk.co.whitbread.marketing.client.permissionmanagement.model;

import lombok.Data;
import uk.co.whitbread.marketing.model.BrandPermission;
import uk.co.whitbread.marketing.model.CustomerCountryOfResidence;
import uk.co.whitbread.marketing.model.LoyaltyAccount;

import java.util.List;

@Data
public class PermissionManagementGetResponse {

    private String contactChannelId;
    private String contactChannelValue;
    private boolean IsValid;
    private boolean shared;
    private String[] sharedBy;
    private List<BrandPermission> brandPermissions;
    private List<LoyaltyAccount> loyaltyAccounts;
    private List<CustomerCountryOfResidence> customerLinks;
    private String modified;
    private boolean deleted;
}
