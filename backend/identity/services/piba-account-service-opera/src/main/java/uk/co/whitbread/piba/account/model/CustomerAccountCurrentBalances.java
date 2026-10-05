package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerAccountCurrentBalances extends CustomerAccount {

    protected Currency outstanding;
    protected Currency newTransactions;
    protected Currency available;
    protected Currency creditLimit;
    protected Currency currentBalance;
    protected Currency interimPayments;
    public CustomerAccountCurrentBalances(String errorCode) {
        this.errorCode = errorCode;
    }
}
