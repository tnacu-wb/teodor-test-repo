package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.hotel.account.service.generated.models.CustomerRequest;
import uk.co.whitbread.reservation.domain.model.in.BookerDetails;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.mapper.CustomerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.HotelAccountClient;

import static org.mockito.Mockito.*;

class HotelAccountOutPortImplTest {

  @Mock
  private HotelAccountClient hotelAccountClient;

  @Mock
  private CustomerRequestMapper customerRequestMapper;

  @InjectMocks
  private HotelAccountOutPortImpl hotelAccountOutPort;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void updateCustomer_shouldCallSendUpdateCustomerRequest() {
    // Arrange
    BookerDetails bookerDetails = mock(BookerDetails.class);
    String authorization = "Bearer token";
    CustomerRequest customerRequest = mock(CustomerRequest.class);
    var customerId = "customer123";

    when(customerRequestMapper.toDto(bookerDetails)).thenReturn(customerRequest);

    // Act
    hotelAccountOutPort.updateCustomer(bookerDetails, customerId, authorization);

    // Assert
    verify(customerRequestMapper).toDto(bookerDetails);
    verify(hotelAccountClient).sendUpdateCustomerRequest(customerRequest, customerId, authorization);
  }
}