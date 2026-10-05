package uk.co.whitbread.marketing.client.customerhub.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerHubNewsletterPreferencesGetRequest {

    @NotBlank
    private String requestId;

    @NotBlank
    private String sourceSystem;

    @NotNull
    private LocalDateTime requestedDateTime;

    @NotEmpty
    private ContactChannelData ContactChannel;

    @NotBlank
    private String[] brandCodes;

}