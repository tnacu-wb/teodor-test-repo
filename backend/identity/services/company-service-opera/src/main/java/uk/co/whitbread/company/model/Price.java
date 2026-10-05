package uk.co.whitbread.company.model;

import jakarta.validation.constraints.DecimalMax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {

    @DecimalMax(value = "999.99", message = "Maximum Dinner Spend is 999.99")
    private int amount;
    private String currency;
}
