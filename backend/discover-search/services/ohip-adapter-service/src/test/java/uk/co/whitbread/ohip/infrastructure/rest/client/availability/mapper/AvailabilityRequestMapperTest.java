package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AvailabilityRequestMapperImpl.class)
class AvailabilityRequestMapperTest {

  @Autowired
  private AvailabilityRequestMapper availabilityRequestMapper;

  private static AvailabilityRoomSearchCriteria createRoomPriceBreakdownSearchCriteria() {
    return AvailabilityRoomSearchCriteria.builder()
        .hotelId("TestHotelId")
        .ratePlanCode("TestRatePlanCode")
        .roomType("TestRoomType")
        .numberOfRooms(1)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-03")
        .adults(1)
        .children(0)
        .build();
  }

  private static AvailabilitySearchRequest createAvailabilityRoomSearchCriteria() {
    return AvailabilitySearchRequest.builder()
        .hotelId("TestHotelId")
        .roomTypes(new String[]{"TestRoomType"})
        .numberOfRooms(1)
        .arrivalDate("2022-01-01")
        .departureDate("2022-01-03")
        .adults(new Integer[]{1})
        .children(new Integer[]{0})
        .build();
  }

  @Test
  void toRoomPriceBreakdownRequest__ShouldReturnOK() {
    //Act
    var availabilityRequest =
        availabilityRequestMapper
            .toAvailabilityRequestDto(createRoomPriceBreakdownSearchCriteria());

    //Assert
    assertEquals("TestHotelId", availabilityRequest.getHotelId());
    assertEquals("TestRatePlanCode", availabilityRequest.getRatePlanCode());
    assertEquals(Collections.singletonList("TestRoomType"), availabilityRequest.getRoomTypes());
    assertEquals(1, availabilityRequest.getRoomStayQuantity());
    assertEquals("2022-01-01", availabilityRequest.getRoomStayStartDate());
    assertEquals("2022-01-03", availabilityRequest.getRoomStayEndDate());
    assertEquals(Collections.singletonList(1), availabilityRequest.getAdults());
    assertEquals(Collections.singletonList(0), availabilityRequest.getChildren());
  }

  @Test
  void toHotelAvailabilityRequest__ShouldReturnOK() {
    //Act
    var availabilityRequest =
        availabilityRequestMapper.toAvailabilityRequestDto(createAvailabilityRoomSearchCriteria());

    //Assert
    assertEquals("TestHotelId", availabilityRequest.getHotelId());
    assertEquals(Collections.singletonList("TestRoomType"), availabilityRequest.getRoomTypes());
    assertEquals(1, availabilityRequest.getRoomStayQuantity());
    assertEquals("2022-01-01", availabilityRequest.getRoomStayStartDate());
    assertEquals("2022-01-03", availabilityRequest.getRoomStayEndDate());
    assertEquals(Collections.singletonList(1), availabilityRequest.getAdults());
    assertEquals(Collections.singletonList(0), availabilityRequest.getChildren());
  }

}