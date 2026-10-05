package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.distribution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.AvailableCost;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRoom;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.dsitribution.DistributionHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.AvailableCostDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionRatePlanDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionRoomDto;


public class DistributionHotelAvailabilitiesMapperTest {

  @Test
  public void shouldMapDistributionHotelListToDistributionHotelDtoListTest() {

    final List<AvailableCost> availableCosts1 = new ArrayList<>();
    final AvailableCost availableCost1 =
        buildAvailableCost(LocalDate.now(), new BigDecimal("45.65"));
    final AvailableCost availableCost2 =
        buildAvailableCost(LocalDate.now().plusDays(1), new BigDecimal("85.00"));

    availableCosts1.add(availableCost1);
    availableCosts1.add(availableCost2);

    final List<DistributionRoom> rooms1 = new ArrayList<>();
    final DistributionRoom distributionRoom1 = buildDistributionRoom(availableCosts1);
    rooms1.add(distributionRoom1);

    final List<DistributionRatePlan> rates1 = new ArrayList<>();
    DistributionRatePlan distributionRatePlan1 =
        buildDistributionRatePlan("STANDERD", "S", rooms1);
    DistributionRatePlan distributionRatePlan2 =
        buildDistributionRatePlan("ADVANCED", "A", rooms1);
    rates1.add(distributionRatePlan1);
    rates1.add(distributionRatePlan2);

    final List<DistributionRatePlan> rates2 = new ArrayList<>();
    DistributionRatePlan distributionRatePlan3 =
        buildDistributionRatePlan("FLEXRATE", "F", rooms1);
    rates2.add(distributionRatePlan3);

    //given
    List<DistributionHotel> distributionHotelList = new ArrayList<>();

    final DistributionHotel distributionHotel1 =
        buildDistributionHotel("TKINPT", false, rates1);

    final DistributionHotel distributionHotel2 =
        buildDistributionHotel("OXFORD", true, rates2);

    distributionHotelList.add(distributionHotel1);
    distributionHotelList.add(distributionHotel2);

    //when
    DistributionHotelAvailabilitiesMapper distributionHotelAvailabilitiesMapper =
        Mappers.getMapper(DistributionHotelAvailabilitiesMapper.class);

    final List<DistributionHotelDto> distributionHotelDtoListActual
        = distributionHotelAvailabilitiesMapper.toDistributionHotelDtoList(distributionHotelList);

    //then
    assertNotNull(distributionHotelDtoListActual);
    assertFalse(distributionHotelDtoListActual.isEmpty());
    assertEquals(
        distributionHotelList.size(), distributionHotelDtoListActual.size());

    final List<DistributionHotelDto> distributionHotelDtoListExpected =
        mapDistributionHotelListToDistributionHotelDtoList(distributionHotelList);

    assertEquals(distributionHotelDtoListExpected, distributionHotelDtoListActual);

  }

  private List<DistributionHotelDto> mapDistributionHotelListToDistributionHotelDtoList(
      List<DistributionHotel> distributionHotelList) {

    List<DistributionHotelDto> distributionHotelDtoList = new ArrayList<>();

    for (DistributionHotel distributionHotel : distributionHotelList) {

      List<DistributionRatePlan> rates = distributionHotel.getRates();

      List<DistributionRatePlanDto> distributionRatePlanDtos = mapRatesToRatePlanDtos(rates);

      DistributionHotelDto distributionHotelDto =
          new DistributionHotelDto(
              distributionHotel.getHotelCode(), distributionHotel.getHotelName(),
              distributionHotel.getHotelBrand(), distributionHotel.getAvailable(),
              distributionHotel.getArrivalDateToday(),
              distributionHotel.getPmsSource(), distributionRatePlanDtos);

      distributionHotelDtoList.add(distributionHotelDto);

    }

    return distributionHotelDtoList;
  }

  private List<DistributionRatePlanDto> mapRatesToRatePlanDtos(
      final List<DistributionRatePlan> rates) {

    List<DistributionRatePlanDto> distributionRatePlanDtos = new ArrayList<>();
    for (DistributionRatePlan distributionRatePlan : rates) {

      List<DistributionRoom> rooms = distributionRatePlan.getRooms();
      List<DistributionRoomDto> roomDtoList = mapRoomsToRoomDtoList(rooms);
      DistributionRatePlanDto distributionRatePlanDto =
          new DistributionRatePlanDto(
              distributionRatePlan.getCode(), distributionRatePlan.getClassification(), roomDtoList);

      distributionRatePlanDtos.add(distributionRatePlanDto);
    }
    return distributionRatePlanDtos;
  }

  private List<DistributionRoomDto> mapRoomsToRoomDtoList(
      final List<DistributionRoom> rooms) {

    final List<DistributionRoomDto> distributionRoomDtoList = new ArrayList<>();
    for (DistributionRoom distRoom : rooms) {

      final List<AvailableCostDto> availableCosts =
          mapAvailableCostListToAvailableCostDtoList(distRoom.getAvailableCosts());
      final DistributionRoomDto distributionRoomDto =
          new DistributionRoomDto(distRoom.getRoomType(), distRoom.getCotRequired(),
              distRoom.getQtyRequested(), availableCosts);

      distributionRoomDtoList.add(distributionRoomDto);
    }

    return distributionRoomDtoList;
  }

  private List<AvailableCostDto> mapAvailableCostListToAvailableCostDtoList(
      final List<AvailableCost> availableCosts) {
    final List<AvailableCostDto> availableCostDtoList = new ArrayList<>();

    for (AvailableCost cost : availableCosts) {
      final AvailableCostDto availableCostDto = new AvailableCostDto(cost.getDate(),
          cost.getAmount(), cost.getCurrency(), cost.getQtyAvailable());
      availableCostDtoList.add(availableCostDto);
    }
    return availableCostDtoList;
  }

  private AvailableCost buildAvailableCost(final LocalDate date, final BigDecimal amount) {

    return AvailableCost.builder()
        .date(date)
        .amount(amount)
        .currency("G")
        .qtyAvailable(5)
        .build();
  }

  private DistributionRoom buildDistributionRoom(final List<AvailableCost> availableCosts) {
    return DistributionRoom.builder()
        .roomType("DBL")
        .cotRequired(false)
        .qtyRequested(1)
        //.qtyAvailable(5)
        .availableCosts(availableCosts)
        .build();
  }

  private DistributionRatePlan buildDistributionRatePlan(
      final String code, final String classification, final List<DistributionRoom> rooms) {
    return DistributionRatePlan.builder()
        .code(code)
        .classification(classification)
        .rooms(rooms)
        .build();
  }

  private DistributionHotel buildDistributionHotel(
      final String hotelCode, final boolean limitedAvail, final List<DistributionRatePlan> rates) {
    return DistributionHotel.builder()
        .hotelCode(hotelCode)
        .hotelName(hotelCode)
        .hotelBrand("pi.com")
        .available(true)
        //.limitedAvailability(limitedAvail)
        .arrivalDateToday(true)
        .pmsSource("OPERA")
        .rates(rates)
        .build();
  }

}
