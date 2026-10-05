package uk.co.whitbread.piba.account.model;

import lombok.Data;

@Data
public class CustomerAccountTransactionsCriteria {
    protected CustomerAccountTransactionsByDateCriteria dateSearch;
    protected CustomerAccountTransactionsByInvoiceNumberCriteria invoiceNumberSearch;
}
