package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.DISTR_BOOKING_CHANNEL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.model.in.AmendStayDatesRequest;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CustomerType;
import uk.co.whitbread.reservation.domain.model.in.EmailInfoType;
import uk.co.whitbread.reservation.domain.model.in.EmailType;
import uk.co.whitbread.reservation.domain.model.in.LeadGuest;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.PersonNameType;
import uk.co.whitbread.reservation.domain.model.in.ProfileInfo;
import uk.co.whitbread.reservation.domain.model.in.ProfileType;
import uk.co.whitbread.reservation.domain.model.in.ProfileTypeEmails;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationGuests;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.RoomRate;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.SpecialRequests;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomOccupancyRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomRateRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateRoomStayRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdGuestsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;

@ExtendWith(MockitoExtension.class)
class AmendLogicDistributionInPortImplTest {

  private static final String HOTEL_ID = "TESTHOTEL";
  private static final String TEMP_RESERVATION_ID_1 = "1950012";
  private static final String TEMP_RESERVATION_ID_2 = "1950015";
  private static final String PACKAGE_CODE_BFADBF = "BFADBF";
  private static final String PACKAGE_CODE_BFADCT = "BFADCT";
  private static final String PACKAGE_CODE_BFCHDF = "BFCHDF";
  private static final String TEMP_BASKET_REF = "AWM-a3a4ea56-b513-40e5-a0e3-926a83bdceb8";
  private static final String COMPLETED_STATUS = "COMPLETED";
  private static final String MANOLD = "MANOLD";
  private static final String LONEUS = "LONEUS";
  private static final String JOHN_DOE_EMAIL = "john.doe@example.com";
  private static final String RESERVATION_ID_1 = "1880126";
  private static final String RESERVATION_ID_2 = "1880045";
  private static final String START_DATE = "2023-06-07";
  private static final String END_DATE = "2023-06-08";
  private static final String MARY_JANE_EMAIL = "mary.jane@example.com";
  private static final String MARY = "Mary";
  private static final String JANE = "Jane";
  private static final String JOHN = "John";
  private static final String DOE = "Doe";
  private static final String MR = "MR";
  private static final String MS = "Ms";
  private static final String SPECIAL_REQUESTS = "SING";
  public static final String PACKAGE_GROUP = "MDP";

  @Mock
  private BasketOutPort basketOutPort;

  @Mock
  private AmendLogicInPort amendLogicInPort;

  @InjectMocks
  private AmendDistributionLogicInPortImpl amendDistributionLogicInPortImpl;


  @Test
  void validateBasketByReference_wrongReference_shouldThrowException() {
    // Arrange
    var basket = BasketResponse.builder().status("test").build();
    var mess = String.format("Inconsistent basket status: %s", basket.getStatus());
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);
    // Assert
    var ex = assertThrows(GenericBadRequestException.class, () -> {
      amendDistributionLogicInPortImpl.validateBasketByReference("wrongReference");
    });
    assertEquals(ErrorCode.DIGITAL_INCONSISTENT_BASKET_EXCEPTION.getCode(), ex.getErrorCode());
    assertEquals(mess, ex.getMessage());
  }

  @Test
  void validateBasketByReference_wrongStatus_shouldThrowException() {
    // Arrange
    when(basketOutPort.getBasketById(anyString())).thenThrow(GenericBadRequestException.class);

    // Assert
    assertThrows(GenericBadRequestException.class, () -> {
      amendDistributionLogicInPortImpl.validateBasketByReference(TEMP_BASKET_REF);
    });
  }

  @Test
  void validateBasketByReference_ok() {
    // Arrange
    BasketResponse basket = mockCreateBasketResponse();
    basket.setStatus(COMPLETED_STATUS);
    when(basketOutPort.getBasketById(anyString())).thenReturn(basket);

    // Act
    BasketResponse basketResponse = amendDistributionLogicInPortImpl.validateBasketByReference(
        TEMP_BASKET_REF);

    // Assert
    assertNotNull(basketResponse);
    assertEquals(basketResponse.getHotelId(), basket.getHotelId());
    assertEquals(basketResponse.getBookingReference(), basket.getBookingReference());
  }

  @Test
  void validateDistributionReservations_differentDates_throwsException() {
    // Arrange
    ReservationRequest reservationRequest = mockReservationRequest();

    // Assert
    assertThrows(GenericBadRequestException.class, () -> {
      amendDistributionLogicInPortImpl.validateDistributionReservations(reservationRequest, LONEUS);
    });
  }

  @Test
  void validateDistributionReservations_differentHotel_throwsException() {
    // Arrange
    ReservationRequest reservationRequest = mockReservationRequest();

    // Assert
    assertThrows(GenericBadRequestException.class, () -> {
      amendDistributionLogicInPortImpl.validateDistributionReservations(reservationRequest, MANOLD);
    });
  }

  @Test
  void validateDistributionReservations_ok() {
    // Arrange
    ReservationRequest reservationRequest = mockReservationRequest();
    reservationRequest.getReservations().get(1).getRoomRates().setStartDate(START_DATE);
    reservationRequest.getReservations().get(1).getRoomRates().setEndDate(END_DATE);

    // Act
    amendDistributionLogicInPortImpl.validateDistributionReservations(reservationRequest, MANOLD);
  }

  @Test
  void validateDistributionIataNumber_ok() {
    // Arrange
    ReservationRequest reservationRequest = mockReservationRequest();
    reservationRequest.setDistributionIATANumber("12345678");

    //Act
    amendDistributionLogicInPortImpl.validateDistributionIataNumber(reservationRequest);
  }

  @Test
  void validateDistributionIataNumber_throwsException() {
    // Arrange
    ReservationRequest reservationRequest = mockReservationRequest();
    reservationRequest.setDistributionIATANumber("123456789");

    //Act
    assertThrows(GenericBadRequestException.class, () ->
            amendDistributionLogicInPortImpl.validateDistributionIataNumber(reservationRequest));
  }

  @Test
  void extractNewRooms_ok() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation1(), mockReservation2());

    // Act
    List<Reservation> rooms =
        amendDistributionLogicInPortImpl.extractNewRooms(reservations);

    // Assert
    assertEquals(1, rooms.size());
    assertEquals(START_DATE, rooms.get(0).getRoomRates().getStartDate());
    assertEquals(END_DATE, rooms.get(0).getRoomRates().getEndDate());
  }

  @Test
  void extractNewRooms_returnsEmptyList() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation2(), mockReservation2());

    // Act
    List<Reservation> rooms =
        amendDistributionLogicInPortImpl.extractNewRooms(reservations);

    // Assert
    assertEquals(0, rooms.size());
  }

  @Test
  void extractSpecialRequests_ok() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation1(), mockReservation2());

    // Act
    List<SpecialRequests> specialRequests =
        amendDistributionLogicInPortImpl.extractSpecialRequests(reservations, List.of("1736673"),
            reservations.get(0).getHotelId(), null);

    // Assert
    assertEquals(1, specialRequests.size());
    assertEquals(List.of(SPECIAL_REQUESTS), specialRequests.get(0).getSpecialRequests());
  }

  @Test
  void extractSpecialRequests_returnsEmptyList() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation1(), mockReservation1());

    // Act
    List<SpecialRequests> specialRequests =
        amendDistributionLogicInPortImpl.extractSpecialRequests(reservations, List.of("1736673"),
            reservations.get(0).getHotelId(), null);

    // Assert
    assertEquals(0, specialRequests.size());
  }

  @Test
  void extractSpecialRequestsWithBookingNotes_ok() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation1(), mockReservation2());

    // Act
    List<SpecialRequests> specialRequests =
        amendDistributionLogicInPortImpl.extractSpecialRequests(reservations, List.of("1736673"),
            reservations.get(0).getHotelId(), List.of("BookingNote1"));

    // Assert
    assertEquals(1, specialRequests.size());
    assertEquals(List.of(SPECIAL_REQUESTS), specialRequests.get(0).getSpecialRequests());
    assertEquals(List.of("BookingNote1"), specialRequests.get(0).getBookingNotes());
  }

  @Test
  void extractSpecialRequestsWithBookingNotes_returnsEmptyList() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation1(), mockReservation1());

    // Act
    List<SpecialRequests> specialRequests =
        amendDistributionLogicInPortImpl.extractSpecialRequests(reservations, List.of("1736673"),
            reservations.get(0).getHotelId(), null);

    // Assert
    assertEquals(0, specialRequests.size());
  }

  @Test
  void extractSpecialRequestsOnlyBookingNotes_ok() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation2(), mockReservation1());
    reservations.get(0).getRoomRates().setSpecialRequests(null);

    // Act
    List<SpecialRequests> specialRequests =
        amendDistributionLogicInPortImpl.extractSpecialRequests(reservations, List.of("1736673"),
            reservations.get(0).getHotelId(), List.of("BookingNote1"));
    // Assert
    assertEquals(1, specialRequests.size());
    assertTrue(specialRequests.get(0).getSpecialRequests().isEmpty());
    assertEquals(List.of("BookingNote1"), specialRequests.get(0).getBookingNotes());
  }

  @Test
  void extractSpecialRequestsForNewRoomAmendment_ok() {
    // Arrange
    List<Reservation> reservations = List.of(mockReservation1(), mockReservation1());
    reservations.get(0).getRoomRates().setSpecialRequests(null);
    reservations.get(1).getRoomRates().setSpecialRequests(null);

    // Act
    List<SpecialRequests> specialRequests =
        amendDistributionLogicInPortImpl.setSpecialRequestsForNewAmendRooms(
            List.of("1736675", "173668"),
            List.of("BookingNote1"), reservations.get(0).getHotelId());

    // Assert
    assertEquals(2, specialRequests.size());
    assertTrue(specialRequests.get(0).getSpecialRequests().isEmpty());
    assertEquals(List.of("BookingNote1"), specialRequests.get(0).getBookingNotes());
  }

  @Test
  void extractStayDates_ok() {
    // Arrange
    ReservationRequest reservationRequest = mockReservationRequestStayDates();
    // Act
    List<AmendStayDatesRequest> stayDates =
        amendDistributionLogicInPortImpl.extractStayDates(START_DATE, END_DATE,
            TEMP_BASKET_REF, reservationRequest);

    // Assert
    assertEquals(1, stayDates.size());
    assertEquals(reservationRequest.getReservations().get(0).getRoomRates().getStartDate(),
        stayDates.get(0).getNewStartDate());
    assertEquals(reservationRequest.getReservations().get(0).getRoomRates().getEndDate(),
        stayDates.get(0).getNewEndDate());
    assertEquals(reservationRequest.getToken(), stayDates.get(0).getToken());
    assertEquals(reservationRequest.getBookingChannel().getSubchannel(),
        stayDates.get(0).getBookingChannel().getSubchannel());
  }

  @Test
  void extractStayDates_noReservationMatchesCriteria() {
    // Arrange
    Reservation reservation = mockReservation2();
    reservation.setExternalReferenceId(null);
    List<Reservation> reservations = List.of(mockReservation1(), reservation);
    ReservationRequest reservationRequest = ReservationRequest.builder()
        .reservations(reservations)
        .build();

    // Act
    List<AmendStayDatesRequest> stayDates =
        amendDistributionLogicInPortImpl.extractStayDates(START_DATE, END_DATE,
            TEMP_BASKET_REF, reservationRequest);

    // Assert
    assertEquals(0, stayDates.size());
  }

  @Test
  void extractRemovedRoomsIds_ok() {
    // Arrange
    BasketResponse basket = mockCreateBasketResponse();
    Reservation reservation = mockReservation2();
    reservation.setExternalReferenceId(RESERVATION_ID_1);

    when(basketOutPort.getBasketById(TEMP_BASKET_REF)).thenReturn(basket);

    // Act
    List<String> removedRoomsIds =
        amendDistributionLogicInPortImpl.extractRemovedRoomsIds(List.of(reservation),
            TEMP_BASKET_REF);

    // Assert
    assertEquals(1, removedRoomsIds.size());
    assertEquals(basket.getLinkAmendReservations().get(RESERVATION_ID_2), removedRoomsIds.get(0));
  }

  @Test
  void extractRemovedRoomsIds_returnsEmptyList() {
    // Arrange
    BasketResponse basket = mockCreateBasketResponse();
    Reservation firstReservation = mockReservation2();
    firstReservation.setExternalReferenceId(RESERVATION_ID_1);

    Reservation secondReservation = mockReservation1();
    secondReservation.setExternalReferenceId(RESERVATION_ID_2);

    when(basketOutPort.getBasketById(TEMP_BASKET_REF)).thenReturn(basket);

    // Act
    List<String> removedRoomsIds = amendDistributionLogicInPortImpl.extractRemovedRoomsIds(
        List.of(firstReservation, secondReservation), TEMP_BASKET_REF);

    // Assert
    assertEquals(0, removedRoomsIds.size());
  }

  @Test
  void setRoomsSelectionsSingleCall_shouldMapPackagesToCorrespondingReservations() {
    // Arrange
    var updatePackagesRequest = new UpdateReservationPackagesByIdRequest();

    updatePackagesRequest.setRoomsSelections(List.of(mockRoomsSelectionsByReservationId_1(),
            mockRoomsSelectionsByReservationId_2()));

    // Act
    amendDistributionLogicInPortImpl.setRoomsSelections(updatePackagesRequest,
            AmendLogicDistributionInPortImplTest.TEMP_BASKET_REF, mockReservationByBasketRefResponse(),
            new ArrayList<>());

    // Assert
    assertEquals(TEMP_RESERVATION_ID_1,
            updatePackagesRequest.getPreviousRoomsSelections().get(0).getReservationId());
    assertEquals(TEMP_RESERVATION_ID_1,
            updatePackagesRequest.getRoomsSelections().get(0).getReservationId());
    assertEquals(TEMP_RESERVATION_ID_2,
            updatePackagesRequest.getPreviousRoomsSelections().get(1).getReservationId());
    assertEquals(TEMP_RESERVATION_ID_2,
            updatePackagesRequest.getRoomsSelections().get(1).getReservationId());
  }

  @Test
  void extractRemovedRoomsIdsSingleCall() {
    // Arrange
    Reservation reservation = mockReservation2();
    reservation.setExternalReferenceId(RESERVATION_ID_1);

    // Act
    List<String> removedRoomsIds =
            amendDistributionLogicInPortImpl.extractRemovedRoomsIds(List.of(reservation),
                    List.of(RESERVATION_ID_2));

    // Assert
    assertEquals(1, removedRoomsIds.size());
  }

  @Test
  void extractUpdateReservations_success() {
    // Arrange
    BasketResponse basket = mockCreateBasketResponse();
    List<ReservationByIdResponse> reservationByIdResponses = mockReservationByIdResponseList();
    List<UpdateReservationsRequest> updatedReservations = mockUpdateReservationsRequestList();

    when(basketOutPort.getBasketById(TEMP_BASKET_REF)).thenReturn(basket);
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponses.get(0),
        updatedReservations.get(0).getReservations().get(0)))
        .thenReturn(false);
    var bookingChannel = mockBookingChannel();
    updatedReservations.getFirst().setBookingChannel(bookingChannel);

    // Act
    List<UpdateReservationsRequest> updateReservationsRequests =
        amendDistributionLogicInPortImpl.extractUpdatedReservations(reservationByIdResponses,
            updatedReservations, TEMP_BASKET_REF);

    // Assert
    assertEquals(2, updateReservationsRequests.size());
    assertTrue(basket.getLinkAmendReservations().containsValue(
        updateReservationsRequests.get(0).getReservations().get(0).getReservationId()));
    assertEquals(MARY_JANE_EMAIL, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail()
        .getEmailAddress());
    assertNull(updateReservationsRequests.get(0).getDistributionIATANumber());
  }


  @Test
  void extractUpdateReservationsWithIata_success() {
    // Arrange
    BasketResponse basket = mockCreateBasketResponse();
    List<ReservationByIdResponse> reservationByIdResponses = mockReservationByIdResponseList();
    List<UpdateReservationsRequest> updatedReservations = mockUpdateReservationsRequestList();
    updatedReservations.get(0).setDistributionIATANumber("11223344");
    updatedReservations.getFirst().setBookingChannel(mockBookingChannel());

    when(basketOutPort.getBasketById(TEMP_BASKET_REF)).thenReturn(basket);
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponses.get(0),
        updatedReservations.get(0).getReservations().get(0)))
        .thenReturn(false);

    // Act
    List<UpdateReservationsRequest> updateReservationsRequests =
        amendDistributionLogicInPortImpl.extractUpdatedReservations(reservationByIdResponses,
            updatedReservations, TEMP_BASKET_REF);

    // Assert
    assertEquals(2, updateReservationsRequests.size());
    assertTrue(basket.getLinkAmendReservations().containsValue(updateReservationsRequests.get(0).getReservations().get(0).getReservationId()));
    assertEquals(MARY_JANE_EMAIL, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail()
        .getEmailAddress());
    assertNotNull(updateReservationsRequests.get(0).getDistributionIATANumber());
    assertEquals(updateReservationsRequests.get(0).getDistributionIATANumber(), "11223344");
  }

  @Test
  void extractUpdateReservationsWithIata_success_distr() {
    // Arrange
    List<ReservationByIdResponse> reservationByIdResponses = mockReservationByIdResponseList();
    List<UpdateReservationsRequest> updatedReservations = mockUpdateReservationsRequestList();
    updatedReservations.get(0).setDistributionIATANumber("11223344");
    var bookingChannel = mockBookingChannel();
    bookingChannel.setChannel(DISTR_BOOKING_CHANNEL);
    updatedReservations.get(0).setBookingChannel(bookingChannel);

    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponses.get(0),
        updatedReservations.get(0).getReservations().get(0)))
        .thenReturn(false);

    // Act
    List<UpdateReservationsRequest> updateReservationsRequests =
        amendDistributionLogicInPortImpl.extractUpdatedReservations(reservationByIdResponses,
            updatedReservations, TEMP_BASKET_REF);

    // Assert
    assertEquals(2, updateReservationsRequests.size());
    assertEquals(RESERVATION_ID_1,
        updateReservationsRequests.get(0).getReservations().get(0).getReservationId());
    assertEquals(MARY_JANE_EMAIL, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail()
        .getEmailAddress());
    assertNotNull(updateReservationsRequests.get(0).getDistributionIATANumber());
    assertEquals(updateReservationsRequests.get(0).getDistributionIATANumber(), "11223344");
  }

  @Test
  void extractUpdateReservations_whenGuestsListEmptyOrNull_leadGuestFromOriginalResIsSet() {
    // Arrange
    var basket = mockCreateBasketResponse();
    var reservationByIdResponses = mockReservationByIdResponseList();
    var updatedReservations = mockUpdateReservationsRequestList();
    updatedReservations.get(0).getReservations().get(0).getReservationGuests().clear();
    updatedReservations.getFirst().setBookingChannel(mockBookingChannel());
    updatedReservations.get(1).getReservations().get(0).getRoomStay().getRoomOccupancy()
        .setChildCount(1);
    updatedReservations.get(1).getReservations().get(0).getReservationGuests().clear();

    when(basketOutPort.getBasketById(TEMP_BASKET_REF)).thenReturn(basket);
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponses.get(0),
        updatedReservations.get(0).getReservations().get(0)))
        .thenReturn(false);

    // Act
    List<UpdateReservationsRequest> updateReservationsRequests =
        amendDistributionLogicInPortImpl.extractUpdatedReservations(reservationByIdResponses,
            updatedReservations, TEMP_BASKET_REF);

    // Assert
    assertEquals(2, updateReservationsRequests.size());
    assertEquals(MARY_JANE_EMAIL, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail()
        .getEmailAddress());
    assertEquals(MARY, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getGivenName());
    assertEquals(JANE, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getSurname());
    assertEquals(MS, updateReservationsRequests.get(0).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getNameTitle());
    assertEquals(JOHN_DOE_EMAIL, updateReservationsRequests.get(1).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getEmails().getEmailInfo().get(0).getEmail()
        .getEmailAddress());
    assertEquals(JOHN, updateReservationsRequests.get(1).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getGivenName());
    assertEquals(DOE, updateReservationsRequests.get(1).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getSurname());
    assertEquals(MR, updateReservationsRequests.get(1).getReservations().get(0)
        .getReservationGuests().get(0)
        .getProfileInfo().getProfile().getCustomer().getPersonName().get(0).getNameTitle());
  }

  @Test
  void whenRoomIsRemoved_originalReservationsList_changesSize() {
    // Arrange
    BasketResponse basket = mockCreateBasketResponse();
    List<ReservationByIdResponse> reservationByIdResponses = mockReservationByIdResponseList();
    List<UpdateReservationsRequest> updatedReservations = mockUpdateReservationsRequestList();
    updatedReservations.remove(1);

    when(basketOutPort.getBasketById(TEMP_BASKET_REF)).thenReturn(basket);
    when(amendLogicInPort.isLeadGuestUpdated(reservationByIdResponses.get(0),
        updatedReservations.get(0).getReservations().get(0)))
        .thenReturn(false);
    updatedReservations.getFirst().setBookingChannel(mockBookingChannel());

    // Act
    amendDistributionLogicInPortImpl.extractUpdatedReservations(reservationByIdResponses,
        updatedReservations, TEMP_BASKET_REF);

    // Assert
    assertEquals(1, reservationByIdResponses.size());
  }

  private ReservationByBasketRefResponse mockReservationByBasketRefResponse() {
    return ReservationByBasketRefResponse.builder()
        .hotelId(HOTEL_ID)
        .reservationByIdList(
            List.of(mockReservationById1(), mockReservationById2()))
        .build();
  }

  private ReservationByIdResponse mockReservationById1() {
    return ReservationByIdResponse.builder()
        .reservationId(TEMP_RESERVATION_ID_1)
        .reservationPackageList(List.of(
            mockReservationPackage(PACKAGE_CODE_BFADBF)))
        .build();
  }

  private ReservationByIdResponse mockReservationById2() {
    return ReservationByIdResponse.builder()
        .reservationId(TEMP_RESERVATION_ID_2)
        .reservationPackageList(List.of(
            mockReservationPackage(PACKAGE_CODE_BFADCT),
            mockReservationPackage(PACKAGE_CODE_BFCHDF)))
        .build();
  }

  private static ReservationPackagesDetailsResponse mockReservationPackage(
      String packageCode) {
    return ReservationPackagesDetailsResponse.builder()
        .packageCode(packageCode)
        .totalQuantity(1)
        .packageGroup(PACKAGE_GROUP)
        .build();
  }

  private static RoomsSelectionsByReservationId mockRoomsSelectionsByReservationId_1() {
    return new RoomsSelectionsByReservationId(RESERVATION_ID_1,
        List.of(new PackagesSelection(PACKAGE_CODE_BFADCT, 1, PACKAGE_GROUP)));
  }

  private static RoomsSelectionsByReservationId mockRoomsSelectionsByReservationId_2() {
    return new RoomsSelectionsByReservationId(RESERVATION_ID_2,
        List.of(new PackagesSelection(PACKAGE_CODE_BFCHDF, 1, PACKAGE_GROUP)));
  }

  private BasketResponse mockCreateBasketResponse() {
    return BasketResponse.builder()
        .hotelId(MANOLD)
        .bookingReference("AWM0087829")
        .reference(TEMP_BASKET_REF)
        .linkAmendReservations(Map.of(RESERVATION_ID_1, TEMP_RESERVATION_ID_1, RESERVATION_ID_2,
            TEMP_RESERVATION_ID_2))
        .createdAt(new Date().toString())
        .status("OPEN")
        .eTag("TEST")
        .build();
  }

  private Reservation mockReservation1() {
    return Reservation.builder()
        .hotelId(MANOLD)
        .externalReferenceId(null)
        .roomRates(RoomRate.builder().startDate(START_DATE).endDate(END_DATE)
            .specialRequests(List.of("SING")).build())
        .leadGuest(LeadGuest.builder().title(MR).firstName(JOHN).lastName("Smith").build())
        .build();
  }

  private Reservation mockReservation2() {
    return Reservation.builder()
        .hotelId(MANOLD)
        .externalReferenceId("1736673")
        .roomRates(RoomRate.builder().startDate("2023-06-09").endDate("2023-06-10")
            .specialRequests(List.of("SING")).build())
        .leadGuest(LeadGuest.builder().title(MR).firstName("George").lastName("Write").build())
        .build();
  }

  private ReservationRequest mockReservationRequest() {
    return ReservationRequest.builder()
        .reservations(List.of(mockReservation1(), mockReservation2()))
        .build();
  }

  private ReservationRequest mockReservationRequestStayDates() {
    return ReservationRequest.builder()
        .reservations(List.of(mockReservation2()))
        .token("test_1837749")
        .bookingChannel(BookingChannel.builder()
            .channel("DISTR")
            .subchannel("AGENCY")
            .language("N/A")
            .build())
        .build();
  }

  private List<ReservationByIdResponse> mockReservationByIdResponseList() {
    List<ReservationByIdResponse> originalReservations = new ArrayList<>();
    ReservationByIdResponse reservation1 = ReservationByIdResponse.builder()
        .reservationId(RESERVATION_ID_1)
        .roomStay(
            RoomStayByIdResponse.builder()
                .arrivalDate(START_DATE)
                .departureDate(END_DATE)
                .adultsNumber(2)
                .childrenNumber(1)
                .roomType("DOUBLE")
                .build())
        .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
            .givenName(MARY)
            .surName(JANE)
            .nameTitle(MS)
            .email(MARY_JANE_EMAIL)
            .build()))
        .build();

    originalReservations.add(reservation1);

    ReservationByIdResponse reservation2 = ReservationByIdResponse.builder()
        .reservationId(RESERVATION_ID_2)
        .roomStay(
            RoomStayByIdResponse.builder()
                .arrivalDate(START_DATE)
                .departureDate(END_DATE)
                .adultsNumber(2)
                .childrenNumber(0)
                .roomType("DOUBLE")
                .build())
        .reservationGuestList(List.of(ReservationByIdGuestsResponse.builder()
            .givenName(JOHN)
            .surName(DOE)
            .nameTitle(MR)
            .email(JOHN_DOE_EMAIL)
            .build()))
        .build();

    originalReservations.add(reservation2);
    return originalReservations;
  }

  private List<UpdateReservationsRequest> mockUpdateReservationsRequestList() {
    var updatedReservations = new ArrayList<UpdateReservationsRequest>();
    var reservationGuests1 = new ArrayList<ReservationGuests>();

    var guest1 = ReservationGuests.builder()
        .profileInfo(ProfileInfo.builder()
            .profile(ProfileType.builder()
                .customer(CustomerType.builder()
                    .personName(List.of(
                        PersonNameType.builder()
                            .givenName(MARY)
                            .surname(JANE)
                            .nameTitle(MS)
                            .nameType("Primary")
                            .build()))
                    .build())
                .emails(ProfileTypeEmails.builder()
                    .emailInfo(List.of(
                        EmailInfoType.builder()
                            .email(EmailType.builder()
                                .emailAddress(MARY_JANE_EMAIL)
                                .build())
                            .build()))
                    .build())
                .build())
            .build())
        .build();
    reservationGuests1.add(guest1);

    var updRes1 = UpdateReservationRequest.builder()
        .hotelId(MANOLD)
        .reservationId(RESERVATION_ID_1)
        .roomStay(UpdateRoomStayRequest.builder()
            .roomOccupancy(UpdateRoomOccupancyRequest.builder()
                .adultCount(2)
                .childCount(0)
                .build())
            .roomRates(List.of(UpdateRoomRateRequest.builder()
                .roomType("DB")
                .build()))
            .build())
        .reservationGuests(reservationGuests1)
        .build();

    var update1 = UpdateReservationsRequest.builder()
        .reservations(List.of(updRes1))
        .build();

    updatedReservations.add(update1);

    var reservationGuests2 = new ArrayList<ReservationGuests>();

    var guest2 = ReservationGuests.builder()
        .profileInfo(ProfileInfo.builder()
            .profile(ProfileType.builder()
                .customer(CustomerType.builder()
                    .personName(List.of(
                        PersonNameType.builder()
                            .givenName(JOHN)
                            .surname(DOE)
                            .nameTitle(MR)
                            .nameType("Primary")
                            .build()))
                    .build())
                .emails(ProfileTypeEmails.builder()
                    .emailInfo(List.of(
                        EmailInfoType.builder()
                            .email(EmailType.builder()
                                .emailAddress(JOHN_DOE_EMAIL)
                                .build())
                            .build()))
                    .build())
                .build())
            .build())
        .build();
    reservationGuests2.add(guest2);

    var updRes2 = UpdateReservationRequest.builder()
        .hotelId(MANOLD)
        .reservationId(RESERVATION_ID_2)
        .roomStay(UpdateRoomStayRequest.builder()
            .roomOccupancy(UpdateRoomOccupancyRequest.builder()
                .adultCount(2)
                .childCount(0)
                .build())
            .roomRates(List.of(UpdateRoomRateRequest.builder()
                .roomType("DOUBLE")
                .build()))
            .build())
        .reservationGuests(reservationGuests2)
        .build();

    var update2 = UpdateReservationsRequest.builder()
        .reservations(List.of(updRes2))
        .build();

    updatedReservations.add(update2);
    return updatedReservations;
  }

  private BookingChannel mockBookingChannel() {
    return BookingChannel.builder()
        .channel("WEB")
        .subchannel("")
        .language("N/A")
        .build();
  }
}