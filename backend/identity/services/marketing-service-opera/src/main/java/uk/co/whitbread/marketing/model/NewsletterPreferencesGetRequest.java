package uk.co.whitbread.marketing.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewsletterPreferencesGetRequest {
    @NotBlank
    private String requestId;

    private ContactChannel contactChannel;

    @Size(min = 1)
    private String[] brandCodes;

}
