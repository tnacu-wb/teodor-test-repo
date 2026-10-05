package uk.co.whitbread.reservation.domain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.properties.DigitalKeyProperties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalKeyTest {

  @InjectMocks
  private DigitalKeyFeature digitalKeyFeature;
  private DigitalKeyProperties digitalKeyProperties;
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  private FeatureFlag mockedfeatureFlag;


  @BeforeEach
  void setUp() {
    digitalKeyProperties = mock(DigitalKeyProperties.class);
    unleashWrapper = mock(UnleashWrapper.class);
    mockedfeatureFlag = mock(FeatureFlag.class);
    digitalKeyFeature = new DigitalKeyFeature(digitalKeyProperties, unleashWrapper);
  }

  @Test
  void shouldReturnFalseWhenFeatureFlagIsDisabled() {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedfeatureFlag);
    when(unleashWrapper.isEnabled(mockedfeatureFlag.getMobileDigitalKey()))
        .thenReturn(false);

    ReservationByBasketRefResponse response = new ReservationByBasketRefResponse();
    response.setReservationByIdList(Collections.emptyList());
    boolean result = digitalKeyFeature.isDigitalKeyAvailable(response);
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenReservationCountExceedsMaxRooms() {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedfeatureFlag);
    when(unleashWrapper.isEnabled(mockedfeatureFlag.getMobileDigitalKey()))
        .thenReturn(true);
    ReservationByIdResponse reservation1 = new ReservationByIdResponse();
    ReservationByIdResponse reservation2 = new ReservationByIdResponse();
    ReservationByBasketRefResponse response = new ReservationByBasketRefResponse();
    response.setReservationByIdList(Arrays.asList(reservation1, reservation2));

    boolean result = digitalKeyFeature.isDigitalKeyAvailable(response);
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenReservationStatusIsInvalid() {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedfeatureFlag);
    when(unleashWrapper.isEnabled(mockedfeatureFlag.getMobileDigitalKey()))
        .thenReturn(true);


    ReservationByIdResponse reservation = new ReservationByIdResponse();
    reservation.setReservationStatus("Cancelled");

    ReservationByBasketRefResponse response = new ReservationByBasketRefResponse();
    response.setReservationByIdList(List.of(reservation));

    boolean result = digitalKeyFeature.isDigitalKeyAvailable(response);
    assertFalse(result);
  }

  @Test
  void shouldReturnFalseWhenHotelIdIsInvalid() {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedfeatureFlag);
    when(unleashWrapper.isEnabled(mockedfeatureFlag.getMobileDigitalKey()))
        .thenReturn(true);
    when(digitalKeyProperties.getDkHotels()).thenReturn(Set.of("HEAP1","TEST1"));

    ReservationByIdResponse reservation = new ReservationByIdResponse();
    reservation.setReservationStatus("Cancelled");

    ReservationByBasketRefResponse response = new ReservationByBasketRefResponse();
    response.setReservationByIdList(List.of(reservation));

    boolean result = digitalKeyFeature.isDigitalKeyAvailable(response);
    assertFalse(result);
  }

  @Test
  void shouldReturnTrueWhenAllConditionsAreMet() {
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedfeatureFlag);
    when(unleashWrapper.isEnabled(mockedfeatureFlag.getMobileDigitalKey()))
        .thenReturn(true);
    when(digitalKeyProperties.getDkHotels()).thenReturn(Set.of("LINMIL"));
    when(digitalKeyProperties.getMaxRooms()).thenReturn(1);
    when(digitalKeyProperties.getReservationStatues()).thenReturn(Set.of("Reserved"));

    ReservationByIdResponse reservation = new ReservationByIdResponse();
    reservation.setReservationStatus("Reserved");
    ReservationByBasketRefResponse response = new ReservationByBasketRefResponse();
    response.setReservationByIdList(List.of(reservation));
    response.setHotelId("LINMIL");

    boolean result = digitalKeyFeature.isDigitalKeyAvailable(response);
    assertTrue(result);
  }
}