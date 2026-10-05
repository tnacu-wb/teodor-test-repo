package uk.co.whitbread.hotel.account.client;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.hotel.account.exceptions.InvalidSessionException;
import uk.co.whitbread.hotel.account.model.ContactDetail;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.HotelCustomerRequest;
import uk.co.whitbread.hotel.account.service.HotelAccountsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class HotelAccountCacheProvider {

    private final HotelAccountsService hotelAccountsService;

    public Customer getCustomer(HotelCustomerRequest request) {
        final Customer customer = hotelAccountsService.getCustomer(request);
        setSessionIdToNullAndValidateCustomerId(customer, request.getCustomerId());
        return customer;
    }

    public void deleteCacheCustomer(String customerId) {
        log.debug("Customer details of {} has been deleted.", sanitizeInputString(customerId));
    }

    private void setSessionIdToNullAndValidateCustomerId(Customer customer, String customerId) {
        Optional.ofNullable(customer)
            .map(c -> {
              // this sessionId will be expired when customer is fetched from cache
             c.setSessionId(null);
             return c;
           })
            .map(Customer::getContactDetail)
            .map(ContactDetail::getEmail)
            .filter(Predicate.isEqual(customerId))
            .orElseThrow(() -> {
              log.error("Email in customer contact details doesn't match to customerId {}", sanitizeInputString(customerId));
              return new InvalidSessionException("Invalid session or token");
            });
    }
}
