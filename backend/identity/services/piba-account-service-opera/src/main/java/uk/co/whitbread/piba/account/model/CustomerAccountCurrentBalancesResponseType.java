package uk.co.whitbread.piba.account.model;

import lombok.Data;

import java.util.List;

@Data
public class CustomerAccountCurrentBalancesResponseType {
    private List<CustomerAccountCurrentBalances> currentBalances;
}
