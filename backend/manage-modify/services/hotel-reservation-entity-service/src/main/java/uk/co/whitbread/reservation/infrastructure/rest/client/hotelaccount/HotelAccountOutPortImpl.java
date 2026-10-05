package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import uk.co.whitbread.reservation.domain.model.in.BookerDetails;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelAccountOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.mapper.CustomerRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.HotelAccountClient;

@Slf4j
@RequiredArgsConstructor
public class HotelAccountOutPortImpl implements HotelAccountOutPort {

  private final HotelAccountClient hotelAccountClient;
  private final CustomerRequestMapper customerRequestMapper;

  @Override
  @Async
  public void updateCustomer(BookerDetails bookerDetails, String customerId, String authorization) {
    hotelAccountClient.sendUpdateCustomerRequest(customerRequestMapper.toDto(bookerDetails), customerId, authorization);
  }
}