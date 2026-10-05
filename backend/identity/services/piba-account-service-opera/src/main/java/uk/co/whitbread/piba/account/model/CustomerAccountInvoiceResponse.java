package uk.co.whitbread.piba.account.model;

import lombok.Data;

@Data
public class CustomerAccountInvoiceResponse {
    protected CustomerAccountInvoiceResponseType response;
    protected PagingResult pagingResult;
}