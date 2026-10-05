package uk.co.whitbread.marketing.client.permissionmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class PermissionManagementConfirmDoubleOptIn {

    @JsonProperty(value = "CustomerId")
    private String customerId;

    @NotBlank
    @JsonProperty(value = "SourceSystem")
    private String sourceSystem;

    @NotNull
    @JsonProperty(value = "SubscriptionData")
    private SubscriptionData[] subscriptionData;
}