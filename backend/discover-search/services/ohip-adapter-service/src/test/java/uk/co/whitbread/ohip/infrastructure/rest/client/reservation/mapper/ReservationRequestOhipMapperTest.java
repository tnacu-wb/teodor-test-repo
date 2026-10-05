package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_10;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_35;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RoomRateOhipMapperImpl.class,
    ScheduleListOhipMapperImpl.class,
    ReservationOhipMapperImpl.class,
    ReservationOhipProperties.class, ReservationRequestOhipMapperImpl.class,
    ReservationRequestOhipMapperTest.TestConfig.class})
class ReservationRequestOhipMapperTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    @SuppressWarnings("unchecked")
    UnleashWrapper<FeatureFlag> unleashWrapper() {
      var wrapper = mock(UnleashWrapper.class);
      var featureFlag = new FeatureFlag();
      featureFlag.setSetDefaultPaymentMethodDs(new FeatureFlag.Feature());
      when(wrapper.featureFlag()).thenReturn(featureFlag);
      when(wrapper.isEnabled(any())).thenReturn(false);
      return wrapper;
    }
  }

  @Autowired
  ReservationRequestOhipMapper reservationRequestOhipMapper;

  private static String COMPANY_ID = "COMP_7846063e-acd1-4931-8c07-1c33ddc2a42f";
  private static String EMPLOYEE_ID = "EMPL_5404e3cf-0c57-4987-84f6-3f70a4079bdc";
  private static String CUSTOMER_ID = "CUST_ea41b087-f7f5-496f-ab1e-eebcd7186582";

  @Test
  void reservationRequestToCreateReservation__ShouldReturnOK() {
    //Arrange
    ReservationRequest reservationRequest = ReservationTestUtils.mockReservationRequest();

    //Act
    var createReservation = reservationRequestOhipMapper.toCreateReservationModel(reservationRequest.getReservations()
        .get(0));

    //Assert
    assertEquals("LONEUS",
        createReservation.getReservations().getReservation().get(0).getHotelId());
    assertEquals("2015-10-20",
        createReservation.getReservations().getReservation().get(0).getRoomStay().getArrivalDate()
            .toString());
    assertEquals("2015-10-22",
        createReservation.getReservations().getReservation().get(0).getRoomStay().getDepartureDate()
            .toString());
    assertEquals(2,
        createReservation.getReservations().getReservation().get(0).getRoomStay().getGuestCounts()
            .getAdults());
    assertEquals(0,
        createReservation.getReservations().getReservation().get(0).getRoomStay().getGuestCounts()
            .getChildren());
    assertEquals("O9ONHOLD",
        createReservation.getReservations().getReservation().get(0).getRoomStay().getGuarantee()
            .getGuaranteeCode());
    assertEquals("UDFC15",
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(0)
            .getName());
    assertEquals("ABC",
        createReservation.getReservations().getReservation().get (0).getUserDefinedFields().getCharacterUDFs().get(0)
            .getValue());
    assertEquals("UDFC13",
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(1)
            .getName());
    assertEquals("testDistUsername",
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(1)
            .getValue());
    assertEquals("UDFC09",
            createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(2)
                    .getName());
    assertEquals("ANON",
            createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(2)
                    .getValue());
    assertEquals("ABCD1234",
        createReservation.getReservations().getReservation().get(0).getExternalReferences().get(1).getId());
    assertEquals("TestTitle",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0)
            .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getNameTitle());
    assertEquals("TestFirstName",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0)
            .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getGivenName());
    assertEquals("TestLastName",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0)
            .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getSurname());
    assertEquals("TestEmailAddress",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0)
            .getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail().getEmailAddress());
  }

  @Test
  void distributionRequestToCreateReservation__ShouldReturnOK() {
    //Arrange
    ReservationRequest reservationRequest = ReservationTestUtils.mockReservationRequest();
    var bookingChannel = BookingChannel.builder()
        .channel("DISTR")
        .subchannel("AMADEUS")
        .build();
    reservationRequest.setBookingChannel(bookingChannel);
    reservationRequest.getReservations().get(0).setSourceCode("38");

    //Act
    var createReservation = reservationRequestOhipMapper.toCreateReservationModel(reservationRequest.getReservations()
        .get(0));

    //Assert
    assertEquals("ABCD1234",
        createReservation.getReservations().getReservation().get(0).getExternalReferences().get(1).getId());
    assertEquals("Amadeus_GDS",
        createReservation.getReservations().getReservation().get(0).getExternalReferences().get(1).getIdContext());
    assertEquals("NONSMOKING",
        createReservation.getReservations().getReservation().get(0).getComments().get(0).getComment().getText()
            .getValue());
  }

  @Test
  void reservationRequestToCreateReservationForBB__ShouldReturnAccountFields() {
    //Arrange
    ReservationRequest reservationRequest = mockBBReservationRequest();

    //Act
    var createReservation = reservationRequestOhipMapper.toCreateReservationModel(reservationRequest.getReservations()
        .get(0));

    //Assert
    assertEquals(UDFC_10,
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(2)
            .getName());
    assertEquals(COMPANY_ID,
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(2)
            .getValue());
    assertEquals(UDFC_35,
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(3)
            .getName());
    assertEquals(EMPLOYEE_ID,
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(3)
            .getValue());
    assertEquals("E",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0).getProfileInfo()
            .getProfile().getCustomer().getLanguage());
    assertEquals("First Line",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0).getProfileInfo()
            .getProfile().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(0));
    assertEquals("Big Smoke",
        createReservation.getReservations().getReservation().get(0).getReservationGuests().get(0).getProfileInfo()
            .getProfile().getAddresses().getAddressInfo().get(0).getAddress().getCityName());
  }

  @Test
  void reservationRequestToCreateReservationForPI__ShouldReturnAccountField() {
    //Arrange
    ReservationRequest reservationRequest = mockPIReservationRequest();

    //Act
    var createReservation = reservationRequestOhipMapper.toCreateReservationModel(reservationRequest.getReservations()
        .get(0));

    //Assert
    assertEquals(UDFC_35,
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(2)
            .getName());
    assertEquals(CUSTOMER_ID,
        createReservation.getReservations().getReservation().get(0).getUserDefinedFields().getCharacterUDFs().get(2)
            .getValue());
  }

  private static ReservationRequest mockBBReservationRequest() {
    var reservation = ReservationTestUtils.mockReservation();
    reservation.setCompanyAccountId(COMPANY_ID);
    reservation.setUserAccountId(EMPLOYEE_ID);
    return new ReservationRequest(Collections.singletonList(reservation),
        BookingChannel.builder().channel("BB").subchannel("WEB").build(), false);
  }

  private static ReservationRequest mockPIReservationRequest() {
    var reservation = ReservationTestUtils.mockReservation();
    reservation.setUserAccountId(CUSTOMER_ID);
    return new ReservationRequest(Collections.singletonList(reservation),
        BookingChannel.builder().channel("PI").subchannel("WEB").build(), false);
  }
}