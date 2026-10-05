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
public class PaymentCardBBCentral extends PaymentCardCommon {
    @NotEmpty(message = "companyId must not be null or empty")
    private String companyId;

    private String cardId;

    @NotEmpty(message = "cardLabel must not be null or empty")
    private String cardLabel;
}
