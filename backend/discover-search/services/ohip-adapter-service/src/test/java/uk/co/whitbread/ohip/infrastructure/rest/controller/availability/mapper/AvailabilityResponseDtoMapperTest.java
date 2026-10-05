package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityDailyPrice;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityResult;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoom;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomPriceBreakdown;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomRate;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoomType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AvailabilityResponseDtoMapperImpl.class)
class AvailabilityResponseDtoMapperTest {

  @Autowired
  AvailabilityResponseDtoMapper availabilityResponseDtoMapper;

  @Test
  void toDto__ShouldReturnOK() {
    //Arrange
    AvailabilityResult availabilityResult = createAvailabilityResult();

    //Act
    var availabilityResponseDto = availabilityResponseDtoMapper.toDto(availabilityResult);

    //Assert
    assertEquals("TestHotelId", availabilityResponseDto.getHotelId());
    assertEquals("2022-01-01", availabilityResponseDto.getStartDate());
    assertTrue(availabilityResponseDto.isAvailable());
    assertEquals("TestRoomType", availabilityResponseDto.getRoomRates().get(0).getRoomTypes().get(0).getRoomType());
    assertEquals("TestRatePlanCode",
        availabilityResponseDto.getRoomRates().get(0).getRatePlanCode());
    assertEquals("GBP",
        availabilityResponseDto.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown().getCurrencyCode());
    assertEquals(new BigDecimal(10.0),
        availabilityResponseDto.getRoomRates().get(0).getRoomTypes().get(0)
            .getRooms().get(0).getRoomPriceBreakdown().getTotalTaxAmount());


  }

  private AvailabilityResult createAvailabilityResult() {
    AvailabilityResult availabilityResult = AvailabilityResult.builder()
        .hotelId("TestHotelId")
        .startDate("2022-01-01")
        .endDate("2022-01-03")
        .available(true)
        .roomRates(createRoomRateList())
        .build();
    return availabilityResult;
  }

  private List<AvailabilityRoomRate> createRoomRateList() {
    List<AvailabilityRoomRate> roomRateList = new LinkedList<>();
    AvailabilityRoomRate availabilityRoomRate = AvailabilityRoomRate.builder()
        .ratePlanCode("TestRatePlanCode")
        .roomType(AvailabilityRoomType.builder()
            .roomType("TestRoomType")
            .room(createRoom())
            .build())
        .build();
    roomRateList.add(availabilityRoomRate);
    return roomRateList;
  }

  private AvailabilityRoom createRoom() {
    return AvailabilityRoom.builder()
        .roomPriceBreakdown(createRoomPriceBreackdown())
        .build();
  }

  private AvailabilityRoomPriceBreakdown createRoomPriceBreackdown() {
    return AvailabilityRoomPriceBreakdown.builder()
        .totalNetAmount(new BigDecimal(110.0))
        .totalGrossAmount(new BigDecimal(100.0))
        .totalTaxAmount(new BigDecimal(10.0))
        .currencyCode("GBP")
        .dailyPrices(createPriceBreakdownResponseList())
        .build();
  }

  private List<AvailabilityDailyPrice> createPriceBreakdownResponseList() {
    List<AvailabilityDailyPrice> availabilityPriceBreakdownList = new LinkedList<>();
    AvailabilityDailyPrice availabilityPriceBreakdown = AvailabilityDailyPrice.builder()
        .date("2022-01-01")
        .netPrice(new BigDecimal(55.0))
        .build();
    availabilityPriceBreakdownList.add(availabilityPriceBreakdown);
    availabilityPriceBreakdown = AvailabilityDailyPrice.builder()
        .date("2022-01-02")
        .netPrice(new BigDecimal(55.0))
        .build();
    availabilityPriceBreakdownList.add(availabilityPriceBreakdown);
    return availabilityPriceBreakdownList;
  }

}