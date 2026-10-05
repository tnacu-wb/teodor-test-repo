package uk.co.whitbread.marketing.client.customerhub.model;

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
public class SubscriptionData {

    @NotBlank
    private String contactChannelType;
    @NotBlank
    private String contactChannelValue;
    @NotNull
    private Boolean contactChannelPermission;
    @NotBlank
    private String brandCode;
}
