package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccountsResponse {
    List<CustomerAccount> accounts;
    int totalRecordCount;
}
