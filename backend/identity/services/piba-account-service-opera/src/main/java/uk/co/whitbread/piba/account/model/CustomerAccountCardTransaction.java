package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAccountCardTransaction {

    protected LocalDate invoiceDate;
    protected String invoiceNo;
    protected LocalDateTime transactionDate;
    protected Currency netAmount;
    protected Currency taxAmount;
    protected Currency grossAmount;
    protected String location;
    protected String pan;
    protected String cardName;
    protected String purchaseOrderReference;
    protected String customerOwnRef;
    protected String salesOrderNumber;
    protected List<CustomerAccountCardTransactionDetail> lineItems;

}
