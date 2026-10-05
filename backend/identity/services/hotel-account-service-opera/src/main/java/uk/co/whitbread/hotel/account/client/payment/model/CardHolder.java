package uk.co.whitbread.hotel.account.client.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHolder {
    @Schema(description = "Billing address for customer making payment")
    private Address address;

    @Schema(description = "Booker email address.",
            example = "example@email.com")
    private String email;

    @Schema(description = "Name of the card holder.",
            example = "Mr Liam Wilson")
    private String cardHolderName;
}
