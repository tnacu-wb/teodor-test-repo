package uk.co.whitbread.ohip.domain.logic.utils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.logic.utils.GuestInfoUtils;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = GuestInfoUtils.class)
class GuestInfoUtilsTest {

  @Test
  void getBookerAddress_fromReservationBooker__Success() {
    // Arrange
    var inputReservationBooker = mockReservationBooker();

    // Act
    ProfileInfo profile = GuestInfoUtils.toProfileType(new ReservationGuest(), inputReservationBooker, "en");

    // Assert
    assertThat(profile, notNullValue());
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getType(),
        is("BILLING"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(0),
        is("First Flat"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(1),
        is("Second Ave"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getCityName(),
        is("Oxfork"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode(),
        is("OX3 0AB"));

  }

  @Test
  void getProfileType_fromReservationGuest__Success(){

    //Arrange
    var inputRservationGuest = mockReservationGuest();

    //Act
    ProfileInfo profile = GuestInfoUtils.toProfileType(inputRservationGuest, new ReservationBooker(), "de");

    //Assert
    assertThat(profile, notNullValue());
    assertThat(profile.getProfile().getEmails().getEmailInfo().get(0).getEmail().getType(), is("EMAIL"));
    assertThat(profile.getProfile().getTelephones().getTelephoneInfo().get(0).getType(), is("HOME"));
    assertThat(profile.getProfile().getCustomer().getPersonName().size(), is(1));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getType(),
        is("HOME"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(0),
        is("First Line"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine().get(1),
        is("Second Line"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getCityName(),
        is("Big Smoke"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode(),
        is("BI6 8OI"));

  }

  @Test
  void getProfileType_fromReservationGuestNoAddressLines__Success(){

    //Arrange
    var inputRservationGuest = mockReservationGuestNoAddressLines();

    //Act
    ProfileInfo profile = GuestInfoUtils.toProfileType(inputRservationGuest, new ReservationBooker(), "de");

    //Assert
    assertThat(profile, notNullValue());
    assertThat(profile.getProfile().getEmails().getEmailInfo().get(0).getEmail().getType(), is("EMAIL"));
    assertThat(profile.getProfile().getTelephones().getTelephoneInfo().get(0).getType(), is("HOME"));
    assertThat(profile.getProfile().getCustomer().getPersonName().size(), is(1));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getType(),
        is("HOME"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress(),
        is(notNullValue()));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getCityName(),
        is("Big Smoke"));
    assertThat(profile.getProfile().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode(),
        is("BI6 8OI"));

  }

  @Test
  void getLanguageCode__Success() {
    testLanguageCode("en", "E");
    testLanguageCode("e", "E");
    testLanguageCode("de", "DE");
    testLanguageCode("d", "DE");
    testLanguageCode(null, "E");
    testLanguageCode("es", "E");
  }

  void testLanguageCode(String receivedLanguage, String expectedLanguage) {
    // Arrange
    var inputReservationBooker = mockReservationBooker();

    // Act
    ProfileInfo profile = GuestInfoUtils.toProfileType(new ReservationGuest(), inputReservationBooker, receivedLanguage);

    //Assert
    assertThat(profile, notNullValue());
    assertThat(profile.getProfile().getCustomer().getPersonName().size(), is(1));
    assertThat(profile.getProfile().getCustomer().getPersonName().get(0).getLanguage(), is(expectedLanguage));
  }

  private ReservationBooker mockReservationBooker() {
    return ReservationBooker.builder()
        .profileId("profile-id")
        .address(mockBookerAddress())
        .build();
  }

  private ReservationBookerAddress mockBookerAddress() {
    return ReservationBookerAddress.builder()
        .addressType("BILLING")
        .addressLine1("First Flat")
        .addressLine2("Second Ave")
        .cityName("Oxfork")
        .postalCode("OX3 0AB")
        .countryCode("GB")
        .build();
  }

  private ReservationGuest mockReservationGuest() {
    return ReservationGuest.builder()
        .guestRestricted(false)
        .givenName("Testerson")
        .surname("Tester")
        .fullName("Tester Testerson")
        .nameTitle("Mr")
        .email("testerson@tester.com")
        .type("Primary")
        .phoneNumber("12345678")
        .birthDate(LocalDate.parse("1900-04-24"))
        .address(mockGuestAddress())
        .build();
  }

  private ReservationGuest mockReservationGuestNoAddressLines() {
    return ReservationGuest.builder()
        .guestRestricted(false)
        .givenName("Testerson")
        .surname("Tester")
        .fullName("Tester Testerson")
        .nameTitle("Mr")
        .email("testerson@tester.com")
        .type("Primary")
        .phoneNumber("12345678")
        .birthDate(LocalDate.parse("1900-04-24"))
        .address(mockGuestAddressNoAddressLines())
        .build();
  }

  private GuestAddress mockGuestAddress() {
    return GuestAddress.builder()
        .addressType("HOME")
        .addressLine1("First Line")
        .addressLine2("Second Line")
        .cityName("Big Smoke")
        .postalCode("BI6 8OI")
        .countryCode("GB")
        .build();
  }

  private GuestAddress mockGuestAddressNoAddressLines() {
    return GuestAddress.builder()
        .addressType("HOME")
        .cityName("Big Smoke")
        .postalCode("BI6 8OI")
        .countryCode("GB")
        .build();
  }
}
