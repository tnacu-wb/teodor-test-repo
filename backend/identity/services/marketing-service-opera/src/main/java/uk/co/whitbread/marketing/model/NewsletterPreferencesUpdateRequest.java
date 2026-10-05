package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class NewsletterPreferencesUpdateRequest {

    @NotBlank
    private String correlationId;
    @NotEmpty
    private List<Subscription> subscriptions;
}
