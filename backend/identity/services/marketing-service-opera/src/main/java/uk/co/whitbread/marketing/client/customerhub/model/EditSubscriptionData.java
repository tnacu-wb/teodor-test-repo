package uk.co.whitbread.marketing.client.customerhub.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class EditSubscriptionData {

    @NotBlank
    private String contactChannelType;

    private String contactChannelValue;

    @NotNull
    private Boolean contactChannelPermission;

    @NotBlank
    private String[] brandCodes;

    private String contactChannelId;

    private String contactChannelSubType;

    private ContentPermissionData contentPermission;

}
