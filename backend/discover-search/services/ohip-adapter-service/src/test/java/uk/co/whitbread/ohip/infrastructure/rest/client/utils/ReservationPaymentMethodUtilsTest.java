package uk.co.whitbread.ohip.infrastructure.rest.client.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@ExtendWith(MockitoExtension.class)
class ReservationPaymentMethodUtilsTest {
  @Mock
  private ReservationOhipProperties reservationOhipProperties;

  @Test
  void getFlagHotelIsInList__ShouldReturnOK() {
    //Arrange
    var hotelId = "GATGAT";
    when(reservationOhipProperties.getNonDigitalPaymentHotels()).
        thenReturn(setNonDigitalPaymentHotels("BANBRI,GATGAT,HEAPTI,LONSTM,LONKIN,FRESUD,HEIBAH,FRAMTI,NEWDRO"));

    //Act
    var response = ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(reservationOhipProperties, hotelId);

    //Assert
    assertThat(response, is(true));
  }

  @Test
  void getFlagHotelIsNotInList__ShouldReturnOK() {
    //Arrange
    var hotelId = "WOLMOS";
    when(reservationOhipProperties.getNonDigitalPaymentHotels()).
        thenReturn(setNonDigitalPaymentHotels("BANBRI,GATGAT,HEAPTI,LONSTM,LONKIN,FRESUD,HEIBAH,FRAMTI,NEWDRO"));

    //Act
    var response = ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(reservationOhipProperties, hotelId);

    //Assert
    assertThat(response, is(false));
  }

  @Test
  void getFlagToggleIsOnEmptyList__ShouldReturnOK() {
    //Arrange
    var hotelId = "BANBRI";
    when(reservationOhipProperties.getNonDigitalPaymentHotels()).
        thenReturn(setNonDigitalPaymentHotels(" "));

    //Act
    var response = ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(reservationOhipProperties, hotelId);

    //Assert
    assertThat(response, is(true));
  }

  @Test
  void getListIsParsed__ShouldReturnOK() {
    //Arrange
    var hotelId = "BanbRI";
    when(reservationOhipProperties.getNonDigitalPaymentHotels()).
        thenReturn(setNonDigitalPaymentHotels("ban$bri ,GATGAT,HEAPTI,LONSTM,LONKIN,FRESUD,HEIBAH,FRAMTI,NEWDRO"));

    //Act
    var response = ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(reservationOhipProperties, hotelId);

    //Assert
    assertThat(response, is(true));
  }

  private List<String> setNonDigitalPaymentHotels(String nonDigitalPaymentHotels) {
    return Arrays.stream(nonDigitalPaymentHotels.split(",")).map(h -> h.replaceAll("\\W+", "")
            .toUpperCase()).filter(h -> !h.isBlank()).toList();
  }
}
