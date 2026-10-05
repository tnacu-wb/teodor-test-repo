package uk.co.whitbread.payments.model.booking.basket;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BasketError {

    private Integer errCode;
    private String debugMessage;
}
