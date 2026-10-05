package uk.co.whitbread.marketing.model.permissionmanagement;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String title;
    private String firstName;
    private String lastName;
    private String nationality;
    private String userId;
    @NotNull
    private String countryOfResidence;
    private String customerId;
    @NotNull
    @Schema(example = "en", requiredMode = Schema.RequiredMode.REQUIRED, description = "ISO code for language which the customer would like to receive marketing.")
    private String language;
}
