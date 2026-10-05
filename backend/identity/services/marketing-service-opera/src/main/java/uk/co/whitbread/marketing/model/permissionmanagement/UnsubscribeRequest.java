package uk.co.whitbread.marketing.model.permissionmanagement;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.marketing.model.newsletter.ContactSubType;
import uk.co.whitbread.marketing.model.newsletter.ContactType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnsubscribeRequest {

    @NotNull
    private String[] brandCodes;
    private String customerId;
    @NotNull
    private ContactType contactType;
    private ContactSubType contactSubType;
}
