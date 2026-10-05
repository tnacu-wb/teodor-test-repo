package uk.co.whitbread.marketing.client.customerhub.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerHubNewsletterPreferencesEditRequest {

    @NotBlank
    private String customerId;

    private String userId;

    @NotBlank
    private String sourceSystem;

    @NotNull
    private LocalDateTime permissionDateTime;

    @NotEmpty
    private List<EditSubscriptionData> subscriptionData;

}