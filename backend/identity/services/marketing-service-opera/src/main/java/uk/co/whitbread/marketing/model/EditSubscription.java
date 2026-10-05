package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditSubscription {

    @NotBlank
    private ContactType contactChannelType;

    private String contactChannelValue;

    @NotNull
    private Boolean contactChannelPermission;

    @NotBlank
    private String[] brandCodes;

    private String contactChannelId;

    private String contactChannelSubType;

    private ContentPermission contentPermission;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ContentPermission {

        private boolean secondParty;
        private boolean thirdParty;

    }
}
