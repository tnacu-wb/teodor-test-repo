package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 * "EMerchantID" : "WhitbreadTestBerlinHotel",
 * "ValidationCode": "WhitbreadTestBerlinHotel1"
 * }
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartReconciliationRequest {

    @JsonProperty(value = "EMerchantID")
    private String validationId;
    @JsonProperty(value = "ValidationCode")
    private String validationCode;
}
