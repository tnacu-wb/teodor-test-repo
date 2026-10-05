package uk.co.whitbread.piba.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAccountTransactionsByDateCriteria {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected LocalDate dateFrom;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected LocalDate dateTo;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected String transactionTypes;

    @Valid
    @Size(min = 1,max=19)
    protected String pan;

}
