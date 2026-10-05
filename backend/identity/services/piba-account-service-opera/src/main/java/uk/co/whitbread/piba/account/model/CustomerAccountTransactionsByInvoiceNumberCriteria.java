package uk.co.whitbread.piba.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerAccountTransactionsByInvoiceNumberCriteria {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    protected int invoiceNumber;
}
