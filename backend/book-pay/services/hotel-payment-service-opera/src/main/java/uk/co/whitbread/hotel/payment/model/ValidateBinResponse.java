package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateBinResponse {
    @Schema(example = "VI")
    private String cardType;
    @Schema(example = "Visa Credit")
    private String cardLegend;
    private Boolean startDateRequired;
    private Boolean issueNumberRequired;
    private Boolean cardFeeApplies;
    private Price cardFeeAmount;

}
