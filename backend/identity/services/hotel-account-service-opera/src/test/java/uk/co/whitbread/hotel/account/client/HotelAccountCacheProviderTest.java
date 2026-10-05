package uk.co.whitbread.hotel.account.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.HotelAccountMicroserviceApplication;
import uk.co.whitbread.hotel.account.exceptions.InvalidSessionException;
import uk.co.whitbread.hotel.account.model.BookingChannelCode;
import uk.co.whitbread.hotel.account.model.ContactDetail;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.HotelBrandCode;
import uk.co.whitbread.hotel.account.model.HotelCustomerRequest;
import uk.co.whitbread.hotel.account.service.HotelAccountsService;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = HotelAccountMicroserviceApplication.class)
@TestPropertySource(properties = {"spring.cache.type=simple"})
class HotelAccountCacheProviderTest {

    private static final String CUSTOMER_ID_1 = "test1@email.com";
    private static final String CUSTOMER_ID_2 = "test2@email.com";
    private static final HotelCustomerRequest request1 = buildHotelCustomerRequest(CUSTOMER_ID_1);
    private static final HotelCustomerRequest request2 = buildHotelCustomerRequest(CUSTOMER_ID_2);

    @Autowired
    private HotelAccountCacheProvider target;

    @MockitoBean
    private HotelAccountsService hotelAccountsService;

    @BeforeEach
    void setupAndTestDeleteCache() {
        target.deleteCacheCustomer(CUSTOMER_ID_1);
        target.deleteCacheCustomer(CUSTOMER_ID_2);
    }

    @Test
    void getCustomerShouldReturnErrorIfEmailProvidedDoesntMatchToTheOneFound() {
        final Customer customer1 = new Customer();
        customer1.setContactDetail(new ContactDetail());
        customer1.getContactDetail().setEmail(CUSTOMER_ID_1);

        Mockito.when(hotelAccountsService.getCustomer(request1)).thenReturn(customer1);
        assertThrows(InvalidSessionException.class, () -> target.getCustomer(request2));
    }

    @Test
    void getCustomerShouldReturnErrorIfCustomerIsNull() {
        assertThrows(InvalidSessionException.class, () -> target.getCustomer(request1));

    }

    @Test
    void getCustomerShouldReturnErrorIfContactDetailsIsNull() {
        final Customer customer1 = new Customer();

        Mockito.when(hotelAccountsService.getCustomer(request1)).thenReturn(customer1);

        assertThrows(InvalidSessionException.class, () -> target.getCustomer(request1));
    }

    @Test
    void getCustomerShouldReturnErrorIfEmailIsNull() {
        final Customer customer1 = new Customer();
        customer1.setContactDetail(new ContactDetail());

        Mockito.when(hotelAccountsService.getCustomer(buildHotelCustomerRequest(null))).thenReturn(customer1);

        assertThrows(InvalidSessionException.class, () -> target.getCustomer(request1));
    }

    private static HotelCustomerRequest buildHotelCustomerRequest(String customerId) {
        return HotelCustomerRequest.builder().customerId(customerId).business(false)
            .bookingChannel(BookingChannelCode.WEB).hotelBrand(HotelBrandCode.PI)
            .build();
    }
}
