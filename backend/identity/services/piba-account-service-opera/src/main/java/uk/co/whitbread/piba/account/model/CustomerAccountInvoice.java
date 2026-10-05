package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAccountInvoice {

    protected String statementDate;
    protected String invoiceNo;
    protected Currency broughtForward;
    protected Currency paymentsReceived;
    protected Currency overdueBalance;
    protected Currency invoiceValue;
    protected Currency statementBalance;
    protected Integer fileAutoID;
}