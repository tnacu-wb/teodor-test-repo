package uk.co.whitbread.piba.account.model;

import java.util.List;

import lombok.Data;

@Data
public class CustomerAccountInvoiceResponseType {
    protected List<CustomerAccountInvoice> invoices;
}