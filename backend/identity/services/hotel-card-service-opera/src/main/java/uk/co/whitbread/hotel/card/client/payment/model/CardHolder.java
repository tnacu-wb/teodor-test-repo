package uk.co.whitbread.hotel.card.client.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardHolder {
    private Address address;
    private String email;
    private String cardHolderName;
}
