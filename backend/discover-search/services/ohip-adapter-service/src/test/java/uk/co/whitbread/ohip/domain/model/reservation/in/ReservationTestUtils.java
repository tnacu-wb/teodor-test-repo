/*
 * Copyright (c) 2023 Whitbread plc. All rights reserved.
 */
package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import uk.co.whitbread.ohip.domain.model.checkin.out.Address;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;

public class ReservationTestUtils {

  public static PaymentCard mockPaymentCard() {
    return PaymentCard.builder()
        .cardType("VISA")
        .token("12344455565234")
        .cardHolderName("JOHN DOE")
        .expirationDate("2023-12-03")
        .build();
  }

  public static BookerAddress mockBookerAddress() {
    return BookerAddress.builder()
        .addressType("BUSINESS")
        .postalCode("123456")
        .addressLine1("SOME STREET")
        .countryCode("UK")
        .cityName("SOME CITY")
        .companyName("SOME COMPANY")
        .build();
  }

  public static BookerDetails mockBookerDetails() {
    return BookerDetails.builder()
        .title("Mr.")
        .firstName("John")
        .lastName("Doe")
        .emailAddress("john_doe@whitbread.com")
        .address(ReservationTestUtils.mockBookerAddress())
        .acceptFutureMailing(Boolean.FALSE)
        .build();
  }

  public static StayingGuestDetails mockGuestDetails() {
    return StayingGuestDetails.builder()
        .title("Mr.")
        .firstName("John")
        .lastName("Doe")
        .employeeAccountId("TDE13849")
        .emailAddress("jane_doe@whitbread.com")
        .address(mockStayingGuestAddress())
        .additionalDetails(mockStayingGuestAdditionalDetails())
        .profileId("1234")
        .build();
  }

  public static StayingGuestAdditionalDetails mockStayingGuestAdditionalDetails() {
    return StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.of(1996, 7, 13))
        .passportNumber("ABCD1234")
        .nationality("UK")
        .build();
  }

  public static StayingGuest mockReservationGuests() {
    return StayingGuest.builder()
        .reservationId("12345")
        .sameAsBooker(false)
        .stayingGuestDetails(ReservationTestUtils.mockGuestDetails())
        .build();
  }

  public static StayingGuest mockReservationGuestsSameAsBooker() {
    return StayingGuest.builder()
        .reservationId("12345")
        .sameAsBooker(true)
        .stayingGuestDetails(ReservationTestUtils.mockGuestDetails())
        .build();
  }

  public static RoomRateReservation mockRoomRateReservation() {
    return RoomRateReservation.builder()
        .start("2015-10-20")
        .end("2015-10-22")
        .roomType("DB")
        .ratePlanCode("DELUXE")
        .cellCode("ABC")
        .build();
  }

  public static RoomRateReservation mockRoomRateReservationWithPredefinedRates() {
    var ratePrice1 = RatePrice.builder()
        .amount(new BigDecimal(65))
        .priceStartDate(LocalDate.now().plusDays(1))
        .priceEndDate(LocalDate.now().plusDays(2))
        .build();

    var ratePrice2 = RatePrice.builder()
        .amount(new BigDecimal(65))
        .priceStartDate(LocalDate.now().plusDays(2))
        .priceEndDate(LocalDate.now().plusDays(3))
        .build();


    return RoomRateReservation.builder()
        .start(LocalDate.now().plusDays(1).toString())
        .end(LocalDate.now().plusDays(3).toString())
        .roomType("DOUBLE")
        .ratePlanCode("FLEXRATE")
        .cellCode("ABC")
        .ratePrices(List.of(ratePrice1, ratePrice2))
        .build();
  }

  public static Reservation mockReservation() {
    return Reservation.builder()
        .hotelId("LONEUS")
        .arrival("2015-10-20")
        .departure("2015-10-22")
        .externalReferenceId("12345")
        .roomRates(mockRoomRateReservation())
        .adults(2)
        .children(0)
        .sourceCode("44")
        .gdsReferenceNumber("ABCD1234")
        .bookingNotes("NONSMOKING")
        .distributionUsername("testDistUsername")
        .distributionIATANumber("00000000123456")
        .bookingType("ANON")
        .leadGuest(mockLeadGuest())
        .build();
  }

  private static LeadGuest mockLeadGuest() {
    return LeadGuest.builder()
        .title("TestTitle")
        .firstName("TestFirstName")
        .lastName("TestLastName")
        .emailAddress("TestEmailAddress")
        .language("E")
        .address(mockGuestAddress())
        .build();
  }

  private static Address mockGuestAddress() {
    return Address.builder()
        .addressLine(List.of("First Line", "Second Line"))
        .postalCode("PO5 TA1")
        .cityName("Big Smoke")
        .country(new Country("GB"))
        .build();
  }

  public static ReservationRequest mockReservationRequest() {
    return new ReservationRequest(Collections.singletonList(mockReservation()),
                  BookingChannel.builder().channel("PI").subchannel("WEB").build(),
        true);
  }

  public static ReservationGuestRequest mockReservationGuestRequest() {
    return ReservationGuestRequest.builder()
        .booker(mockBookerDetails())
        .stayingGuests(Collections.singletonList(mockReservationGuests()))
        .hotelId("LONEUS")
        .reasonForStay("LEI")
        .sendEmailConfirmation(Boolean.TRUE)
        .sendEmailInvoice(Boolean.FALSE)
        .build();
  }

  public static UpdateReservationOverrideReasonsRequest mockUpdateReservationOverrideReasonsRequest() {
    return UpdateReservationOverrideReasonsRequest.builder()
        .reservationIds(Set.of("123456"))
        .hotelId("BERALX")
        .reasonCode("ILL")
        .reasonName("Medical Appointment")
        .callerName("John Doe")
        .managerName("Miriam More")
        .build();
  }

  public static StayingGuestAddress mockStayingGuestAddress() {
    return StayingGuestAddress.builder()
        .addressType("BUSINESS")
        .postalCode("123456")
        .addressLine1("SOME STREET")
        .countryCode("UK")
        .build();
  }

  public static ConfirmAmendOnReservationsRequest mockConfirmAmendOnReservationsRequest() {
    return ConfirmAmendOnReservationsRequest.builder()
        .hotelId("LONEUS")
        .tempReservations(List.of("123456"))
        .originalReservations(List.of("6789"))
        .bookingChannel(BookingChannel.builder().channel("PI").subchannel("WEB").build())
        .linkAmendReservations(new HashMap<>(
            Map.of("6789","value1")))
        .markAsPayOnArrival(false)
        .build();
  }

  public static LinkReservationToLeisureCustomerRequest mockLinkReservationToLeisureCustomerRequest() {
    return LinkReservationToLeisureCustomerRequest.builder()
        .reservationIds(Set.of("123456"))
        .hotelId("BERALX")
        .customerAccountId("CUST_123456")
        .build();
  }
}
