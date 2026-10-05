package uk.co.whitbread.piba.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAccountInvoiceCriteria {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected LocalDate dateFrom;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected LocalDate dateTo;
}
