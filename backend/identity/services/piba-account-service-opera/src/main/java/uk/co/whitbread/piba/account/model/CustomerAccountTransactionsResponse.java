package uk.co.whitbread.piba.account.model;

import lombok.Data;

@Data
public class CustomerAccountTransactionsResponse {
    protected CustomerAccountTransactionsResponseType response;
    protected PagingResult pagingResult;

}
