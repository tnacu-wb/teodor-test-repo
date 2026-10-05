package uk.co.whitbread.marketing.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.List;
import lombok.Data;
import uk.co.whitbread.marketing.client.customerhub.model.ContentPermissionData;


@Data
public class BrandPermission {
    private String brandCode;
    private String brand;
    private boolean optIn;
    @JsonProperty("2ndOptInReq")
    private boolean secondOptInReq;
    @JsonProperty("2ndOptIn")
    private boolean secondOptIn;
    private ContentPermissionData contentPermission = new ContentPermissionData();
    private String lastOptInDate;
    private String lastOptOutDate;
    private String lastSourceBusinessKey;
    private String lastSourceCustomerId;
    private String lastModifiedBy;
    private String[] sharedBy;
    private List<String> countriesOfResidence;

    @JsonSetter("contentPermission")
    public void setContentPermission(ContentPermissionData aContentPermission) {
        if (aContentPermission != null) {
            contentPermission = aContentPermission;
        }
    }
}
