package uk.co.whitbread.payments.model.booking.basket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BasketResponse {
    
    private String reference;

    private String basketUri;
}
