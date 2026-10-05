package uk.co.whitbread.hotel.card.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class PaymentCardPIPersonal extends PaymentCardCommon {
    @NotEmpty(message = "customerAccountId must not be null or empty")
    private String customerAccountId;
}
