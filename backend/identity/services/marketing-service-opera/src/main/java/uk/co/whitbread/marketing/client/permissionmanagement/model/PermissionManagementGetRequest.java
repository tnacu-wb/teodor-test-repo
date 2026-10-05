package uk.co.whitbread.marketing.client.permissionmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermissionManagementGetRequest {

    @NotBlank
    @JsonProperty(value = "SourceSystem")
    private String sourceSystem;

    @NotEmpty
    @JsonProperty(value = "ContactChannel")
    private ContactChannel contactChannel;

    @NotBlank
    private String[] brandCodes;

}