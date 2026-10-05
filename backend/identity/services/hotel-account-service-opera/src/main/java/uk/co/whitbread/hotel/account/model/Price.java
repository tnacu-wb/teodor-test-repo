package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Price {

    @Digits(integer = 9, fraction = 2)
    @NotNull
    private BigDecimal amount;

    @NotEmpty
    private String currency;
}
