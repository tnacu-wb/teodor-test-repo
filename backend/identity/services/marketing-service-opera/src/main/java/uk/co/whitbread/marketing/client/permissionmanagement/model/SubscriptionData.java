package uk.co.whitbread.marketing.client.permissionmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(value = "ContactChannelType")
    private String contactChannelType;

    @JsonProperty(value = "ContactChannelSubType")
    private String contactChannelSubType;

    @JsonProperty(value = "ContactChannelId")
    @NotNull
    private String contactChannelId;

    @JsonProperty(value = "BrandCodes")
    @NotBlank
    private String[] brandCodes;

}
