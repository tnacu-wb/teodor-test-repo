package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.checkin.out.SourceOfSale;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CopyReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.Reservation;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRateReservation;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.AddressResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.BillingResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowance;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowancesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelInformationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancellationPoliciesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmationCustomer;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmationRoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmount;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.ohip.domain.model.reservation.out.Customer;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPolicies;
import uk.co.whitbread.ohip.domain.model.reservation.out.Deposits;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.RatePerNight;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCreationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetails;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetailsEnhancedResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationId;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.Reservations;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomsSelections;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.UniqueIdType;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;
import uk.co.whitbread.ohip.domain.ports.primary.HotelReservationInPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.DiscountInvalidAmountException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out.RoomRatesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.AttachReservationProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.BookerDetailsCnpRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.BookingAllowancesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancelReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancelReservationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancelResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CancellationPoliciesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CompanyQuestionAndAnswerRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ConfirmReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ConfirmReservationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CopyReservationsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.CopyReservationsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.DepositsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.LinkReservationToLeisureCustomerRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.MarketingPreferencesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.MemosMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.PreCheckInRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.RatePlanChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationAmountsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationByBasketRefResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationDetailsEnhancedResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationDetailsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationFileAttachmentRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationGuestRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationGuestResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationLightweightResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationPreferencesRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationsPackagesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.RoomTypeChangeRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.SearchBookingsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.SearchBookingsResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.SpecialRequestsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateBookerEmailRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateCustomReferenceNumberRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateDiscountRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReasonForStayResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReservationCcAgentIdRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReservationOverrideReasonsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.UpdateReservationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.AlertDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.AttachReservationProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BillingAddressCaptRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerAddressDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerDetailsCnpDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerDetailsCnpRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.BookerDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CancelReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ConfirmReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CopyReservationsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CreateMemoRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PackagesSelectionDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PaymentCardDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PaymentOptionDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.PreCheckInRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RatePlanChangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationFileAttachmentRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationGuestRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationPreferencesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.ReservationScheduledPackagesRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomRateDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomTypeChangeRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.RoomsSelectionsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.SearchBookingsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.SpecialRequestsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.StayingGuestAdditionalDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.StayingGuestAddressDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.StayingGuestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.StayingGuestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateBookerEmailRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateCustomReferenceNumberRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReasonForStayRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationCcAgentIdRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationOverrideReasonsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.UpdateReservationAlertsRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancelInformationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancelReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CancellationPoliciesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ConfirmReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ConfirmationCustomerDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ConfirmationRoomStayDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CopyReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CopyReservationsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CurrencyAmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CustomerDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.DepositsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ExternalReferenceTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.MarketingPreferencesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.MemosResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RatePerNightDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationAmountsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationCreationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationDetailsEnhancedDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationGuestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationGuestResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationIdDetailsCheckDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationIdDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationLightweightResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsDetailsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsIdDetailsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationsPackagesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomStayByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomStayDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomTypeChangeResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingBookerDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingReservationDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingStayingGuestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.SearchBookingsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.UniqueIdTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.UpdateReasonForStayResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.UserDefinedFieldsDto;

@ExtendWith(MockitoExtension.class)
class HotelReservationControllerTest {

  @InjectMocks
  HotelReservationController hotelReservationControllerUnderTest;

  @Mock
  private HotelReservationInPort reservationPortBusinessCase;

  @Mock
  private ReservationResponseMapper reservationResponseMapper;

  @Mock
  private ReservationDetailsEnhancedResponseMapper reservationDetailsEnhancedResponseMapper;

  @Mock
  private ReservationDetailsResponseMapper reservationDetailsResponseMapper;

  @Mock
  private ReservationRequestMapper reservationRequestMapper;

  @Mock
  private ConfirmReservationResponseMapper confirmReservationResponseMapper;

  @Mock
  private ConfirmReservationRequestMapper confirmReservationRequestMapper;

  @Mock
  private CancelReservationRequestMapper cancelReservationRequestMapper;

  @Mock
  private CancelReservationResponseMapper cancelReservationResponseMapper;


  @Mock
  private ReservationGuestRequestMapper reservationGuestRequestMapper;

  @Mock
  private ReservationGuestResponseMapper reservationGuestResponseMapper;

  @Mock
  private ReservationByBasketRefResponseMapper reservationByBasketRefResponseMapper;

  @Mock
  private ReservationLightweightResponseMapper reservationLightweightResponseMapper;

  @Mock
  private ReservationsPackagesResponseMapper reservationsPackagesResponseMapper;

  @Mock
  private CancelResponseMapper cancelResponseMapper;

  @Mock
  private UpdateReservationRequestMapper updateReservationRequestMapper;

  @Mock
  private RatePlanChangeRequestMapper reservationRateChangeRequestMapper;

  @Mock
  private RoomTypeChangeRequestMapper roomTypeChangeRequestMapper;

  @Mock
  private UpdateDiscountRequestMapper updateDiscountRequestMapper;

  @Mock
  private UpdateReasonForStayRequestMapper updateReasonForStayRequestMapper;

  @Mock
  private UpdateReasonForStayResponseMapper updateReasonForStayResponseMapper;

  @Mock
  private UpdateReservationOverrideReasonsRequestMapper updateReservationOverrideReasonsRequestMapper;

  @Mock
  private UpdateReservationCcAgentIdRequestMapper updateReservationCcAgentIdRequestMapper;

  @Mock
  private DepositsResponseMapper depositsResponseMapper;

  @Mock
  private CancellationPoliciesResponseMapper cancellationPoliciesResponseMapper;

  @Mock
  private SearchBookingsResponseMapper searchBookingsResponseMapper;

  @Mock
  private MarketingPreferencesResponseMapper marketingPreferencesResponseOhipMapper;

  @Mock
  private CopyReservationsRequestMapper copyReservationsRequestMapper;

  @Mock
  private CopyReservationsResponseMapper copyReservationsResponseMapper;

  @Mock
  private SearchBookingsRequestMapper searchBookingsRequestMapper;

  @Mock
  private BookingAllowancesResponseMapper bookingAllowancesResponseMapper;

  @Mock
  private SpecialRequestsMapper specialRequestsMapper;

  @Mock
  private BookerDetailsCnpRequestMapper bookerDetailsCnpRequestMapper;

  @Mock
  private UpdateBookerEmailRequestMapper updateBookerEmailRequestMapper;

  @Mock
  private CompanyQuestionAndAnswerRequestMapper companyQuestionAndAnswerRequestMapper;

  @Mock
  private MemosMapper memosMapper;

  @Mock
  private AttachReservationProfileRequestMapper attachReservationProfileRequestMapper;

  @Mock
  private UpdateCustomReferenceNumberRequestMapper updateCustomReferenceNumberRequestMapper;

  @Mock
  private ReservationFileAttachmentRequestMapper reservationFileAttachmentRequestMapper;

  @Mock
  private PreCheckInRequestMapper preCheckInRequestMapper;

  @Mock
  private LinkReservationToLeisureCustomerRequestMapper linkReservationToLeisureCustomerRequestMapper;

  @Mock
  private ReservationPreferencesRequestMapper reservationPreferencesRequestMapper;

  @Mock
  private ReservationAmountsMapper reservationAmountsMapper;

  @Test
  void whenUpdateBookerEmailThenReturnResponseEntity() {
    UpdateBookerEmailRequestDto updateBookerEmailRequestDto =
        Mockito.mock(UpdateBookerEmailRequestDto.class);
    UpdateBookerEmailRequest updateBookerEmailRequest =
        Mockito.mock(UpdateBookerEmailRequest.class);
    when(updateBookerEmailRequestMapper.toModel(updateBookerEmailRequestDto)).thenReturn(
        updateBookerEmailRequest);

    ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateBookerEmail(updateBookerEmailRequestDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void createReservation_ShouldReturnReservation() {
    //Arrange
    ReservationRequestDto createReservationRequestDto = createReservationRequestDto();

    when(reservationRequestMapper.toReservationRequestModel(createReservationRequestDto))
        .thenReturn(createReservationRequest());
    when(reservationPortBusinessCase.processCreateReservation(createReservationRequest()))
        .thenReturn(getReservationResponse());
    when(reservationResponseMapper.toReservationResponseDto(getReservationResponse()))
        .thenReturn(getReservationResponseDto());

    //act
    ReservationRequest request = reservationRequestMapper.toReservationRequestModel(
        createReservationRequestDto);
    ReservationResponse reservationResponse = reservationPortBusinessCase.processCreateReservation(
        request);
    var reservationResponseDto = reservationResponseMapper.toReservationResponseDto(
        reservationResponse);
    final ResponseEntity<ReservationResponseDto> response = hotelReservationControllerUnderTest.createReservation(
        createReservationRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(201, response.getStatusCode().value(), response.toString());
    assertEquals(reservationResponseDto.getHotelId(),
        Objects.requireNonNull(response.getBody()).getHotelId());
  }

  @Test
  void savePackages_ShouldReturnReservation() {
    //Arrange
    ReservationPackagesRequestDto reservationPackagesRequestDto = new ReservationPackagesRequestDto();

    when(updateReservationRequestMapper.toModel(reservationPackagesRequestDto))
        .thenReturn(new ReservationPackagesRequest());
    doNothing().when(reservationPortBusinessCase).updateReservationPackages(any());

    //act
    ReservationPackagesRequest request = updateReservationRequestMapper.toModel(
        reservationPackagesRequestDto);
    reservationPortBusinessCase.updateReservationPackages(request);
    final ResponseEntity<ReservationPackagesRequestDto> response = hotelReservationControllerUnderTest.savePackages(
        reservationPackagesRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());

  }

  @Test
  void updateReservationRatePlanCode_ShouldReturnReservation() {
    //Arrange
    RatePlanChangeRequestDto ratePlanChangeRequestDto = new RatePlanChangeRequestDto();

    when(reservationRateChangeRequestMapper.toModel(ratePlanChangeRequestDto))
        .thenReturn(new RatePlanRoomTypeChangeRequest());
    doNothing().when(reservationPortBusinessCase).changeReservationRatePlan(any());

    //act
    RatePlanRoomTypeChangeRequest request = reservationRateChangeRequestMapper.toModel(
        ratePlanChangeRequestDto);
    reservationPortBusinessCase.changeReservationRatePlan(request);
    final ResponseEntity<RatePlanChangeRequestDto> response = hotelReservationControllerUnderTest.updateReservationRatePlanCode(
        ratePlanChangeRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());

  }

  @Test
  void updateReservationRoomType_ShouldReturnReservation() {
    //Arrange
    RoomTypeChangeRequestDto roomTypeChangeRequestDto = new RoomTypeChangeRequestDto();

    when(roomTypeChangeRequestMapper.toModel(roomTypeChangeRequestDto))
        .thenReturn(new RatePlanRoomTypeChangeRequest());
    doNothing().when(reservationPortBusinessCase).changeReservationRoomType(any());

    //act
    RatePlanRoomTypeChangeRequest request = roomTypeChangeRequestMapper.toModel(
        roomTypeChangeRequestDto);
    reservationPortBusinessCase.changeReservationRoomType(request);
    final ResponseEntity<RoomTypeChangeResponseDto> response = hotelReservationControllerUnderTest.updateReservationRoomType(
        roomTypeChangeRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }

  @Test
  void updateReasonForStay_ShouldReturnReservation() {
    //Arrange
    UpdateReasonForStayRequestDto updateReasonForStayRequestDto = createUpdateReasonForStayRequestDTO();

    when(updateReasonForStayRequestMapper.toModel(updateReasonForStayRequestDto))
        .thenReturn(createUpdateResonForStayRequest());
    when(reservationPortBusinessCase.updateReasonForStay(any())).thenReturn(
        new UpdateReasonForStayResponse());
    when(updateReasonForStayResponseMapper.toDto(any())).thenReturn(
        new UpdateReasonForStayResponseDto());

    //act
    UpdateReasonForStayRequest request = updateReasonForStayRequestMapper.toModel(
        updateReasonForStayRequestDto);
    reservationPortBusinessCase.updateReasonForStay(request);
    final ResponseEntity<UpdateReasonForStayResponseDto> response = hotelReservationControllerUnderTest.updateReasonForStay(
        updateReasonForStayRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());

  }

  @Test
  void updateReservation_basketReference_ShouldReturnReservation() {
    //Arrange
    var hotelId = "hotelId";
    var reservationId = "1244";
    var externalReference = "31243";

    //Act
    ResponseEntity<Void> response = hotelReservationControllerUnderTest.updateReservationsWithExternalRef(
            hotelId, Set.of(reservationId), externalReference);

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());

  }

  @Test
  void updateReservation_routingInstructions_payeeInfo_ShouldReturnReservation() {
    //Arrange
    var hotelId = "hotelId";
    var reservationId = "1244";

    //Act
    ResponseEntity<Void> response = hotelReservationControllerUnderTest.updateRoutingInstructionsWithPayeeInfo(
            hotelId, Set.of(reservationId));

    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());

  }

  @Test
  void searchBookingReservation_ShouldReturnReservation() {
    //Arrange
    SearchBookingsRequestDto searchBookingsRequestDto = createUpdateSearchBookingRequestDTO();

    when(searchBookingsRequestMapper.toBookingSearchCriteriaModel(searchBookingsRequestDto))
        .thenReturn(bookingSearchCriteria());
    when(reservationPortBusinessCase.searchBookings(any())).thenReturn(
        new SearchBookingsResponse());
    when(searchBookingsResponseMapper.toSearchBookingsResponseDto(any())).
        thenReturn(searchBookingsResponseDto());

    //act
    BookingSearchCriteria request = searchBookingsRequestMapper.toBookingSearchCriteriaModel(
        searchBookingsRequestDto);
    reservationPortBusinessCase.searchBookings(request);
    final ResponseEntity<SearchBookingsResponseDto> response = hotelReservationControllerUnderTest.searchReservations(
        searchBookingsRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());

  }


  @Test
  void createReservationGuest_ShouldReturnReservation() {
    //Arrange
    ReservationGuestRequestDto reservationGuestRequestDto = createReservationGuestRequestDto();

    when(reservationGuestRequestMapper.toModel(reservationGuestRequestDto))
        .thenReturn(createReservationGuestRequest());
    when(
        reservationPortBusinessCase.createReservationGuest(createReservationGuestRequest()))
        .thenReturn(createReservationGuestResponse());
    when(reservationGuestResponseMapper.toDto(createReservationGuestResponse()))
        .thenReturn(getReservationGuestResponseDto());

    //act
    ReservationGuestRequest request = reservationGuestRequestMapper.toModel(
        reservationGuestRequestDto);
    ReservationGuestResponse reservationResponse = reservationPortBusinessCase.createReservationGuest(
        request);
    var reservationResponseDto = reservationGuestResponseMapper.toDto(reservationResponse);
    final ResponseEntity<ReservationGuestResponseDto> response = hotelReservationControllerUnderTest.createReservationGuest(
        reservationGuestRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(201, response.getStatusCode().value(), response.toString());
    assertEquals(reservationResponseDto.getHotelId(),
        Objects.requireNonNull(response.getBody()).getHotelId(), response.toString());
  }

  @Test
  void getReservationsByExternalReferenceIds_ShouldReturnReservation() {
    //Arrange
    String hotelId = "FRAMTI";
    List<String> externalReferenceIds = getCancellationIds();
    int limit = 20;
    int offset = 20;

    when(reservationPortBusinessCase
        .getReservationsByExternalReferenceIds(
            hotelId, externalReferenceIds, limit, offset)).thenReturn(
        createReservationDetailsResponse());
    when(reservationResponseMapper
        .toReservationsDetailsResponseDto(createReservationDetailsResponse()))
        .thenReturn(getReservationDetailsResponseDto());

    //act

    final ResponseEntity<ReservationsDetailsResponseDto> response = hotelReservationControllerUnderTest
        .getReservationsByExternalReferenceIds(hotelId, externalReferenceIds, limit, offset);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(getReservationDetailsResponseDto(), response.getBody());
  }

  @Test
  void getReservationsPaymentType_ShouldReturnReservation() {
    //Arrange
    String hotelId = "FRAMTI";

    var paymentMethodType = getReservationPaymentMethodType();
    var expected = List.of(getReservationPaymentMethodType());

    when(reservationPortBusinessCase
        .getReservationMethodPayment(hotelId, List.of("id"))).thenReturn(List.of(paymentMethodType));


    //act
    final ResponseEntity<List<ReservationsPaymentCardType>> response = hotelReservationControllerUnderTest
        .getPaymentTypeReservationsByReservationIds(hotelId, List.of("id"));

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(expected, response.getBody());
  }

  @Test
  void getReservationsByIds_ShouldReturnReservation() {
    //Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = getReservationIds();

    when(reservationPortBusinessCase
        .getReservationsByIds(hotelId, reservationIds, false, true, false))
        .thenReturn(createReservationByBasketRefResponse());
    when(reservationByBasketRefResponseMapper.toDto(createReservationByBasketRefResponse()))
        .thenReturn(getReservationByBasketRefResponseDto());

    //act
    ReservationByBasketRefResponse reservationsResponse = reservationPortBusinessCase
        .getReservationsByIds(hotelId, reservationIds, false, true, false);

    final ResponseEntity<ReservationByBasketRefResponseDto> response = hotelReservationControllerUnderTest
        .getReservationsByIds(hotelId, reservationIds, false, true, false);

    //Assert
    assertNotNull(response);
    assertEquals("2015-10-20",
        reservationsResponse.getReservationByIdList().get(0).getReservationPackageList().get(0)
            .getStartDate());
    assertEquals("2015-10-20",
        reservationsResponse.getReservationByIdList().get(0).getReservationPackageList().get(0)
            .getEndDate());
    assertEquals("address line 1", reservationsResponse.getReservationByIdList().get(0).getReservationGuestList().get(0).getAddress().getAddressLine1());
    assertEquals("address line 2", reservationsResponse.getReservationByIdList().get(0).getReservationGuestList().get(0).getAddress().getAddressLine2());
    assertEquals("cityName", reservationsResponse.getReservationByIdList().get(0).getReservationGuestList().get(0).getAddress().getCityName());
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }

  @Test
  void testGetLightweightReservationsByIds() {
    // Arrange
    String hotelId = "123";
    Set<String> reservationIds = Set.of("rsv1", "rsv2");

    ReservationLightweightResponse mockResponse = new ReservationLightweightResponse(List.of());
    ReservationLightweightResponseDto mockResponseDto = new ReservationLightweightResponseDto();

    when(reservationPortBusinessCase.getReservationsByIds(hotelId, reservationIds)).thenReturn(mockResponse);
    when(reservationLightweightResponseMapper.toDto(mockResponse)).thenReturn(mockResponseDto);

    // Act
    ResponseEntity<ReservationLightweightResponseDto> response = hotelReservationControllerUnderTest.getLightweightReservationsByIds(hotelId, reservationIds);

    // Assert
    assertEquals(mockResponseDto, response.getBody());
    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  void getReservationsByIds_operaUiReservation_Success() {
    //Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = getReservationIds();

    when(reservationPortBusinessCase
            .getReservationsByIds(hotelId, reservationIds, false, true, true))
            .thenReturn(createReservationOperaByBasketRefResponse());
    when(reservationByBasketRefResponseMapper.toDto(createReservationOperaByBasketRefResponse()))
            .thenReturn(getReservationByBasketRefResponseDto());

    //act
    ReservationByBasketRefResponse reservationsResponse = reservationPortBusinessCase
            .getReservationsByIds(hotelId, reservationIds, false, true, true);

    final ResponseEntity<ReservationByBasketRefResponseDto> response = hotelReservationControllerUnderTest
            .getReservationsByIds(hotelId, reservationIds, false, true, true);

    //Assert
    assertNotNull(response);
    var individualReservationResponse = reservationsResponse.getReservationByIdList()
        .stream()
        .findFirst()
        .orElseThrow(() -> new AssertionError("Reservation not found"));
    assertNotNull(individualReservationResponse.getBookingAllowancesResponse());
    assertNotNull(individualReservationResponse.getDepositFoliosResponse());
    var reservationPackageResponse = individualReservationResponse.getReservationPackageList().stream()
        .findFirst()
        .orElseThrow(() -> new AssertionError("Packages not found"));
    assertNotNull(reservationPackageResponse);
    assertEquals("MDP",reservationPackageResponse.getPackageGroup());
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }


  @Test
  void getCancelInformation_ShouldReturnReservation() {
    //Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = getReservationIds();
    String userDateTime = "userDateTime";

    when(reservationPortBusinessCase
        .getCancelInformation(hotelId, reservationIds, userDateTime))
        .thenReturn(createCancelInformationResponse());
    when(cancelResponseMapper.toDto(createCancelInformationResponse()))
        .thenReturn(getCancelInformationResponseDto());

    //act

    final ResponseEntity<CancelInformationResponseDto> response = hotelReservationControllerUnderTest
        .getCancelInformation(hotelId, reservationIds, userDateTime);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }

  @ParameterizedTest
  @CsvSource({"true", "false"})
  void getReservationsPackagesByIds_ShouldReturnReservation(boolean mealInclusiveRate) {
    //Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = getReservationIds();
    if (mealInclusiveRate) {
      when(reservationPortBusinessCase
          .getReservationsPackagesMealInclusiveRateByReservationsIds(hotelId, reservationIds))
          .thenReturn(createReservationsPackagesByReservationsIdsResponse());
    } else {
      when(reservationPortBusinessCase
          .getReservationsPackagesByReservationsIds(hotelId, reservationIds))
          .thenReturn(createReservationsPackagesByReservationsIdsResponse());
    }
    when(reservationsPackagesResponseMapper.toDto(
        createReservationsPackagesByReservationsIdsResponse()))
        .thenReturn(getReservationPackagesResponseDto());

    //act
    final ResponseEntity<ReservationsPackagesResponseDto> response = hotelReservationControllerUnderTest
        .getReservationsPackagesByIds(hotelId, reservationIds, mealInclusiveRate);

    //Assert
    assertNotNull(response.getBody());
    var roomResponse = response.getBody().getRoomsSelections()
        .stream()
        .findFirst()
        .orElseThrow(() -> new AssertionError("Room not found"));
    var packageResponse = roomResponse.getPackagesSelection().stream()
        .findFirst()
        .orElseThrow(() -> new AssertionError("Package not found"));
    assertEquals("MDP", packageResponse.getPackageGroup());
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }

  @Test
  void confirmReservation__ShouldReturnConfirmation() {
    //Arrange
    ConfirmReservationRequestDto confirmReservationRequestDto = createConfirmReservationRequestDto();
    when(confirmReservationRequestMapper.toConfirmReservationRequestModel(
        confirmReservationRequestDto)).thenReturn(createConfirmReservationRequest());
    when(reservationPortBusinessCase.confirmReservation(createConfirmReservationRequest()))
        .thenReturn(getConfirmReservationResponse());
    when(confirmReservationResponseMapper.toConfirmReservationResponseDto(
        getConfirmReservationResponse())).thenReturn(getConfirmReservationResponseDto());

    //act
    final var request = confirmReservationRequestMapper.toConfirmReservationRequestModel(
        confirmReservationRequestDto);
    final var reservationResponse = reservationPortBusinessCase.confirmReservation(request);
    final var reservationResponseDto = confirmReservationResponseMapper.toConfirmReservationResponseDto(
        reservationResponse);
    final ResponseEntity<ConfirmReservationResponseDto> response = hotelReservationControllerUnderTest.confirmReservation(
        confirmReservationRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(reservationResponseDto.getReservationStatus(), Objects
        .requireNonNull(response.getBody()).getReservationStatus(), response.toString());
  }

  @Test
  void cancelReservation__ShouldSaveReservation() {
    //Arrange

    CancelReservationRequestDto cancelReservationRequestDto = new CancelReservationRequestDto();

    when(cancelReservationRequestMapper.toModel(cancelReservationRequestDto))
        .thenReturn(createCancelReservationRequest());
    when(reservationPortBusinessCase.cancelReservation(createCancelReservationRequest()))
        .thenReturn(getCancelReservationResponse());
    when(cancelReservationResponseMapper.toDto(getCancelReservationResponse()))
        .thenReturn(getCancelReservationResponseDto());

    //act
    final var request = cancelReservationRequestMapper.toModel(cancelReservationRequestDto);
    final var reservationResponse = reservationPortBusinessCase.cancelReservation(request);
    final var reservationResponseDto = cancelReservationResponseMapper.toDto(reservationResponse);
    final ResponseEntity<CancelReservationResponseDto> response = hotelReservationControllerUnderTest.cancelReservation(
        cancelReservationRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(reservationResponseDto.getCancellationIds(), Objects
        .requireNonNull(response.getBody()).getCancellationIds(), response.toString());
  }

  @Test
  void getReservationsByExternalReferenceId_Success() {
    //Arrange
    String externalReferenceId = "123";

    when(reservationPortBusinessCase.getReservationsByExternalRefId(externalReferenceId))
        .thenReturn(createReservationDetailsEnhancedResponse());
    when(reservationDetailsEnhancedResponseMapper.toDto(createReservationDetailsEnhancedResponse()))
        .thenReturn(getReservationDetailsEnhancedDto());

    //act

    final ResponseEntity<ReservationDetailsEnhancedDto> response =
        hotelReservationControllerUnderTest.getReservationsByExternalId(externalReferenceId);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  void whenGetReservationsByExternalReferenceIdThenReturnNotFound() {
    String externalReferenceId = "123";
    when(
        reservationPortBusinessCase.getReservationsByExternalRefId(externalReferenceId)).thenReturn(
        null);

    ResponseEntity<ReservationDetailsEnhancedDto> response =
        hotelReservationControllerUnderTest.getReservationsByExternalId(externalReferenceId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }


  @Test
  void getReservationsByReservationId_Success() {
    //Arrange
    String reservationId = "123";
    String hotelId = "MANOLD";
    when(reservationPortBusinessCase.getReservationsByReservationId(hotelId, reservationId))
        .thenReturn(createReservationIdResponse());
    when(reservationDetailsResponseMapper.toDto(createReservationIdResponse()))
        .thenReturn(getReservationDetailsDto());

    //act

    final ResponseEntity<ReservationIdDetailsDto> response =
        hotelReservationControllerUnderTest.getReservationsByReservationId(hotelId, reservationId);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
  }

  @Test
  void whenGetReservationsByReservationIdThenReturnNotFound() {
    String reservationId = "123";
    String hotelId = "MANOLD";
    when(reservationPortBusinessCase.getReservationsByReservationId(hotelId,
        reservationId)).thenReturn(null);

    ResponseEntity<ReservationIdDetailsDto> response =
        hotelReservationControllerUnderTest.getReservationsByReservationId(hotelId, reservationId);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNull(response.getBody());
  }


  @Test
  void updateDiscount_shouldReturnSuccess() {
    //Arrange
    UpdateDiscountRequestDto updateDiscountRequestDto = new UpdateDiscountRequestDto();
    when(updateDiscountRequestMapper.toModel(updateDiscountRequestDto))
        .thenReturn(new UpdateDiscountRequest());
    doNothing().when(reservationPortBusinessCase).updateDiscount(any());

    //Act
    final ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateDiscount(updateDiscountRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    verifyNoMoreInteractions(updateDiscountRequestMapper);
    verifyNoMoreInteractions(reservationPortBusinessCase);
  }

  @Test
  void updateDiscount_shouldReturnError() {
    //Arrange
    UpdateDiscountRequestDto updateDiscountRequestDto = new UpdateDiscountRequestDto();
    when(updateDiscountRequestMapper.toModel(updateDiscountRequestDto))
        .thenReturn(new UpdateDiscountRequest());
    doThrow(new DiscountInvalidAmountException(ErrorCode.DIGITAL_UPDATE_DISCOUNT_EXCEPTION,
        "Exception")).when(reservationPortBusinessCase).updateDiscount(any());

    //Act
    var thrownException = assertThrowsExactly(DiscountInvalidAmountException.class,
        () -> hotelReservationControllerUnderTest.updateDiscount(updateDiscountRequestDto));

    //Assert
    String errorCode = thrownException.getGlobalErrTextTemplate();
    assertEquals("internal.server.exception", errorCode);
    verifyNoMoreInteractions(updateDiscountRequestMapper);
    verifyNoMoreInteractions(reservationPortBusinessCase);
  }

  @Test
  void updateReservationOverrideReasons__shouldReturnSuccess() {
    //Arrange
    var updateReservationOverrideReasonsRequestDto = new UpdateReservationOverrideReasonsRequestDto();
    when(updateReservationOverrideReasonsRequestMapper.toModel(
        updateReservationOverrideReasonsRequestDto))
        .thenReturn(new UpdateReservationOverrideReasonsRequest());
    doNothing().when(reservationPortBusinessCase).updateReservationOverrideReasons(any());

    //Act
    final ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateReservationOverrideReasons(
            updateReservationOverrideReasonsRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    verifyNoMoreInteractions(updateReservationOverrideReasonsRequestMapper);
    verifyNoMoreInteractions(reservationPortBusinessCase);
  }

  @Test
  void updateReservationCcAgentId__shouldReturnSuccess() {
    //Arrange
    var updateReservationCcAgentIdRequestDto = new UpdateReservationCcAgentIdRequestDto();
    when(updateReservationCcAgentIdRequestMapper.toModel(
        updateReservationCcAgentIdRequestDto))
        .thenReturn(new UpdateReservationCcAgentIdRequest());
    doNothing().when(reservationPortBusinessCase).updateReservationCcAgentId(any());

    //Act
    final ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateReservationCcAgentId(
            updateReservationCcAgentIdRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    verifyNoMoreInteractions(updateReservationCcAgentIdRequestMapper);
    verifyNoMoreInteractions(reservationPortBusinessCase);
  }

  @Test
  void deposits__ShouldReturnDepositsResponse() {
    //Arrange
    String hotelId = "FRAMTI";
    String resNo = "12345678";

    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(Arrays.asList(Deposits.builder().paymentReference("3CPREFERENCE")
        .build()));

    var depositsResponseDto = new DepositsResponseDto(
        Arrays.asList(DepositsDto.builder().paymentReference("3CPREFERENCE")
            .build()));

    Mockito.when(reservationPortBusinessCase.getDepositsForReservationId(hotelId, resNo))
        .thenReturn(depositsResponse);
    Mockito.when(depositsResponseMapper.toDto(depositsResponse))
        .thenReturn(depositsResponseDto);

    //act
    final ResponseEntity<DepositsResponseDto> response = hotelReservationControllerUnderTest.getDepositsForReservationId(
        hotelId, resNo);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(depositsResponseDto.getDeposits().get(0).getPaymentReference(),
        response.getBody().getDeposits().get(0).getPaymentReference(), response.toString());
  }

  @Test
  void marketingPreferences__ShouldReturnMarketingPreferencesResponse() {
    //Arrange
    String hotelId = "FRAMTI";
    String resNo = "12345678";

    var marketingPreferencesResponse = new MarketingPreferencesResponse();
    marketingPreferencesResponse.setContactValue("mail@mail.com");
    marketingPreferencesResponse.setOptIn(true);
    marketingPreferencesResponse.setCustomer(new Customer("Mr", "Sarah", "Smith", "GB", "en"));

    var customerDto = new CustomerDto("Mr", "Sarah", "Smith", "GB", "en");
    var marketingPreferencesResponseDto = new MarketingPreferencesResponseDto(true, customerDto,
        "mail@mail.com");

    Mockito.when(reservationPortBusinessCase.getMarketingPreferences(hotelId, resNo))
        .thenReturn(marketingPreferencesResponse);
    Mockito.when(marketingPreferencesResponseOhipMapper.toDto(marketingPreferencesResponse))
        .thenReturn(marketingPreferencesResponseDto);

    //act
    final ResponseEntity<MarketingPreferencesResponseDto> response =
        hotelReservationControllerUnderTest.getMarketingPreferences(hotelId, resNo);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(marketingPreferencesResponseDto.getOptIn(), Objects
        .requireNonNull(response.getBody()).getOptIn(), response.toString());
    assertEquals(marketingPreferencesResponseDto.getContactValue(), Objects
        .requireNonNull(response.getBody()).getContactValue(), response.toString());
  }

  @Test
  void getCancellationPolicies_ShouldReturnSucces() {
    //Arrange
    String hotelId = "LONEUS";
    Set<String> reservationIds = getReservationIds();

    when(reservationPortBusinessCase
        .getCancellationPolicies(reservationIds, hotelId, null, null))
        .thenReturn(createCancellationPolicies());
    when(cancellationPoliciesResponseMapper.toDto(createCancellationPolicies()))
        .thenReturn(createCancellationPoliciesDto());

    //act

    final ResponseEntity<CancellationPoliciesResponseDto> response = hotelReservationControllerUnderTest
        .getCancellationPolicies(reservationIds, hotelId, null, null);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    verifyNoMoreInteractions(cancellationPoliciesResponseMapper);
    verifyNoMoreInteractions(reservationPortBusinessCase);
  }

  @Test
  void getBookingAllowances_ShouldReturnSucces() {
    //Arrange
    String hotelId = "LONEUS";
    String reservationId = "123456";
    List<String> basketBookingAllowances = List.of("dinner");

    when(reservationPortBusinessCase.getBookingAllowances(hotelId, reservationId, basketBookingAllowances))
        .thenReturn(createBookingAllowanceResponse());

    //Assert
    verifyNoMoreInteractions(reservationPortBusinessCase);
    assertDoesNotThrow(
        () -> hotelReservationControllerUnderTest.getBookingAllowances(hotelId, reservationId, basketBookingAllowances));
  }

  @Test
  void copyReservations_ShouldReturnSuccess() {
    //Arrange
    var copyReservationsRequestDto = createCopyReservationsRequestDto();
    var copyReservationsRequest = createCopyReservationsRequest();
    var copyReservationsResponse = createCopyReservationsResponse();
    var copyReservationsResponseDto = createCopyReservationsResponseDto();

    when(copyReservationsRequestMapper.toModel(copyReservationsRequestDto))
        .thenReturn(createCopyReservationsRequest());
    when(reservationPortBusinessCase.copyReservations(copyReservationsRequest))
        .thenReturn(copyReservationsResponse);
    when(copyReservationsResponseMapper.toDto(copyReservationsResponse))
        .thenReturn(copyReservationsResponseDto);

    //act
    final ResponseEntity<CopyReservationsResponseDto> response = hotelReservationControllerUnderTest.copyReservations(
        copyReservationsRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(copyReservationsResponseDto, response.getBody(), response.toString());
  }

  @Test
  void updateBookerDetails_ShouldReturnSuccess() {
    //Arrange
    var bookerDetailsCnpRequestDto = createBookerDetailsCnpRequestDto();
    var bookerDetailsCnpRequest = createbookerDetailsCnpRequest();

    when(bookerDetailsCnpRequestMapper.toModel(bookerDetailsCnpRequestDto))
        .thenReturn(bookerDetailsCnpRequest);

    //act
    var response = hotelReservationControllerUnderTest.updateBookerDetails(
        bookerDetailsCnpRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }

  @Test
  void whenUpdateSpecialRequestsThenReturnOk() {
    //Arrange
    when(specialRequestsMapper.toModel(any())).thenReturn(any());
    //Act
    ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateReservationsSpecialRequests(
            SpecialRequestsDto.builder().build());
    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void updateBillingAddressTest_ShouldUpdateBillingAddress() {
    //Arrange
    BillingAddressCaptRequestDto billingAddressCaptRequestDto = updateBillingAddressRequestDto();

    when(reservationGuestRequestMapper.toModel(billingAddressCaptRequestDto))
        .thenReturn(updateBillingAddressRequest());
    doNothing().when(reservationPortBusinessCase)
        .updateBillingAddress(updateBillingAddressRequest());

    //act
    var response = hotelReservationControllerUnderTest.updateBillingAddress(
        billingAddressCaptRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(204, response.getStatusCode().value(), response.toString());

  }

  @Test
  void deleteReservation_ShouldReturnVoid() {
    //Arrange
    String reservationId = "123456";
    String hotelId = "HOTELTEST";
    doNothing().when(reservationPortBusinessCase).deleteReservation(anyString(), anyString());

    //Act
    hotelReservationControllerUnderTest.deleteReservation(hotelId, reservationId);

    //Assert
    verify(reservationPortBusinessCase, times(1)).deleteReservation(anyString(), anyString());
  }

  @Test
  void movePaymentDetails_ShouldReturnVoid() {
    //Arrange
    String hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");
    doNothing().when(reservationPortBusinessCase).movePaymentDetails(anyString(), any());

    //Act
    hotelReservationControllerUnderTest.movePaymentDetails(reservationIds, hotelId);

    //Assert
    verify(reservationPortBusinessCase, times(1)).movePaymentDetails(anyString(), any());
  }

  @Test
  void deleteRoutingInstructions_shouldReturn204() {
    //Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = getReservationIds();

    doNothing().when(reservationPortBusinessCase)
        .deleteRoutingInstruction(hotelId, reservationIds);

    //act
    final ResponseEntity<Void> response = hotelReservationControllerUnderTest
        .deleteRoutingInstructions(hotelId, reservationIds);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void createMemo_Success() {
    // Arrange
    when(memosMapper.toModel(any(CreateMemoRequestDto.class))).thenReturn(new CreateMemoRequest());
    when(memosMapper.toDto(any())).thenReturn(new MemosResponseDto());

    // Act
    MemosResponseDto response = hotelReservationControllerUnderTest.createMemo(
        new CreateMemoRequestDto());

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMemos_Success() {
    // Arrange
    when(memosMapper.toDto(any())).thenReturn(new MemosResponseDto());

    // Act
    MemosResponseDto response =
        hotelReservationControllerUnderTest.getMemos("hotelId",
            Set.of("reservationId1, reservationId2"));

    // Assert
    assertNotNull(response);
  }

  @Test
  void attachProfileToReservations_Success() {
    // Arrange
    when(attachReservationProfileRequestMapper.toModel(any()))
        .thenReturn(new AttachReservationProfileRequest());
    doNothing().when(reservationPortBusinessCase).attachProfileToReservations(any());

    // Act
    ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.attachProfileToReservations(
            new AttachReservationProfileRequestDto());

    // Assert
    assertNotNull(response);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    verify(reservationPortBusinessCase, times(1)).attachProfileToReservations(any());
  }

  @Test
  void updateCustomReferenceNumber_Success() {
    // Arrange
    when(updateCustomReferenceNumberRequestMapper.toModel(any()))
        .thenReturn(new UpdateCustomReferenceNumberRequest());
    doNothing().when(reservationPortBusinessCase).updateCustomReferenceNumber(any());

    // Act
    ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateCustomReferenceNumber(
            new UpdateCustomReferenceNumberRequestDto());

    // Assert
    assertNotNull(response);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    verify(reservationPortBusinessCase, times(1)).updateCustomReferenceNumber(any());
  }

  @Test
  void linkReservationToLeisureCustomer__shouldReturnSuccess() {
    //Arrange
    var linkReservationToLeisureCustomerRequestDto = new LinkReservationToLeisureCustomerRequestDto();

    when(linkReservationToLeisureCustomerRequestMapper.toModel(
        linkReservationToLeisureCustomerRequestDto))
        .thenReturn(new LinkReservationToLeisureCustomerRequest());
    doNothing().when(reservationPortBusinessCase).linkReservationToLeisureCustomer(any());

    //Act
    final ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.linkReservationToLeisureCustomer(
            linkReservationToLeisureCustomerRequestDto);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    verifyNoMoreInteractions(linkReservationToLeisureCustomerRequestMapper);
    verifyNoMoreInteractions(reservationPortBusinessCase);
  }

  @Test
  void updatePreferences_Success() {
    // Arrange
    when(reservationPreferencesRequestMapper.toModel(any()))
        .thenReturn(new ReservationPreferencesRequest());
    doNothing().when(reservationPortBusinessCase).updateReservationPreferences(any());

    // Act
    ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updatePreferences(
            new ReservationPreferencesRequestDto());

    // Assert
    assertNotNull(response);
    assertTrue(response.getStatusCode().is2xxSuccessful());
    verify(reservationPortBusinessCase, times(1)).updateReservationPreferences(any());
  }

  private CopyReservationsResponse createCopyReservationsResponse() {
    return CopyReservationsResponse.builder()
        .reservations(Arrays.asList(new CopyReservationResponse("857716", "2023-02-23T15:21:22Z"),
            new CopyReservationResponse("857717", "2023-02-23T15:22:22Z")))
        .build();
  }

  private CopyReservationsResponseDto createCopyReservationsResponseDto() {
    var copyReservationResponseDto1 = new CopyReservationResponseDto();
    copyReservationResponseDto1.setReservationId("857716");
    copyReservationResponseDto1.setCreateDateTime("2023-02-23T15:21:22Z");

    var copyReservationResponseDto2 = new CopyReservationResponseDto();
    copyReservationResponseDto2.setReservationId("857717");
    copyReservationResponseDto2.setCreateDateTime("2023-02-23T15:22:22Z");

    var copyReservationsResponseDto = new CopyReservationsResponseDto();
    copyReservationsResponseDto.setReservations(
        Arrays.asList(copyReservationResponseDto1, copyReservationResponseDto2));
    return copyReservationsResponseDto;
  }

  private CopyReservationsRequestDto createCopyReservationsRequestDto() {
    CopyReservationsRequestDto copyReservationsRequestDto = new CopyReservationsRequestDto();
    copyReservationsRequestDto.setReservationIds(getReservationIds());
    copyReservationsRequestDto.setHotelId("LONEUS");
    copyReservationsRequestDto.setExternalReferenceId("ABC12345");
    return copyReservationsRequestDto;
  }

  private CopyReservationsRequest createCopyReservationsRequest() {
    return CopyReservationsRequest.builder()
        .reservationIds(getReservationIds())
        .hotelId("LONEUS")
        .externalReferenceId("ABC12345")
        .build();
  }

  private ReservationResponse getReservationResponse() {
    return new ReservationResponse(List.of(createReservationCreationResponse()),
        new BigDecimal(1), "FRAMTI", "currencyCode");
  }

  private ReservationResponseDto getReservationResponseDto() {
    return new ReservationResponseDto(List.of(createReservationCreationResponseDto()),
        new BigDecimal(1), "FRAMTI", "currencyCode");
  }

  private ConfirmReservationResponse getConfirmReservationResponse() {
    return new ConfirmReservationResponse(
        getConfirmedReservatinIds(),
        getConfirmationRoomStayResponse(),
        getConfirmationCustomer(),
        "LONEUS",
        "Reserverd"
    );
  }

  private CancelReservationResponse getCancelReservationResponse() {
    return new CancelReservationResponse(
        getCancellationIds(), null);
  }

  private List<String> getCancellationIds() {
    return List.of("147", "852");
  }

  private Set<String> getReservationIds() {
    return new HashSet<>(Arrays.asList("147", "852"));
  }

  private List<UniqueIdType> getConfirmedReservatinIds() {
    return List.of(createUniqueIDType("147", "Reservation"),
        createUniqueIDType("852", "Confirmation"));
  }

  private List<UniqueIdTypeDto> getDtoConfirmedReservationIds() {
    return List.of(createUniqueIdTypeDto("34865", "Reservation"),
        createUniqueIdTypeDto("264873", "Confirmation"));
  }

  private ConfirmReservationResponseDto getConfirmReservationResponseDto() {
    return new ConfirmReservationResponseDto(
        getDtoConfirmedReservationIds(),
        getDtoConfirmationRoomStayResponse(),
        getDtoConfirmationCustomer(),
        "LONEUS",
        "Reserved"
    );
  }

  private CancelReservationResponseDto getCancelReservationResponseDto() {
    return new CancelReservationResponseDto(
        getCancellationIds(), null);
  }

  private RoomStayDto createRoomStayDto() {
    return new RoomStayDto(LocalDate.parse("2023-01-08"), LocalDate.parse("2023-01-08"),
        null, 1, 1, "roomClass", "roomType", 1,
        "ratePlanCode", createCurrencyAmountTypeDto(), true,
        "bookingChannelCode", true, createCurrencyAmountTypeDto(),
        "marketCode", "sourceCode", "roomTypeCharged",
        true, true);
  }


  private ReservationsDetailsResponseDto getReservationDetailsResponseDto() {
    return new ReservationsDetailsResponseDto(createReservationsDto());
  }

  private ReservationsPaymentCardType getReservationPaymentMethodType() {
    ReservationPaymentCardType reservationPaymentMethodType = ReservationPaymentCardType.builder()
        .folioView(1)
        .paymentMethod("BU")
        .cardNumberMasked("xxxx-xxxx-xxxx-1234")
        .cardType("CU")
        .cardId(new UniqueIdType("id", "type"))
        .build();
    return ReservationsPaymentCardType.builder()
        .paymentCardType(reservationPaymentMethodType)
        .ids(List.of()).build();
  }

 ReservationsIdDetailsResponseDto getReservationIdDetailsResponseDto() {
    return new ReservationsIdDetailsResponseDto(createReservationsIdDto());
  }

  private ReservationDetailsEnhancedDto getReservationDetailsEnhancedDto() {
    return ReservationDetailsEnhancedDto.builder()
        .reservationsDetailsResponse(getReservationDetailsResponseDto())
        .build();
  }

  private ReservationIdDetailsDto getReservationDetailsDto() {
    return ReservationIdDetailsDto.builder()
        .reservationsDetailsResponse(getReservationIdDetailsResponseDto())
        .build();
  }

  private ReservationsDto createReservationsDto() {
    return new ReservationsDto(List.of(createReservationInfoDto()),
        1, 1, 1, true, 1);
  }

  private ReservationsIdDto createReservationsIdDto() {
    return new ReservationsIdDto(List.of(createReservationIdDetailsCheckDto()),
        1, 1, 1, true, 1);
  }


  private ReservationInfoDto createReservationInfoDto() {
    return new ReservationInfoDto(List.of(createUniqueIdTypeDto("34865", "Reservation")),
        List.of(createExternalReferenceTypeDto()), createRoomStayDto(),
        createReservationGuestDto(), "FRAMTI", "FRAMTI", true, "Completed");
  }

  private ReservationIdDetailsCheckDto createReservationIdDetailsCheckDto() {
     return new ReservationIdDetailsCheckDto(
        List.of(createUniqueIdTypeDto("34865", "Reservation")),
        createsourceSale("123", "ABC"), createRoomStaydto(), List.of(createReservationGuest()),
        null, null, null, null, null,
        null, true, "FRAMTI", true, "reserved", "computed", true, true, "12-10-2023", "134",
        "23:00", "12", "12-10-2023", true, true, false, false, false, false, false, false,
         UserDefinedFieldsDto.builder().build());

  }


  private ReservationGuestDto createReservationGuestDto() {
    return new ReservationGuestDto("givenName", "surname", "nameTitle",
        "fullName", "phoneNumber", "email",
        LocalDate.parse("2000-01-08"), "language", true,
        "id", "type");
  }

  private ExternalReferenceTypeDto createExternalReferenceTypeDto() {
    return new ExternalReferenceTypeDto("id", 1, "idContext");
  }

  private ReservationsDetailsResponse createReservationDetailsResponse() {
    return new ReservationsDetailsResponse(createReservations());
  }


  private ReservationIdResponse createReservationDetailsIdResponse() {
    return new ReservationIdResponse(createReservationId());
  }

  private ReservationGuestResponseDto getReservationGuestResponseDto() {
    ReservationGuestResponseDto reservationGuestResponseDto = new ReservationGuestResponseDto();
    reservationGuestResponseDto.setHotelId("FRAMTI");
    reservationGuestResponseDto.setReservationIds(List.of("147", "852"));
    return reservationGuestResponseDto;
  }

  private ReservationGuestResponse createReservationGuestResponse() {
    return ReservationGuestResponse.builder()
        .hotelId("FRAMTI")
        .reservationIds(List.of("147", "852"))
        .build();
  }

  private ReservationGuestRequest createReservationGuestRequest() {
    return ReservationGuestRequest.builder()
        .hotelId("FRAMTI")
        .reasonForStay("LEI")
        .stayingGuests(List.of(createStayingGuest()))
        .booker(createBookerDeatils())
        .sendEmailConfirmation(Boolean.TRUE)
        .sendEmailInvoice(Boolean.TRUE)
        .build();
  }

  private BillingAddressRequest updateBillingAddressRequest() {
    return BillingAddressRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("3728071"))
        .booker(createBookerDeatils())
        .build();
  }


  private UpdateReasonForStayRequest createUpdateResonForStayRequest() {
    return UpdateReasonForStayRequest.builder()
        .hotelId("FRAMTI")
        .reasonForStay("LEI")
        .reservationIds(Collections.singletonList("132484"))
        .build();
  }

  private BookingSearchCriteria bookingSearchCriteria() {
    return BookingSearchCriteria.builder()
        .hotelId("FRAMTI")
        .bookingReference("1234")
        .build();
  }

  private StayingGuest createStayingGuest() {
    return StayingGuest.builder()
        .reservationId("132484")
        .sameAsBooker(true)
        .stayingGuestDetails(createStayingGuestDetails())
        .build();
  }

  private StayingGuestDetails createStayingGuestDetails() {
    return StayingGuestDetails.builder()
        .title("Mr")
        .firstName("FirstName")
        .lastName("LastName")
        .additionalDetails(stayingGuestAdditionalDetails())
        .build();
  }

  private StayingGuestAdditionalDetails stayingGuestAdditionalDetails() {
    return StayingGuestAdditionalDetails.builder().dob(LocalDate.parse("1996-07-13"))
        .nationality("Briton")
        .passportNumber("PR123JDS").build();
  }

  private BookerDetails createBookerDeatils() {
    return BookerDetails.builder()
        .title("Mr")
        .firstName("FirstName")
        .lastName("LastName")
        .emailAddress("emailAddress")
        .acceptFutureMailing(true)
        .mobile("mobile")
        .landline("landline")
        .address(createBookerAddress())
        .acceptFutureMailing(Boolean.FALSE)
        .build();
  }

  private BookerAddress createBookerAddress() {
    return BookerAddress.builder()
        .addressType("addressType")
        .postalCode("postalCode")
        .addressLine1("addressLine1")
        .addressLine2("addressLine2")
        .addressLine3("addressLine3")
        .addressLine4("addressLine4")
        .countryCode("countryCode")
        .cityName("cityName")
        .companyName("companyName")
        .build();
  }

  private ReservationByBasketRefResponseDto getReservationByBasketRefResponseDto() {
    return null;
  }

  private ReservationByBasketRefResponse createReservationByBasketRefResponse() {
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(createReservationById()))
        .previousTotal(new BigDecimal(1))
        .balanceOutstanding(new BigDecimal(1))
        .newTotal(new BigDecimal(1))
        .totalCost(new BigDecimal(1))
        .policyCode("policyCode")
        .currencyCode("currencyCode")
        .build();
  }

  private ReservationByBasketRefResponse createReservationOperaByBasketRefResponse() {
    return ReservationByBasketRefResponse.builder()
            .reservationByIdList(List.of(createReservationForOperaUi()))
            .previousTotal(new BigDecimal(1))
            .balanceOutstanding(new BigDecimal(1))
            .newTotal(new BigDecimal(1))
            .totalCost(new BigDecimal(1))
            .policyCode("policyCode")
            .currencyCode("currencyCode")
            .build();
  }


  private ReservationById createReservationById() {
    return ReservationById.builder()
        .reservationGuestList(List.of(createReservationGuest()))
        .roomStay(createRoomStay())
        .depositPolicies(List.of(createDepositPolicies()))
        .billing(createBillingResponse())
        .reservationPackageList(List.of(createReservationPackagesDetailsResponse()))
        .balanceAmount(BigDecimal.valueOf(60))
        .build();
  }

  private ReservationById createReservationForOperaUi() {
    return ReservationById.builder()
            .reservationGuestList(List.of(createReservationGuest()))
            .roomStay(createRoomStay())
            .depositPolicies(List.of(createDepositPolicies()))
            .billing(createBillingResponse())
            .reservationPackageList(List.of(createReservationPackagesDetailsResponse()))
            .balanceAmount(BigDecimal.valueOf(60))
            .operaLinkedReservation(true)
            .bookingAllowancesResponse(createBookingAllowanceResponse())
            .depositFoliosResponse(mockDepositFoliosResponse())
            .build();
  }

  private ReservationPackagesDetailsResponse createReservationPackagesDetailsResponse() {
    return ReservationPackagesDetailsResponse.builder()
        .description("description")
        .unitPrice(new BigDecimal(1))
        .totalQuantity(1)
        .computedPrice(new BigDecimal(1))
        .startDate("2015-10-20")
        .endDate("2015-10-20")
        .packageGroup("MDP")
        .build();
  }

  private BillingResponse createBillingResponse() {
    return BillingResponse.builder()
        .address(createAddressResponse())
        .email("email")
        .firstName("firstName")
        .lastName("lastName")
        .telephone("telephone")
        .title("title")
        .build();
  }

  private AddressResponse createAddressResponse() {
    return AddressResponse.builder()
        .countryCode("countryCode")
        .line1("line1")
        .line2("line2")
        .line3("line3")
        .line4("line4")
        .postalCode("postalCode")
        .build();
  }

  private ReservationsPackagesResponseDto getReservationPackagesResponseDto() {
    return ReservationsPackagesResponseDto.builder()
        .roomsSelections(List.of(createRoomSelectionsDto()))
        .build();
  }

  private RoomsSelectionsDto createRoomSelectionsDto() {
    return new RoomsSelectionsDto(List.of(createPackagesSelectionDto()));
  }

  private PackagesSelectionDto createPackagesSelectionDto() {
    var packageSelectionDto = new PackagesSelectionDto("id", 1);
    packageSelectionDto.setPackageGroup("MDP");
    return packageSelectionDto;
  }

  private ReservationPackagesResponse createReservationsPackagesByReservationsIdsResponse() {
    return ReservationPackagesResponse.builder()
        .roomsSelections(List.of(createRoomSelections()))
        .build();
  }

  private RoomsSelections createRoomSelections() {
    return RoomsSelections.builder()
        .packagesSelection(List.of(createPackagesSelection()))
        .build();
  }

  private PackagesSelection createPackagesSelection() {
    return PackagesSelection.builder()
        .id("id")
        .noSelections(1)
        .packageGroup("MDP")
        .build();
  }

  private CancelInformationResponseDto getCancelInformationResponseDto() {
    return new CancelInformationResponseDto(true);
  }


  private SearchBookingBookerDto createSearchBookingBookerDto() {
    return new SearchBookingBookerDto("12", "Mrs", "nameTitle",
        "test", "abc@gmail.com", "678910",
        "language",
        "442307", "type");
  }


  private SearchBookingsResponseDto searchBookingsResponseDto() {
    return new SearchBookingsResponseDto(List.of(searchBookingDto()), 0,
        2, 3, true, 0, false);

  }


  private SearchBookingDto searchBookingDto() {
    return new SearchBookingDto("54621", "MANOLD",
        "ManchesterOld", "", createSearchBookingBookerDto(),
        LocalDate.parse("2000-01-08"), LocalDate.parse("2000-01-08"),
        List.of(searchBookingReservationDto()), BigDecimal.valueOf(22), "GBP");

  }

  private SearchBookingReservationDto searchBookingReservationDto() {
    return new SearchBookingReservationDto("givenName", "surname",
        "nameTitle", searchBookingStayingGuestDto());
  }

  private SearchBookingStayingGuestDto searchBookingStayingGuestDto() {
    return new SearchBookingStayingGuestDto("givenName", "surname", "nameTitle",
        "fullName");
  }

  private CancelInformationResponse createCancelInformationResponse() {
    return new CancelInformationResponse(true);
  }

  private ReservationGuestRequestDto createReservationGuestRequestDto() {
    ReservationGuestRequestDto reservationGuestRequestDto = new ReservationGuestRequestDto();
    reservationGuestRequestDto.setHotelId("FRAMTI");
    reservationGuestRequestDto.setReasonForStay("LEI");
    reservationGuestRequestDto.setBooker(createBookerDeatilsDto());
    reservationGuestRequestDto.setStayingGuests(List.of(createStayingGuestDto()));
    reservationGuestRequestDto.setSendEmailConfirmation(Boolean.TRUE);
    reservationGuestRequestDto.setSendEmailInvoice(Boolean.TRUE);
    return reservationGuestRequestDto;
  }

  private BillingAddressCaptRequestDto updateBillingAddressRequestDto() {
    BillingAddressCaptRequestDto billingAddressCaptRequestDto = new BillingAddressCaptRequestDto();
    billingAddressCaptRequestDto.setHotelId("LONEUS");
    billingAddressCaptRequestDto.setReservationIds(List.of("3728071"));
    billingAddressCaptRequestDto.setBooker(createBookerDeatilsDto());
    return billingAddressCaptRequestDto;
  }

  private StayingGuestDto createStayingGuestDto() {
    StayingGuestDto stayingGuestDto = new StayingGuestDto();
    stayingGuestDto.setReservationId("1234");
    stayingGuestDto.setSameAsBooker(true);
    stayingGuestDto.setStayingGuestDetails(createStayingGuestDetailsDto());
    return stayingGuestDto;
  }

  private StayingGuestDetailsDto createStayingGuestDetailsDto() {
    StayingGuestDetailsDto stayingGuestDetailsDto = new StayingGuestDetailsDto();
    stayingGuestDetailsDto.setFirstName("John");
    stayingGuestDetailsDto.setLastName("Carry");
    stayingGuestDetailsDto.setTitle("Mrs");
    stayingGuestDetailsDto.setAddress(createStayingGuestAddressDto());
    stayingGuestDetailsDto.setAdditionalDetails(createStayingGuestAdditionalDetailsDto());
    return stayingGuestDetailsDto;
  }

  private StayingGuestAddressDto createStayingGuestAddressDto() {
    var stayingGuestAddressDto = new StayingGuestAddressDto();
    stayingGuestAddressDto.setAddressType("BUSINESS");
    stayingGuestAddressDto.setPostalCode("123456");
    stayingGuestAddressDto.setAddressLine1("SOME STREET");
    stayingGuestAddressDto.setCountryCode("UK");
    return stayingGuestAddressDto;
  }

  private StayingGuestAdditionalDetailsDto createStayingGuestAdditionalDetailsDto() {
    var stayingGuestAdditionalDetailsDto = new StayingGuestAdditionalDetailsDto();
    stayingGuestAdditionalDetailsDto.setDob(LocalDate.parse("1996-07-13"));
    stayingGuestAdditionalDetailsDto.setNationality("Briton");
    stayingGuestAdditionalDetailsDto.setPassportNumber("PR123JDS");
    return stayingGuestAdditionalDetailsDto;
  }

  private BookerDetailsDto createBookerDeatilsDto() {
    BookerDetailsDto bookerDetailsDto = new BookerDetailsDto();
    bookerDetailsDto.setTitle("Mrs");
    bookerDetailsDto.setFirstName("John");
    bookerDetailsDto.setLandline("3905678754");
    bookerDetailsDto.setLastName("Carry");
    bookerDetailsDto.setEmailAddress("john.carry@email.com");
    bookerDetailsDto.setAcceptFutureMailing(true);
    bookerDetailsDto.setMobile("3905678754");
    bookerDetailsDto.setAddress(createBookerAddressDto());
    bookerDetailsDto.setAcceptFutureMailing(Boolean.FALSE);
    return bookerDetailsDto;
  }

  private BookerAddressDto createBookerAddressDto() {
    BookerAddressDto bookerAddressDto = new BookerAddressDto();
    bookerAddressDto.setAddressType("HOME");
    bookerAddressDto.setPostalCode("WC2N 5DU");
    bookerAddressDto.setAddressLine1("4 Brockley Avenue");
    bookerAddressDto.setAddressLine2("4 Brockley Avenue");
    bookerAddressDto.setAddressLine3("4 Brockley Avenue");
    bookerAddressDto.setAddressLine4("4 Brockley Avenue");
    bookerAddressDto.setCountryCode("UK");
    bookerAddressDto.setCityName("cityName");
    bookerAddressDto.setCompanyName("companyName");
    return bookerAddressDto;
  }

  private ReservationRequestDto createReservationRequestDto() {
    ReservationRequestDto reservationRequestDto = new ReservationRequestDto();
    reservationRequestDto.setReservations(List.of(createReservationDto()));
    return reservationRequestDto;
  }

  private UpdateReasonForStayRequestDto createUpdateReasonForStayRequestDTO() {
    UpdateReasonForStayRequestDto updateReasonForStayRequestDto = new UpdateReasonForStayRequestDto();
    updateReasonForStayRequestDto.setReservationIds(Collections.singletonList("FRAMTI"));
    updateReasonForStayRequestDto.setReasonForStay("LEI");
    updateReasonForStayRequestDto.setHotelId("132484");
    return updateReasonForStayRequestDto;
  }

  private SearchBookingsRequestDto createUpdateSearchBookingRequestDTO() {
    SearchBookingsRequestDto searchBookingsRequestDto = new SearchBookingsRequestDto();
    searchBookingsRequestDto.setBookingReference("FRAMTI");
    searchBookingsRequestDto.setCompanyName("LEI");
    searchBookingsRequestDto.setHotelId("132484");
    return searchBookingsRequestDto;
  }

  private ReservationDto createReservationDto() {
    ReservationDto reservationDto = new ReservationDto();
    reservationDto.setHotelId("LONSTM");
    reservationDto.setArrival("2015-10-20");
    reservationDto.setDeparture("2015-10-21");
    reservationDto.setExternalReferenceId("132484");
    reservationDto.setRoomRates(createRoomRateDto());
    reservationDto.setAdults(1);
    reservationDto.setChildren(1);
    reservationDto.setCotRequired(true);
    reservationDto.setGdsReferenceNumber("ABCD1234");
    reservationDto.setBookingNotes("NONSMOKING");
    reservationDto.setDistributionUsername("testDistUsername");
    reservationDto.setDistributionIATANumber("00000000123456");
    reservationDto.setBookingType("ANON");
    return reservationDto;
  }

  private RoomRateDto createRoomRateDto() {
    RoomRateDto roomRateDto = new RoomRateDto();
    roomRateDto.setStart("2015-10-20");
    roomRateDto.setEnd("2015-10-21");
    roomRateDto.setRoomType("SDB");
    roomRateDto.setRatePlanCode("DAILY");
    roomRateDto.setCellCode("ABC");
    return roomRateDto;
  }

  private ConfirmReservationRequestDto createConfirmReservationRequestDto() {
    ConfirmReservationRequestDto confirmReservationRequestDto = new ConfirmReservationRequestDto();
    confirmReservationRequestDto.setReservationId("1234");
    confirmReservationRequestDto.setHotelId("FRAMTI");
    confirmReservationRequestDto.setPaymentCard(createPaymentCardDto());
    confirmReservationRequestDto.setPaymentOption(createPaymentOptionDto());
    return confirmReservationRequestDto;
  }

  private PaymentOptionDto createPaymentOptionDto() {
    return PaymentOptionDto.PAY_ON_ARRIVAL;
  }

  private PaymentCardDto createPaymentCardDto() {
    PaymentCardDto paymentCardDto = new PaymentCardDto();
    paymentCardDto.setCardType("Va");
    paymentCardDto.setToken("4111111111111111");
    paymentCardDto.setExpirationDate("2025-03-31");
    paymentCardDto.setCardHolderName("Tester");
    paymentCardDto.setCardNumberLast4Digits("1234");
    return paymentCardDto;
  }

  private CancellationPoliciesResponse createCancellationPolicies() {
    return CancellationPoliciesResponse.builder()
        .text("Cancellations after 1pm on the day of arrival charged 100% of 1 night")
        .time("2022-03-04T01:00:00+00:00")
        .build();
  }

  private CancellationPoliciesResponseDto createCancellationPoliciesDto() {
    return CancellationPoliciesResponseDto.builder()
        .text("Cancellations after 1pm on the day of arrival charged 100% of 1 night")
        .time("2022-03-04T01:00:00+00:00")
        .build();
  }

  private ReservationDetailsEnhancedResponse createReservationDetailsEnhancedResponse() {
    return ReservationDetailsEnhancedResponse.builder()
        .reservationsDetailsResponse(createReservationDetailsResponse())
        .billing(createBillingResponse())
        .currencyCode("GBP")
        .policyCode("OA")
        .build();
  }

  private ReservationIdDetailsResponse createReservationIdResponse() {
    return ReservationIdDetailsResponse.builder()
        .reservationIdResponse(createReservationDetailsIdResponse())
        .build();
  }

  private BookerDetailsCnpRequest createbookerDetailsCnpRequest() {
    return BookerDetailsCnpRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetailsCnp.builder()
            .emailAddress("test@whitbread.com")
            .build()
        ).build();
  }

  private BookerDetailsCnpRequestDto createBookerDetailsCnpRequestDto() {
    var booker = new BookerDetailsCnpDto();
    booker.setEmailAddress("test@whitbread.com");

    var request = new BookerDetailsCnpRequestDto();
    request.setReservationIds(List.of("1234"));
    request.setHotelId("LONEUS");
    request.setBooker(booker);

    return request;
  }

  private BookingAllowancesResponse createBookingAllowanceResponse() {
    return BookingAllowancesResponse.builder()
        .bookingAllowances(Arrays.asList(
            BookingAllowance.builder()
                .allowance("dinner")
                .budget(BigDecimal.TEN)
                .build(),
            BookingAllowance.builder()
                .allowance("alcohol")
                .budget(BigDecimal.ZERO)
                .build()))
        .build();
  }

  private UniqueIdType createUniqueIDType(String id, String type) {
    return new UniqueIdType(id, type);
  }


  private ConfirmationRoomStay getConfirmationRoomStayResponse() {
    return ConfirmationRoomStay.builder()
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now()).build();
  }

  private ConfirmationCustomer getConfirmationCustomer() {
    return ConfirmationCustomer.builder()
        .givenName("Emma")
        .surName("Smith").build();
  }

  private UniqueIdTypeDto createUniqueIdTypeDto(String id, String type) {
    return new UniqueIdTypeDto(id, type);
  }

  private SourceOfSale createsourceSale(String id, String type) {
    return new SourceOfSale(id, type);
  }

  private ConfirmationRoomStayDto getDtoConfirmationRoomStayResponse() {
    return new ConfirmationRoomStayDto(LocalDate.now(), LocalDate.now());
  }

  private ConfirmationCustomerDto getDtoConfirmationCustomer() {
    return new ConfirmationCustomerDto("Emma", "Smith");
  }

  private ConfirmReservationRequest createConfirmReservationRequest() {
    return ConfirmReservationRequest.builder()
        .hotelId("FRAMTI")
        .reservationId("1234")
        .paymentOption(PaymentOption.PAY_ON_ARRIVAL)
        .build();
  }

  private CancelReservationRequest createCancelReservationRequest() {
    return CancelReservationRequest.builder()
        .hotelId("FRAMTI")
        .reservationIds(List.of("147", "852"))
        .paymentOption(PaymentOption.PAY_NOW)
        .build();
  }

  @Test
  void whenUpdateQuestionsAndAnswersThenReturnOk() {
    //Arrange
    when(companyQuestionAndAnswerRequestMapper.toModel(any())).thenReturn(any());

    //Act
    ResponseEntity<Void> response =
        hotelReservationControllerUnderTest.updateQuestionsAndAnswers(
            CompanyQuestionAndAnswerDetailsRequestDto.builder()
                .reservationIds(Set.of("123456")).hotelId("LONEUS").build());
    //Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void addAttachmentToReservation_ShouldReturnPreCheckInResponse() {
    // Arrange
    ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto = createReservationFileAttachmentRequestDto();

    when(reservationFileAttachmentRequestMapper.toModel(reservationFileAttachmentRequestDto))
        .thenReturn(createReservationFileAttachmentRequest());
    when(reservationPortBusinessCase.addAttachmentToReservation(
        createReservationFileAttachmentRequest()))
        .thenReturn(createPreCheckInResponse());

    // Act
    ReservationFileAttachmentRequest request = reservationFileAttachmentRequestMapper.toModel(
        reservationFileAttachmentRequestDto);
    PreCheckInResponse preCheckInResponse = reservationPortBusinessCase.addAttachmentToReservation(
        request);
    final ResponseEntity<PreCheckInResponse> response = hotelReservationControllerUnderTest.addAttachmentToReservation(
        reservationFileAttachmentRequestDto);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(preCheckInResponse.getStatus(),
        Objects.requireNonNull(response.getBody()).getStatus(), response.toString());
    assertEquals(preCheckInResponse.getMessage(), response.getBody().getMessage(),
        response.toString());
  }

  private ReservationFileAttachmentRequestDto createReservationFileAttachmentRequestDto() {
    ReservationFileAttachmentRequestDto reservationFileAttachmentRequestDto = new ReservationFileAttachmentRequestDto();
    reservationFileAttachmentRequestDto.setFileName("REG_RES1234567_ID232323_P76767676.pdf");
    reservationFileAttachmentRequestDto.setReservationId("1234567");
    reservationFileAttachmentRequestDto.setOverwriteExistingFile(false);
    reservationFileAttachmentRequestDto.setDescription("Test description");
    reservationFileAttachmentRequestDto.setHotelId("STUAIR");
    reservationFileAttachmentRequestDto.setGlobal(false);
    reservationFileAttachmentRequestDto.setFileAttachment("Base64 encoded string");
    return reservationFileAttachmentRequestDto;
  }

  private ReservationFileAttachmentRequest createReservationFileAttachmentRequest() {
    return ReservationFileAttachmentRequest.builder()
        .fileName("REG_RES1234567_ID232323_P76767676.pdf")
        .reservationId("1234567")
        .overwriteExistingFile(false)
        .description("Test description")
        .hotelId("STUAIR")
        .global(false)
        .fileAttachment("Base64 encoded string")
        .build();
  }

  private PreCheckInResponse createPreCheckInResponse() {
    PreCheckInResponse response = new PreCheckInResponse();
    response.setStatus("Success");
    response.setMessage("File attached successfully");
    return response;
  }

  @Test
  void saveReservationPreCheckIn_ShouldReturnPreCheckInResponse() {
    // Arrange
    PreCheckInRequestDto preCheckInRequestDto = createPreCheckInRequestDto();
    PreCheckInRequest preCheckInRequest = createPreCheckInRequest();
    PreCheckInResponse preCheckInResponse = createPreCheckInResponse();

    when(preCheckInRequestMapper.toModel(preCheckInRequestDto)).thenReturn(preCheckInRequest);
    when(reservationPortBusinessCase.saveReservationPreCheckIn(preCheckInRequest)).thenReturn(
        preCheckInResponse);

    // Act
    final ResponseEntity<PreCheckInResponse> response = hotelReservationControllerUnderTest.saveReservationPreCheckIn(
        preCheckInRequestDto);

    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
    assertEquals(preCheckInResponse.getStatus(),
        Objects.requireNonNull(response.getBody()).getStatus(), response.toString());
    assertEquals(preCheckInResponse.getMessage(), response.getBody().getMessage(),
        response.toString());
  }

  @Test
  void deleteReservationPreCheckIn_ShouldReturnVoid() {
    //Arrange
    String reservationId = "123456";
    String hotelId = "FRAMTI";
    doNothing().when(reservationPortBusinessCase)
        .deleteReservationPreCheckIn(anyString(), anyString());

    //Act
    hotelReservationControllerUnderTest.deleteReservationPreCheckIn(hotelId, reservationId);

    //Assert
    verify(reservationPortBusinessCase, times(1))
        .deleteReservationPreCheckIn(anyString(), anyString());
  }

  @Test
  void deleteRegCard_ShouldReturnVoid() {
    //Arrange
    String reservationId = "123456";
    String hotelId = "FRAMTI";
    doNothing().when(reservationPortBusinessCase).deleteRegCardAttachment(anyString(), anyString());

    //Act
    hotelReservationControllerUnderTest.deleteRegCardAttachment(hotelId, reservationId);

    //Assert
    verify(reservationPortBusinessCase, times(1))
        .deleteRegCardAttachment(anyString(), anyString());
  }

  @Test
  void updateScheduledPackages_Success() {
    // Arrange
    when(updateReservationRequestMapper.toModel(any(ReservationScheduledPackagesRequestDto.class)))
        .thenReturn(new ReservationPackagesRequest());
    doNothing().when(reservationPortBusinessCase).updateReservationPackages(any());

    //Act
    hotelReservationControllerUnderTest.updateScheduledPackages(
        new ReservationScheduledPackagesRequestDto());
    verify(reservationPortBusinessCase, times(1))
        .updateReservationPackages(any());
  }

  @Test
  void updateReservationAlerts_Success() {
    // Arrange
    when(updateReservationRequestMapper.toAlertsModel(any(UpdateReservationAlertsRequestDto.class)))
        .thenReturn(new UpdateReservationAlertsRequest());
    doNothing().when(reservationPortBusinessCase).updateReservationAlerts(any(
        UpdateReservationAlertsRequest.class));

    //Act
    hotelReservationControllerUnderTest.updateReservationAlerts(
        new UpdateReservationAlertsRequestDto(Set.of("rsvId"), "hotelId",
            List.of(new AlertDto("id","area", "code", "description",
                true, false))));

    //Assert
    verify(reservationPortBusinessCase, times(1))
        .updateReservationAlerts(any(UpdateReservationAlertsRequest.class));
  }

  @Test
  void getReservationAmounts_shouldReturnSuccessfully() {
    //Arrange
    String hotelId = "FRAMTI";
    Set<String> reservationIds = getReservationIds();

    when(reservationPortBusinessCase
        .getReservationAmounts(hotelId, reservationIds))
        .thenReturn(new ReservationAmounts());
    when(reservationAmountsMapper.toDto(any(ReservationAmounts.class)))
        .thenReturn(new ReservationAmountsDto());

    //act
    final ResponseEntity<ReservationAmountsDto> response = hotelReservationControllerUnderTest
        .getReservationAmounts(hotelId, reservationIds);

    //Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value(), response.toString());
  }

  private PreCheckInRequestDto createPreCheckInRequestDto() {
    PreCheckInRequestDto preCheckInRequestDto = new PreCheckInRequestDto();
    preCheckInRequestDto.setHotelId("STAUIR");
    preCheckInRequestDto.setReservationId("123456");
    preCheckInRequestDto.setArrivalTime(LocalDate.now());
    return preCheckInRequestDto;
  }

  private PreCheckInRequest createPreCheckInRequest() {
    return PreCheckInRequest.builder()
        .hotelId("STAUIR")
        .reservationId("123456")
        .arrivalTime(new Date())
        .build();
  }

  private ReservationRequest createReservationRequest() {
    return new ReservationRequest(List.of(createReservation()),
        BookingChannel.builder().channel("PI").subchannel("WEB").language("EN").build(),
        false);
  }

  private Reservation createReservation() {
    return Reservation.builder()
        .hotelId("FRAMTI")
        .externalReferenceId("ext")
        .roomRates(createRoomRateReservation())
        .arrival("arrival")
        .adults(1)
        .departure("departure")
        .gdsReferenceNumber("ABCD1234")
        .bookingNotes("NONSMOKING")
        .distributionUsername("testDistUsername")
        .distributionIATANumber("00000000123456")
        .bookingType("ANON")
        .build();
  }

  private Reservations createReservations() {
    return Reservations.builder()
        .reservationInfo(List.of(createReservationInfo()))
        .totalPages(1)
        .totalResults(1)
        .offset(1)
        .limit(1)
        .hasMore(true)
        .build();
  }

  private ReservationId createReservationId() {
    return ReservationId.builder()
        .reservation(List.of(createReservationDetailsId()))
        .totalPages(1)
        .totalResults(1)
        .offset(1)
        .limit(1)
        .hasMore(true)
        .build();
  }

  private ReservationInfo createReservationInfo() {
    return ReservationInfo.builder()
        .hotelId("FRAMTI")
        .reservationIdList(List.of(createUniqueIDType("147", "Reservation")))
        .externalReferences(List.of())
        .roomStay(createRoomStay())
        .reservationGuest(createReservationGuest())
        .hotelName("FRAMTI")
        .roomStayReservation(true)
        .build();
  }

  private ReservationDetails createReservationDetailsId() {
    return ReservationDetails.builder()
        .hotelId("FRAMTI")
        .reservationIdList(List.of(createUniqueIDType("147", "Reservation")))
        .roomStay(createRoomStay())
        .hotelName("FRAMTI")
        .roomStayReservation(true)
        .build();
  }

  private ReservationGuest createReservationGuest() {
    return ReservationGuest.builder()
        .givenName("givenName")
        .surname("surname")
        .nameTitle("nameTitle")
        .fullName("fullName")
        .phoneNumber("phoneNumber")
        .email("email")
        .address(createReservationGuestAddress())
        .birthDate(LocalDate.parse("2000-01-08"))
        .language("language")
        .guestRestricted(true)
        .id("id")
        .type("type")
        .additionalDetails(createStayingGuestAdditionalDetails())
        .build();
  }

  private GuestAddress createReservationGuestAddress() {
    return GuestAddress.builder()
        .addressType("HOME")
        .addressLine1("address line 1")
        .addressLine2("address line 2")
        .addressLine3("address line 3")
        .addressLine4("address line 4")
        .cityName("cityName")
        .countryCode("GB")
        .postalCode("postal code")
        .build();
  }

  private StayingGuestAdditionalDetails createStayingGuestAdditionalDetails() {
    return StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.parse("1996-07-13"))
        .nationality("UK")
        .passportNumber("ABCD1234")
        .build();
  }

  private RoomRateReservation createRoomRateReservation() {
    return RoomRateReservation.builder()
        .start("start")
        .end("end")
        .roomType("roomType")
        .ratePlanCode("ratePlanCode")
        .cellCode("ABC")
        .build();
  }

  private ReservationCreationResponseDto createReservationCreationResponseDto() {
    return new ReservationCreationResponseDto("147", "createDateTime", createRoomStayByIdDto());

  }

  private ReservationCreationResponse createReservationCreationResponse() {
    return new ReservationCreationResponse("147", "createDateTime", createRoomStay(),
        List.of(createDepositPolicies()));

  }

  private RoomStayByIdDto createRoomStayByIdDto() {
    return RoomStayByIdDto.builder()
        .adults(1)
        .children(1)
        .cot(true)
        .roomType("roomType")
        .ratePlanCode("ratePlanCode")
        .arrivalDate("arrivalDate")
        .departureDate("departureDate")
        .roomPrice(new BigDecimal(1))
        .ratesPerNight(List.of(createRatePerNightDto()))
        .build();
  }

  private RoomStayDto createRoomStaydto() {
    return RoomStayDto.builder()
        .arrivalDate(LocalDate.parse("2023-01-08"))
        .departureDate(LocalDate.parse("2023-01-08"))
        .adultCount(1)
        .childCount(1)
        .roomRates(List.of(RoomRatesDto.builder().sourceCode("32").build()))
        .roomClass("roomClass")
        .roomType("roomType")
        .numberOfRooms(1)
        .ratePlanCode("ratePlanCode")
        .rateSuppressed(true)
        .bookingChannelCode("bookingChannelCode")
        .fixedRate(true)
        .marketCode("marketCode")
        .sourceCode("sourceCode")
        .roomTypeCharged("roomTypeCharged")
        .roomNumberLocked(true)
        .pseudoRoom(true)
        .build();
  }

  private RoomStay createRoomStay() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.parse("2023-01-08"))
        .departureDate(LocalDate.parse("2023-01-08"))
        .adultCount(1)
        .childCount(1)
        .cot(true)
        .roomClass("roomClass")
        .roomType("roomType")
        .numberOfRooms(1)
        .ratePlanCode("ratePlanCode")
        .rateAmount(createCurrencyAmountType())
        .rateSuppressed(true)
        .bookingChannelCode("bookingChannelCode")
        .fixedRate(true)
        .totalAmount(createCurrencyAmountType())
        .marketCode("marketCode")
        .sourceCode("sourceCode")
        .roomTypeCharged("roomTypeCharged")
        .roomNumberLocked(true)
        .pseudoRoom(true)
        .roomPrice((new BigDecimal(1)))
        .ratesPerNight(List.of(createRatePerNight()))
        .cellCode("cellCode")
        .roomNumber("120")
        .bookingChannel("PI.com")
        .build();
  }

  private RatePerNightDto createRatePerNightDto() {
    return RatePerNightDto.builder()
        .startDate("start")
        .pricePerNight(new BigDecimal(1))
        .build();
  }

  private RatePerNight createRatePerNight() {
    return RatePerNight.builder()
        .startDate("start")
        .pricePerNight(new BigDecimal(1))
        .cityTaxPerNight(new BigDecimal(2))
        .grossPricePerNight(new BigDecimal(3))
        .vatRate(new BigDecimal(4))
        .cityTaxVat(new BigDecimal(5))
        .build();
  }

  private CurrencyAmountType createCurrencyAmountType() {
    return CurrencyAmountType.builder()
        .currencyCode("currencyCode")
        .amount(new BigDecimal(1))
        .build();
  }

  private CurrencyAmountTypeDto createCurrencyAmountTypeDto() {
    return new CurrencyAmountTypeDto(new BigDecimal(1),
        "currencyCode");
  }

  private DepositPolicies createDepositPolicies() {
    return DepositPolicies.builder()
        .amountPaid(createCurrencyAmountType())
        .amountDue(createCurrencyAmountType())
        .policyCode("policyCode")
        .build();
  }

  private DepositFoliosResponse mockDepositFoliosResponse() {
    DepositFolioCharge charge = DepositFolioCharge.builder().quantity(1).transactionCode("9016")
            .reference("2034-07-27").currencyAmount(CurrencyAmount.builder().amount(
                    BigDecimal.valueOf(123)).currencyCode("EUR").build()).build();
    DepositFolio depositFolio = DepositFolio.builder().reservationId("11").hotelId("DONTAB")
            .charges(Collections.singletonList(charge)).build();

    return DepositFoliosResponse.builder().depositFolios(Collections.singletonList(depositFolio))
            .build();
  }

  @Test
  void saveReservationPreRegister_ShouldReturnPreCheckInResponse() {
    // Arrange
    PreCheckInRequestDto preCheckInRequestDto = createPreCheckInRequestDto();
    PreCheckInRequest preCheckInRequest = createPreCheckInRequest();
    PreCheckInResponse preCheckInResponse = createPreCheckInResponse();

    when(preCheckInRequestMapper.toModel(preCheckInRequestDto)).thenReturn(preCheckInRequest);
    when(reservationPortBusinessCase.saveReservationPreRegister(preCheckInRequest)).thenReturn(preCheckInResponse);

    // Act
    final ResponseEntity<PreCheckInResponse> response = hotelReservationControllerUnderTest
            .saveReservationPreRegister(preCheckInRequestDto);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    PreCheckInResponse body = response.getBody();
    assertNotNull(body, "Response body must not be null");
    assertEquals(preCheckInResponse.getStatus(), body.getStatus());
    assertEquals(preCheckInResponse.getMessage(), body.getMessage());
  }
}
