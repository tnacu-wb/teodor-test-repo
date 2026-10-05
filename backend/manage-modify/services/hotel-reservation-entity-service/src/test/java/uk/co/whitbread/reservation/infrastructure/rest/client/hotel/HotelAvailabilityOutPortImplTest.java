package uk.co.whitbread.reservation.infrastructure.rest.client.hotel;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.whitbread.hotel.entity.service.generated.models.hotel.HotelAvailabilityByIdsV2Dto;
import uk.co.whitbread.reservation.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.reservation.domain.model.availability.in.Rate;
import uk.co.whitbread.reservation.domain.model.availability.in.Room;
import uk.co.whitbread.reservation.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.mapper.HotelAvailabilityResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.mapper.HotelEntityAvailabilityRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.model.HotelAvailabilityV2RequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotel.service.HotelAvailabilityClient;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class HotelAvailabilityOutPortImplTest {

  @Mock
  private HotelAvailabilityClient hotelAvailabilityClient;

  @Mock
  private HotelEntityAvailabilityRequestMapper hotelAvailabilityRequestMapper;

  @Mock
  private HotelAvailabilityResponseMapper hotelAvailabilityResponseMapper;

  private HotelAvailabilityOutPortImpl hotelAvailabilityOutPort;

  @BeforeEach
  public void before() {
    hotelAvailabilityOutPort = new HotelAvailabilityOutPortImpl(hotelAvailabilityClient,
        hotelAvailabilityRequestMapper,
        hotelAvailabilityResponseMapper);
  }

  @Test
  void testGetHotelAvailabilitiesByIdsV2() {
// Arrange
    HotelAvailabilityByIdsV2Request request = HotelAvailabilityByIdsV2Request.builder()
        .bookingChannel(BookingChannel.builder().build())
        .hotelIds(List.of("hotel1"))
        .arrivalDate(LocalDate.of(2024, 10, 28))
        .departureDate(LocalDate.of(2024, 10, 30))
        .rooms(Collections.singletonList(Room.builder().build()))
        .rates(Rate.builder().build())
        .vatNotRequired(false)
        .isOTA(true)
        .build();

    HotelAvailabilityV2RequestDto requestDto = new HotelAvailabilityV2RequestDto();
    HotelAvailabilityByIdsV2Dto availDto = new HotelAvailabilityByIdsV2Dto();
    HotelAvailabilityByIdsV2 expectedResponse = new HotelAvailabilityByIdsV2();

    when(hotelAvailabilityRequestMapper.toDto(request)).thenReturn(requestDto);
    when(hotelAvailabilityClient.getHotelAvailabilityV2(any())).thenReturn(availDto);
    when(hotelAvailabilityResponseMapper.toDomainModel(availDto)).thenReturn(expectedResponse);

// Act
    HotelAvailabilityByIdsV2 actualResponse = hotelAvailabilityOutPort.getHotelAvailabilitiesByIdsV2(request);

// Assert
    assertEquals(expectedResponse, actualResponse);
    verify(hotelAvailabilityRequestMapper).toDto(request);
    verify(hotelAvailabilityClient).getHotelAvailabilityV2(requestDto);
    verify(hotelAvailabilityResponseMapper).toDomainModel(availDto);
  }
}

