package uk.co.whitbread.company.domain.model.out;

import java.math.BigDecimal;

public record Price(

    BigDecimal amount,
    BigDecimal netAmount,
    String currency) {

}
