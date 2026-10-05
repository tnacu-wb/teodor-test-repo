package uk.co.whitbread.hotel.account.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.model.BookingType.BUSINESS;
import static uk.co.whitbread.hotel.account.model.HotelBrandCode.PID;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.account.model.AddressType;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.shared.cdh.model.Address;
import uk.co.whitbread.shared.cdh.model.BookingPreference;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.PaymentCard;
import uk.co.whitbread.shared.cdh.model.PaymentPreference;
import uk.co.whitbread.shared.cdh.model.RoomRequirements;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

  private static final String COMPANY = "company";
  private static final String COUNTRY = "DE";
  private static final String ADDRESS_LINE_1 = "Line1";
  private static final String ADDRESS_LINE_2 = "Line2";
  private static final String ADDRESS_LINE_3 = "Line3";
  private static final String ADDRESS_LINE_4 = "Line4";
  private static final String ADDRESS_LINE_5 = "Line5";
  private static final String POST_CODE = "PR2 2AS";
  private static final String TYPE = "HOME";
  @Mock
  private AddressTypeMapper addressTypeMapper;
  @InjectMocks
  private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

  @Test
  void getCustomerAccountResponseToCustomer() {

    //Arrange
    final String guestHistoryCreation = "2021-02-04";
    final String hotelBrand = "pId ";
    final String reason = "busineSS ";

    final GetCustomerAccountResponse getCustomerAccountResponse = new GetCustomerAccountResponse();
    getCustomerAccountResponse.setBartGuestHistoryCreation(guestHistoryCreation);

    final RoomRequirements roomRequirements = new RoomRequirements();
    roomRequirements.setHotelBrand(hotelBrand);

    final BookingPreference bookingPreference = new BookingPreference();
    bookingPreference.setReason(reason);
    bookingPreference.setRoomRequirements(roomRequirements);
    getCustomerAccountResponse.setBookingPreference(bookingPreference);

    getCustomerAccountResponse.setPaymentPreference(mockPaymentReference());
    when(addressTypeMapper.toAddressType(any())).thenCallRealMethod();

    //Act
    final Customer customer = customerMapper.toCustomer(getCustomerAccountResponse);

    //Assert
    assertThat(customer.getGuestHistoryCreation(), is(LocalDate.parse(guestHistoryCreation)));
    assertThat(customer.getBookingPreference().getReason(), is(BUSINESS));
    assertThat(customer.getBookingPreference().getRoomRequirements().getHotelBrand(), is(PID));

    var billingAddress = customer.getPaymentPreference().getPaymentCard().getBillingAddress();
    assertThat(billingAddress.getType(), is(AddressType.valueOf(TYPE)));
    assertThat(billingAddress.getLine1(), is(ADDRESS_LINE_1));
    assertThat(billingAddress.getLine2(), is(ADDRESS_LINE_2));
    assertThat(billingAddress.getLine3(), is(ADDRESS_LINE_3));
    assertThat(billingAddress.getLine4(), is(ADDRESS_LINE_4));
    assertThat(billingAddress.getLine5(), is(ADDRESS_LINE_5));
    assertThat(billingAddress.getPostCode(), is(POST_CODE));
    assertThat(billingAddress.getCountryCodeISO(), is(COUNTRY));
    assertThat(billingAddress.getCompanyName(), is(COMPANY));
  }

  private static PaymentPreference mockPaymentReference() {
    return PaymentPreference.builder()
        .paymentCard(PaymentCard.builder()
            .billingAddress(new Address(COMPANY, COUNTRY, ADDRESS_LINE_1, ADDRESS_LINE_2,
                ADDRESS_LINE_3, ADDRESS_LINE_4, ADDRESS_LINE_5, POST_CODE, TYPE))
            .build())
        .build();
  }
}
