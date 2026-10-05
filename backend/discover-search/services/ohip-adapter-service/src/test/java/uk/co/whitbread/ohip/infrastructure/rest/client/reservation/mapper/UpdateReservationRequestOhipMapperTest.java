package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AmountType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CustomerType;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameTypeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfo;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeAddresses;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeEmails;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeTelephones;
import uk.co.whitbread.ohip.domain.model.reservation.in.RateType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuests;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomOccupancy;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRate;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.in.TelephoneInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.TelephoneType;
import uk.co.whitbread.ohip.domain.model.reservation.in.TotalType;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UpdateReservationRequestOhipMapperImpl.class)
class UpdateReservationRequestOhipMapperTest {

  @Autowired
  private UpdateReservationRequestOhipMapper mapper;

  @Test
  void toDto_WhenUpdateReservationRequestHasAllFieldsSet_ThenTheFieldsAreProperlyMapped() {
    var update = UpdateReservationRequest.builder()
        .hotelId("TestHotelId")
        .reservationType(ReservationType.builder().type("DOUBLE").id("232323").build())
        .roomStay(RoomStay.builder()
            .arrivalDate("2024-09-25")
            .departureDate("2024-09-28")
            .roomOccupancy(RoomOccupancy.builder().adultCount(2).childCount(1).build())
            .roomRates(List.of(RoomRate.builder()
                .rates(RateType.builder()
                    .rate(List.of(AmountType.builder()
                        .start("2024-09-25")
                        .end("2024-09-26")
                        .base(TotalType.builder()
                            .amountBeforeTax(BigDecimal.ONE)
                            .currencyCode("EUR")
                            .build())
                        .build()))
                    .build())
                .roomType("SINGLE")
                .ratePlanCode("FLEX")
                .roomOccupancy(RoomOccupancy.builder().adultCount(2).childCount(1).build())
                .startDate("2024-09-25")
                .endDate("2024-09-28")
                .fixedRate(false)
                .build()))
            .build())
        .reservationGuests(List.of(ReservationGuests.builder().profileInfo(ProfileInfo.builder().profile(
                ProfileType
                    .builder()
                    .customer(CustomerType.builder()
                        .birthDate(LocalDate.of(1950, Month.AUGUST, 11))
                        .personName(List.of(PersonNameType.builder()
                                .givenName("John")
                                .surname("Coltrane")
                                .nameTitle("Mr.")
                                .nameType(PersonNameTypeType.PRIMARY)
                                .language("en")
                                .email("email@dot.com")
                            .build()))
                        .build())
                    .emails(ProfileTypeEmails.builder()
                        .emailInfo(List.of(EmailInfoType.builder()
                            .email(EmailType.builder()
                                .emailAddress("email2@dot.com")
                                .type("E")
                                .build())
                            .build()))
                        .build())
                    .telephones(ProfileTypeTelephones.builder()
                        .telephoneInfo(List.of(TelephoneInfoType.builder()
                                .telephone(TelephoneType.builder()
                                    .phoneNumber("111-222")
                                    .primaryInd(true)
                                    .build())
                                .type("MOBILE")
                                .build())).build())

                    .addresses(ProfileTypeAddresses.builder()
                        .addressInfo(List.of(AddressInfoType.builder()
                                .address(AddressType.builder()
                                    .validated(true)
                                    .addressLine(List.of("address line 1", "address line 2"))
                                    .cityName("Birmingham")
                                    .postalCode("postal-code")
                                    .state("STATE")
                                    .country(new Country("UK"))
                                    .language("en")
                                    .type("Primary")
                                    .primaryInd(true)
                                .build())
                            .build()))
                        .build())
                    .build()).build()).build()))
        .sendEmailConfirmation(true)
        .sendEmailInvoice(true)
        .specialRequests(List.of("sp1", "sp2"))
        .build();

    var result = mapper.toDto(update).getReservations().get(0);

    assertEquals("TestHotelId", result.getHotelId());
    assertEquals("232323", result.getReservationIdList().get(0).getId());
    assertEquals("DOUBLE", result.getReservationIdList().get(0).getType());
    assertEquals(LocalDate.parse("2024-09-25"), result.getRoomStay().getArrivalDate());
    assertEquals(LocalDate.parse("2024-09-28"), result.getRoomStay().getDepartureDate());
    assertEquals(2, result.getRoomStay().getGuestCounts().getAdults());
    assertEquals(1, result.getRoomStay().getGuestCounts().getChildren());
    assertEquals(LocalDate.parse("2024-09-25"), result.getRoomStay().getRoomRates().get(0).getRates().getRate().get(0).getStart());
    assertEquals(LocalDate.parse("2024-09-26"), result.getRoomStay().getRoomRates().get(0).getRates().getRate().get(0).getEnd());
    assertEquals(BigDecimal.ONE, result.getRoomStay().getRoomRates().get(0).getRates().getRate().get(0).getBase().getAmountBeforeTax());
    assertEquals("EUR", result.getRoomStay().getRoomRates().get(0).getRates().getRate().get(0).getBase().getCurrencyCode());
    assertEquals("SINGLE", result.getRoomStay().getRoomRates().get(0).getRoomType());
    assertEquals("FLEX", result.getRoomStay().getRoomRates().get(0).getRatePlanCode());
    assertEquals(LocalDate.parse("2024-09-25"), result.getRoomStay().getRoomRates().get(0).getStart());
    assertEquals(LocalDate.parse("2024-09-28"), result.getRoomStay().getRoomRates().get(0).getEnd());
    assertEquals(false, result.getRoomStay().getRoomRates().get(0).getFixedRate());
    assertEquals(LocalDate.parse("1950-08-11"), result.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer().getBirthDate());
    assertEquals("John", result.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getGivenName());
    assertEquals("Coltrane", result.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getSurname());
    assertEquals("Primary", result.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getNameType().getValue());
    assertEquals("en", result.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getLanguage());
    assertEquals("email2@dot.com", result.getReservationGuests().get(0).getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail().getEmailAddress());
    assertEquals("E", result.getReservationGuests().get(0).getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail().getType());
    assertEquals("111-222", result.getReservationGuests().get(0).getProfileInfo().getProfile().getTelephones().getTelephoneInfo().get(0).getTelephone().getPhoneNumber());
    assertEquals(true, result.getReservationGuests().get(0).getProfileInfo().getProfile().getTelephones().getTelephoneInfo().get(0).getTelephone().getPrimaryInd());
    assertEquals("MOBILE", result.getReservationGuests().get(0).getProfileInfo().getProfile().getTelephones().getTelephoneInfo().get(0).getType());
    assertEquals(List.of("address line 1", "address line 2"), result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getAddressLine());
    assertEquals("Birmingham", result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getCityName());
    assertEquals("postal-code", result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getPostalCode());
    assertEquals("STATE", result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getState());
    assertEquals("UK", result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getCountry().getValue());
    assertEquals("en", result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getLanguage());
    assertEquals("Primary", result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getType());
    assertEquals(true, result.getReservationGuests().get(0).getProfileInfo().getProfile().getAddresses().getAddressInfo().get(0).getAddress().getPrimaryInd());
    assertEquals("sp1", result.getPreferenceCollection().get(0).getPreference().get(0).getPreferenceValue());
    assertEquals("sp2", result.getPreferenceCollection().get(0).getPreference().get(1).getPreferenceValue());
    assertEquals("SPECIALS", result.getPreferenceCollection().get(0).getPreferenceType());
    assertEquals("Specials", result.getPreferenceCollection().get(0).getPreferenceTypeDescription());
  }
}