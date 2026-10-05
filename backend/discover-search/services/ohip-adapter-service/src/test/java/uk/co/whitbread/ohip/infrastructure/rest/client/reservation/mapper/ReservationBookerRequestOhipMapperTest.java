package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReservationBookerRequestOhipMapperImpl.class)
class ReservationBookerRequestOhipMapperTest {

  @Autowired
  ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper;

  @Test
  void createRequestToProfile__ShouldReturnOK() {
    //Act
    var profile = reservationBookerRequestOhipMapper.toDto(createBookerDetails());

    //Assert
    assertEquals(false, profile.getProfileDetails().getPrivacyInfo().getOptInEmail());
    assertEquals("Mr", profile.getProfileDetails().getCustomer().getPersonName().get(0).getNameTitle());
    assertEquals("John", profile.getProfileDetails().getCustomer().getPersonName().get(0).getGivenName());
    assertEquals("Doe", profile.getProfileDetails().getCustomer().getPersonName().get(0).getSurname());
    assertEquals("HOME", profile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress()
        .getType());
    assertEquals("WC2N 5DU",
        profile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode());
    assertEquals(List.of("4 Brocky Avenue", StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
        profile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine());
    assertEquals("UK",
        profile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress().getCountry().getValue());

  }

  @Test
  void createRequestToProfile_additionalInformation__ShouldReturnOK() {
    StayingGuest guest = StayingGuest.builder()
        .reservationId("123456")
        .stayingGuestDetails(createGuestDetails())
        .build();

    var profile = reservationBookerRequestOhipMapper.toDto(createBookerDetails(), Optional.of(guest));

    assertNotNull(profile);
    assertEquals(LocalDate.of(1990, 1, 1), profile.getProfileDetails().getCustomer().getBirthDate());
    assertEquals("British", profile.getProfileDetails().getCustomer().getNationality());
    assertEquals("123456789", profile.getProfileDetails().getCustomer().getIdentifications()
        .getIdentificationInfo().get(0).getIdentification().getIdNumber());
    assertEquals("PASSPORT", profile.getProfileDetails().getCustomer().getIdentifications()
        .getIdentificationInfo().get(0).getIdentification().getIdType());
  }

  private BookerDetails createBookerDetails() {
    return BookerDetails.builder()
        .title("Mr")
        .firstName("John")
        .lastName("Doe")
        .address(createBookerAddress())
        .acceptFutureMailing(Boolean.FALSE)
        .build();
  }

  private StayingGuestDetails createGuestDetails() {
    return StayingGuestDetails.builder()
        .additionalDetails(StayingGuestAdditionalDetails.builder()
            .dob(LocalDate.of(1990, 1, 1))
            .nationality("British")
            .passportNumber("123456789")
            .build())
        .build();
  }

  private BookerAddress createBookerAddress() {
    return BookerAddress.builder()
        .addressType("HOME")
        .postalCode("WC2N 5DU")
        .addressLine1("4 Brocky Avenue")
        .countryCode("UK")
        .build();
  }

  @Test
  void shouldNotSetPassportWhenMaskedWith4Xs() {
    StayingGuest guest = StayingGuest.builder()
        .reservationId("123456")
        .stayingGuestDetails(StayingGuestDetails.builder()
            .additionalDetails(StayingGuestAdditionalDetails.builder()
                .dob(LocalDate.of(1990, 1, 1))
                .nationality("British")
                .passportNumber("XXXX32")
                .build())
            .build())
        .build();

    var profile = reservationBookerRequestOhipMapper.toDto(createBookerDetails(), Optional.of(guest));

    assertNotNull(profile);
    assertNull(profile.getProfileDetails().getCustomer().getIdentifications(),
        "Masked passport (4 X's) should not be sent to OPERA");
  }

  @Test
  void shouldNotSetPassportWhenMaskedWith5OrMoreXs() {
    StayingGuest guest = StayingGuest.builder()
        .reservationId("123456")
        .stayingGuestDetails(StayingGuestDetails.builder()
            .additionalDetails(StayingGuestAdditionalDetails.builder()
                .dob(LocalDate.of(1990, 1, 1))
                .nationality("British")
                .passportNumber("XXXXX23")
                .build())
            .build())
        .build();

    var profile = reservationBookerRequestOhipMapper.toDto(createBookerDetails(), Optional.of(guest));

    assertNotNull(profile);
    assertNull(profile.getProfileDetails().getCustomer().getIdentifications(),
        "Masked passport (5+ X's) should not be sent to OPERA");
  }
}
