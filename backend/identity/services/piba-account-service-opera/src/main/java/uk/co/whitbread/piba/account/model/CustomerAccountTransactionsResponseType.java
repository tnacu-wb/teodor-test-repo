package uk.co.whitbread.piba.account.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerAccountTransactionsResponseType {
    protected List<CustomerAccountCardTransaction> transactions;
}
