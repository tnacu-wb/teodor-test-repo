package uk.co.whitbread.marketing.client.permissionmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PermissionManagementUpdateRequest {

    private String userId;

    @NotBlank
    private String sourceSystem;

    @NotBlank
    private String sourceLanguage;

    @NotBlank
    private String transactionId;

    @NotBlank
    private String transactionType;

    @NotBlank
    private String transactionSubType;

    private String[] brandCode;

    private String title;

    private String firstName;

    private String lastName;

    private String countryOfResidence;

    private String nationality;

    @JsonProperty(value = "EmailAddress")
    private String email;

    @JsonProperty(value = "EmailContactChannelId")
    private String emailContactChannelId;

    private boolean optIn;

    @JsonProperty(value = "2ndOptInReq")
    private boolean secondOptInReq;

    @JsonProperty(value = "2ndPartyContent")
    private boolean secondPartyContent;

    @JsonProperty(value = "3rdPartyContent")
    private boolean thiryPartyContent;
}