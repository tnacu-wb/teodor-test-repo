package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePrice;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRateReservation;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RoomRateOhipMapperImpl.class})
class RoomRateOhipMapperTest {

  @Autowired
  RoomRateOhipMapper roomRateOhipMapper;

  @Test
  void roomRateReservationToRoomRateType__ShouldReturnOK() {
    //Arrange
    RoomRateReservation roomRateReservation = createRoomRates();

    //Act
    var roomRateType = roomRateOhipMapper.toRoomRateTypeModel(roomRateReservation, null, "44", "OTH");

    //Assert
    assertEquals("SDB", roomRateType.getRoomType());
    assertEquals("DAILY", roomRateType.getRatePlanCode());
    assertEquals(1, roomRateType.getNumberOfUnits());
    assertNull(roomRateType.getRates());
    assertEquals(false, roomRateType.getFixedRate());

  }

  @Test
  void roomRateReservationToRoomRateTypeWithRatePrice__ShouldReturnOK() {
    //Arrange
    RoomRateReservation roomRateReservation = createRoomRates();
    RatePrice ratePrice = createRatePrice(LocalDate.now(), BigDecimal.valueOf(100.0));

    //Act
    var roomRateType = roomRateOhipMapper.toRoomRateTypeModel(roomRateReservation, ratePrice, "44", "OTH");

    //Assert
    assertEquals("SDB", roomRateType.getRoomType());
    assertEquals("DAILY", roomRateType.getRatePlanCode());
    assertEquals(1, roomRateType.getNumberOfUnits());
    assertEquals(LocalDate.now(), roomRateType.getStart());
    assertEquals(LocalDate.now(), roomRateType.getEnd());
    assertEquals(BigDecimal.valueOf(100.0),
        roomRateType.getRates().getRate().get(0).getBase().getAmountBeforeTax());
    assertEquals(true, roomRateType.getFixedRate());

  }

  @Test
  void roomRateReservationToRoomRateTypesWithRatePrice__ShouldReturnOK() {
    //Arrange
    RoomRateReservation roomRateReservation = createRoomRates();
    RatePrice ratePrice = createRatePrice(LocalDate.now(), BigDecimal.valueOf(100.0));
    RatePrice ratePrice2 = createRatePrice(LocalDate.now().plusDays(1), BigDecimal.valueOf(110.0));
    RatePrice ratePrice3 = createRatePrice(LocalDate.now().plusDays(2), BigDecimal.valueOf(120.0));
    roomRateReservation.setRatePrices(List.of(ratePrice, ratePrice2, ratePrice3));

    BookingChannel bookingChannel = createBookingChannel();


    //Act
    var roomRateType = roomRateOhipMapper
        .toRoomRateTypesModel(roomRateReservation, "44", "OTH", bookingChannel);

    //Assert
    assertEquals("SDB", roomRateType.get(0).getRoomType());
    assertEquals("DAILY", roomRateType.get(0).getRatePlanCode());
    assertEquals(1, roomRateType.get(0).getNumberOfUnits());
    assertEquals(LocalDate.now(), roomRateType.get(0).getStart());
    assertEquals(LocalDate.now(), roomRateType.get(0).getEnd());
    assertEquals(BigDecimal.valueOf(100.0),
        roomRateType.get(0).getRates().getRate().get(0).getBase().getAmountBeforeTax());

    assertEquals("SDB", roomRateType.get(1).getRoomType());
    assertEquals("DAILY", roomRateType.get(1).getRatePlanCode());
    assertEquals(1, roomRateType.get(1).getNumberOfUnits());
    assertEquals(LocalDate.now().plusDays(1), roomRateType.get(1).getStart());
    assertEquals(LocalDate.now().plusDays(1), roomRateType.get(1).getEnd());
    assertEquals(BigDecimal.valueOf(110.0),
        roomRateType.get(1).getRates().getRate().get(0).getBase().getAmountBeforeTax());

    assertEquals("SDB", roomRateType.get(2).getRoomType());
    assertEquals("DAILY", roomRateType.get(2).getRatePlanCode());
    assertEquals(1, roomRateType.get(2).getNumberOfUnits());
    assertEquals(LocalDate.now().plusDays(2), roomRateType.get(2).getStart());
    assertEquals(LocalDate.now().plusDays(2), roomRateType.get(2).getEnd());
    assertEquals(BigDecimal.valueOf(120.0),
        roomRateType.get(2).getRates().getRate().get(0).getBase().getAmountBeforeTax());

  }

  private BookingChannel createBookingChannel() {
    return BookingChannel.builder()
        .channel("DISTR")
        .subchannel("AGENCY")
        .build();
  }

  private RatePrice createRatePrice(LocalDate date, BigDecimal amount) {
    return RatePrice.builder()
        .priceStartDate(date)
        .priceEndDate(date)
        .amount(amount)
        .build();
  }

  private RoomRateReservation createRoomRates() {

    return RoomRateReservation.builder()
        .start("2015-10-20")
        .end("2015-10-21")
        .roomType("SDB")
        .ratePlanCode("DAILY")
        .build();
  }

}