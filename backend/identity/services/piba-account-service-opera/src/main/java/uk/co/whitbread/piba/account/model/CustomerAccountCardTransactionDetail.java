package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerAccountCardTransactionDetail {

    protected String description;
    protected int quantity;
    protected Currency netAmount;
    protected Currency taxAmount;
    protected Currency grossAmount;
    protected String guestName;
    protected int invoiceLineItem;
}
