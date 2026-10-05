package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TetheredUserAccountOverview {
    protected int primarySchemeCustomerId;
    protected int schemeCustomerId;
    protected String accountName;
    protected String accountNumber;

}
