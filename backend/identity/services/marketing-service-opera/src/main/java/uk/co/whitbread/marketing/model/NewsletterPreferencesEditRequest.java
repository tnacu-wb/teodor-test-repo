package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsletterPreferencesEditRequest {

    private String customerId;
    private String userId;
    @NotEmpty
    private List<EditSubscription> subscriptionData;

}
