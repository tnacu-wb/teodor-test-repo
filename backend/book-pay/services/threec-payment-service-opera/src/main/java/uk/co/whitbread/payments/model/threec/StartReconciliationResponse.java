package uk.co.whitbread.payments.model.threec;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {
 *  "ReturnCode": 0,
 *  "ReturnText": "Success"
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartReconciliationResponse {

    @JsonProperty(value = "ReturnCode")
    private int returnCode;
    @JsonProperty(value = "ReturnText")
    private String returnText;
}
