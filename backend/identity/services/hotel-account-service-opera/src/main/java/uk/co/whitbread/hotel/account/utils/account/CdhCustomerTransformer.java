package uk.co.whitbread.hotel.account.utils.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.account.mapper.CustomerMapper;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.SearchCustomerRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.SearchCustomerAccountRequest;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class CdhCustomerTransformer {

    private final CustomerMapper customerMapper;

    public CustomerAccountRequest transformToCustomerAccountRequest(CustomerRequest updateCustomerRequest, Customer customer) {
        Customer updatedCustomer = customerMapper.updateCustomerFromCustomerRequest(updateCustomerRequest, customer);
        return customerMapper.toCustomerAccountRequest(updatedCustomer);
    }

    public Customer getCustomerAccountResponseToCustomer(GetCustomerAccountResponse getCustomerAccountResponse) {
        return customerMapper.toCustomer(getCustomerAccountResponse);
    }

    public List<Customer> getCustomerAccountResponseToCustomer(List<GetCustomerAccountResponse> getCustomerAccountResponse) {
        return customerMapper.toListOfCustomer(getCustomerAccountResponse);
    }

    public SearchCustomerAccountRequest transformToSearchCustomerAccountRequest(SearchCustomerRequest searchCustomerRequest) {
        return customerMapper.toSearchCustomerAccountRequest(searchCustomerRequest);
    }
}
