package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import uk.co.whitbread.payments.validation.ValueOfEnum;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Refund {

    @NotEmpty
    @Valid
    @ValueOfEnum(enumClass = PaymentType.class)
    @Schema(required = true, example = "CARD", description = "Type of refund to process.", implementation = PaymentType.class)
    private String type;

    @NotNull
    @Schema(description = "Customers card for refund of the payment.")
    @Valid
    private Card card;

    @NotNull
    @Valid
    @Schema(required = true, description = "Information on amount to process refund for.")
    private Amount amount;

    @NotNull
    @Schema(description = "Reason for refund.")
    @Valid
    private String reason;
}
