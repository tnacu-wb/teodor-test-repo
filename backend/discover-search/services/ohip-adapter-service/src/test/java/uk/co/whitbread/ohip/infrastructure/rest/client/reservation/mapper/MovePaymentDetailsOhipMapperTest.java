package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.ResPaymentCardType;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@ExtendWith(MockitoExtension.class)
class MovePaymentDetailsOhipMapperTest {

  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  private ReservationOhipProperties reservationOhipProperties;

  private MovePaymentDetailsOhipMapper movePaymentDetailsOhipMapper;

  @BeforeEach
  void setUp() {
    movePaymentDetailsOhipMapper = new MovePaymentDetailsOhipMapperImpl();
    movePaymentDetailsOhipMapper.toReservationOhipPropertiesForModel(
        reservationOhipProperties, unleashWrapper);
  }

  @Test
  void createChangeReservationRequest_WhenFfDisabled_ShouldUseCaPaymentMethod() {
    //Arrange
    ReservationPaymentMethodType reservationPaymentMethod = mockReservationPaymentMethodType();
    CreditCardInfo creditCardInfo = mockCreditCardInfo();

    FeatureFlag featureFlag = mock(FeatureFlag.class);
    FeatureFlag.Feature feature = mock(FeatureFlag.Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getSetDefaultPaymentMethodDs()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(false);
    when(reservationOhipProperties.getDefaultPaymentMethod()).thenReturn("CA");
    when(reservationOhipProperties.getFolio()).thenReturn(1);

    //Act
    var changeReservation =
        movePaymentDetailsOhipMapper.toChangeReservationDto(reservationPaymentMethod,
            creditCardInfo);

    //Assert
    assertEquals(2, changeReservation.getReservations().get(0).getReservationPaymentMethods().size());
    assertEquals("CA",
        changeReservation.getReservations().get(0).getReservationPaymentMethods().get(0)
            .getPaymentMethod());
  }

  @Test
  void createChangeReservationRequest_WhenFfEnabled_ShouldUseDsPaymentMethod() {
    //Arrange
    ReservationPaymentMethodType reservationPaymentMethod = mockReservationPaymentMethodType();
    CreditCardInfo creditCardInfo = mockCreditCardInfo();

    FeatureFlag featureFlag = mock(FeatureFlag.class);
    FeatureFlag.Feature feature = mock(FeatureFlag.Feature.class);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(featureFlag.getSetDefaultPaymentMethodDs()).thenReturn(feature);
    when(unleashWrapper.isEnabled(feature)).thenReturn(true);
    when(reservationOhipProperties.getDefaultPaymentMethodDs()).thenReturn("DS");
    when(reservationOhipProperties.getFolio()).thenReturn(1);

    //Act
    var changeReservation =
        movePaymentDetailsOhipMapper.toChangeReservationDto(reservationPaymentMethod,
            creditCardInfo);

    //Assert
    assertEquals(2, changeReservation.getReservations().get(0).getReservationPaymentMethods().size());
    assertEquals("DS",
        changeReservation.getReservations().get(0).getReservationPaymentMethods().get(0)
            .getPaymentMethod());
  }


  private ReservationPaymentMethodType mockReservationPaymentMethodType() {

    var resPaymentCardType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    resPaymentCardType.setCardNumberMasked("XXXXXXXXXXXX1100");
    resPaymentCardType.cardType(CardTypeType.MC);
    resPaymentCardType.setCardHolderName("Tester Testerson");
    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentCard(resPaymentCardType);
    reservationPaymentMethod.setPaymentMethod("MC");
    reservationPaymentMethod.setFolioView(1);
    return reservationPaymentMethod;
  }

  private CreditCardInfo mockCreditCardInfo() {
    var cardType = new ResPaymentCardType();
    cardType.setCardNumber("5657601229143938611");
    cardType.setExpirationDate(LocalDate.parse("2030-03-01"));
    cardType.setCardNumberLast4Digits("8611");
    var cardInfo = new CreditCardInfo();
    cardInfo.setCreditCard(cardType);
    return cardInfo;
  }
}

