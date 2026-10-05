package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import uk.co.whitbread.payments.validation.ValueOfEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Amount {

    @NotEmpty
    @ValueOfEnum(enumClass = Currency.class)
    @Schema(description = "Currency of payment.",
            implementation = Currency.class,
            example = "GBP",
            required = true)
    private String currency;

    @NotNull
    @Schema(description = "Minor units of payment..e.g 1 == £0.01 or €0.01",
            example = "1", required = true)
    // For no card read transactions minor units can be set to 0
    @Min(value = 0, message = "Transaction must be for 0 minor unit or more.")
    private int minorUnits;
}
