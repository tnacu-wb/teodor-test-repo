package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.ohip.domain.model.checkin.out.UserDefinedFields;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.ohip.domain.model.reservation.out.BillingResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAlerts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCompany;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.CurrencyAmountDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.in.DepositFolioChargeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out.DepositFolioDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.deposit.model.out.DepositFoliosResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.StayingGuestAdditionalDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.AdditionalGuestInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.AddressResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.BillingResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.BookingAllowanceDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.BookingAllowancesResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CharacterUDFsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.CurrencyAmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.DepositPoliciesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.GuaranteeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RateInfoDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RateInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RateInfoSummaryDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RatePerNightDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ResCashieringTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationAlertsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationBookerAddressDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationBookerDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByBasketRefResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByIdGuestAddressDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationByIdGuestsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationCompanyDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationEmailNotificationsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationEventPreferenceDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationOverrideReasonsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationPackagesDetailsResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationPaymentCardTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.ReservationTaxTypeInfoDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.RoomStayByIdDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out.UserDefinedFieldsDto;

@Mapper(componentModel = "spring")
public interface ReservationByBasketRefResponseMapper {

  @Mapping(source = "reservationByBasketRefResponse", target = "reservationByIdList",
      qualifiedByName = "toDtoFromModel")
  @Mapping(source = "idContext", target = "idContext")
  ReservationByBasketRefResponseDto toDto(
      ReservationByBasketRefResponse reservationByBasketRefResponse);

  ReservationCompanyDto toReservationCompanyDto(ReservationCompany reservationCompany);

  @Named("toDtoFromModel")
  default List<ReservationByIdDto> toDtoFromModel(
      ReservationByBasketRefResponse reservationByBasketRefResponse) {
    return reservationByBasketRefResponse.getReservationByIdList().stream()
        .map(this::toReservationDto).toList();
  }

  @Named("toReservationDto")
  default ReservationByIdDto toReservationDto(ReservationById reservation) {
    var reservationBookerSource = reservation.getReservationBooker();
    var bookerProfileId = reservationBookerSource.getProfileId();
    
    final var reservationGuestList = reservation.getReservationGuestList()
        .stream()
        .map(reservationGuest -> new ReservationByIdGuestsDto(reservationGuest.getGivenName(),
            reservationGuest.getSurname(), reservationGuest.getNameTitle(),
            reservationGuest.getEmail(), reservationGuest.getType(),
            getReservationByIdGuestAddressDto(reservationGuest.getAddress()),
            getReservationByIdGuestHomeAddressDto(reservationGuest.getHomeAddress()),
            getAdditionalDetailsDto(reservationGuest.getAdditionalDetails()),
            reservationGuest.getIsAccompanyingGuest(),
            reservationGuest.getProfileId(),
            isSameAsBooker(reservationGuest.getProfileId(), bookerProfileId)
        ))
        .toList();
    var reservationBookerDtoBuilder = ReservationBookerDto.builder()
        .profileId(bookerProfileId)
        .email(reservationBookerSource.getEmail())
        .mobile(reservationBookerSource.getMobile())
        .landline(reservationBookerSource.getLandline())
        .title(reservationBookerSource.getTitle())
        .firstName(reservationBookerSource.getFirstName())
        .lastName(reservationBookerSource.getLastName());
    if (Objects.nonNull(reservationBookerSource.getAddress())) {
      reservationBookerDtoBuilder.address(ReservationBookerAddressDto.builder()
          .addressType(reservationBookerSource.getAddress().getAddressType())
          .companyName(reservationBookerSource.getAddress().getCompanyName())
          .cityName(reservationBookerSource.getAddress().getCityName())
          .countryCode(reservationBookerSource.getAddress().getCountryCode())
          .postalCode(reservationBookerSource.getAddress().getPostalCode())
          .addressLine1(reservationBookerSource.getAddress().getAddressLine1())
          .addressLine2(reservationBookerSource.getAddress().getAddressLine2())
          .addressLine3(reservationBookerSource.getAddress().getAddressLine3())
          .addressLine4(reservationBookerSource.getAddress().getAddressLine4())
          .build());
    }
    final var reservationBookerDto = reservationBookerDtoBuilder.build();
    List<RatePerNightDto> ratePerNightDtoList =
        reservation.getRoomStay().getRatesPerNight().stream()
            .map(ratePerNight -> new RatePerNightDto(ratePerNight.getStartDate(),
                ratePerNight.getPricePerNight(), ratePerNight.getGrossPricePerNight(),
                ratePerNight.getVatRate(),
                ratePerNight.getCityTaxPerNight(), ratePerNight.getCityTaxAmountBeforeTax(),
                ratePerNight.getCityTaxVat()))
            .toList();
    var roomPrice = reservation.getRoomStay().getRoomPrice()
        .add(computeCityTaxAmountPerRoom(ratePerNightDtoList));
    final RoomStayByIdDto roomStay = new RoomStayByIdDto(reservation.getRoomStay().getAdultCount(),
        reservation.getRoomStay().getChildCount(), reservation.getRoomStay().getCot(),
        reservation.getRoomStay().getRoomType(), reservation.getRoomStay().getRatePlanCode(),
        reservation.getRoomStay().getArrivalDate().toString(), reservation.getRoomStay().getDepartureDate().toString(),
        reservation.getRoomStay().getCheckInTime(), reservation.getRoomStay().getCheckOutTime(),
        roomPrice, reservation.getRoomStay().getSourceCode(), ratePerNightDtoList,
        reservation.getRoomStay().getCellCode(), reservation.getRoomStay().getRoomNumber(),
        reservation.getRoomStay().getBookingChannel(),
        reservation.getRoomStay().getPromotionCode());
    final var rateInfoDto = getRateInfoDto(reservation);
    final AdditionalGuestInfoDto additionalGuestInfoDto = AdditionalGuestInfoDto.builder()
        .purposeOfStay(reservation.getAdditionalGuestInfo().getPurposeOfStay())
        .acceptFutureMailing(reservation.getAdditionalGuestInfo().getAcceptFutureMailing())
        .build();
    final List<DepositPoliciesDto> depositPoliciesDtos = reservation.getDepositPolicies().stream().map(
            depositPolicies -> new DepositPoliciesDto(
                new CurrencyAmountTypeDto(depositPolicies.getAmountPaid().getAmount(),
                    depositPolicies.getAmountPaid().getCurrencyCode()),
                new CurrencyAmountTypeDto(depositPolicies.getAmountDue().getAmount(),
                    depositPolicies.getAmountDue().getCurrencyCode()), depositPolicies.getPolicyCode()))
        .toList();
    final List<ReservationPackagesDetailsResponseDto> reservationPackagesDetailsResponseDtos = reservation
        .getReservationPackageList().stream().map(
            reservationPackagesDetailsResponse -> new ReservationPackagesDetailsResponseDto(
                reservationPackagesDetailsResponse.getPackageCode(),
                reservationPackagesDetailsResponse.getDescription(),
                reservationPackagesDetailsResponse.getUnitPrice(),
                reservationPackagesDetailsResponse.getTotalQuantity(),
                reservationPackagesDetailsResponse.getPackageGroup(),
                reservationPackagesDetailsResponse.getComputedPrice(),
                reservationPackagesDetailsResponse.getGrossPrice(),
                reservationPackagesDetailsResponse.getVatTax(),
                reservationPackagesDetailsResponse.getStartDate(),
                reservationPackagesDetailsResponse.getEndDate())).toList();
    BillingResponseDto billingResponseDto = null;
    if (reservation.getBilling() != null) {
      billingResponseDto = buildAddressResponse(reservation.getBilling());
    }
    final ReservationPaymentCardTypeDto reservationPaymentCardTypeDto = ReservationPaymentCardTypeDto.builder()
        .cardNumberMasked(reservation.getPaymentCard().getCardNumberMasked())
        .token(reservation.getPaymentCard().getToken())
        .expirationDate(reservation.getPaymentCard().getExpirationDate())
        .cardType(reservation.getPaymentCard().getCardType())
        .paymentMethod(reservation.getPaymentCard().getPaymentMethod())
        .cardHolderName(reservation.getPaymentCard().getCardHolderName())
        .folioView(reservation.getPaymentCard().getFolioView())
        .build();

    ReservationOverrideReasonsDto reservationOverrideReasonsDto = null;
    if (reservation.getReservationOverrideReasons() != null) {
      reservationOverrideReasonsDto = ReservationOverrideReasonsDto.builder()
          .reasonCode(reservation.getReservationOverrideReasons().getReasonCode())
          .reasonName(reservation.getReservationOverrideReasons().getReasonName())
          .callerName(reservation.getReservationOverrideReasons().getCallerName())
          .managerName(reservation.getReservationOverrideReasons().getManagerName())
          .build();
    }
    GuaranteeDto guaranteeDto = GuaranteeDto.builder()
        .onHold(reservation.getGuarantee().getOnHold())
        .guaranteeCode(reservation.getGuarantee().getGuaranteeCode())
        .shortDescription(reservation.getGuarantee().getShortDescription())
        .build();

    ReservationEmailNotificationsDto reservationEmailNotificationsDto = ReservationEmailNotificationsDto
        .builder()
        .sendEmailInvoice(reservation.getReservationEmailNotifications().isSendEmailInvoice())
        .sendEmailConfirmation(reservation.getReservationEmailNotifications()
            .isSendEmailConfirmation())
        .build();
    ResCashieringTypeDto reservationCashiering = ResCashieringTypeDto.builder()
        .taxType(ReservationTaxTypeInfoDto.builder()
            .code(reservation.getCashiering().getTaxType().getCode())
            .build())
        .build();
    ReservationCompanyDto reservationCompany = this.toReservationCompanyDto(reservation.getReservationCompany());
    UserDefinedFieldsDto userDefinedFieldsDto = Optional.ofNullable(reservation.getUserDefinedFields())
        .map(UserDefinedFields::getCharacterUDFs)
        .filter(CollectionUtils::isNotEmpty)
        .map(characterUDFs -> UserDefinedFieldsDto.builder()
            .characterUDFs(characterUDFs.stream()
                .map(characterUDF -> CharacterUDFsDto.builder()
                    .name(characterUDF.getName())
                    .value(characterUDF.getValue())
                    .build())
                .toList())
            .build())
        .orElse(null);

    return new ReservationByIdDto(reservation.getReservationId(), reservationGuestList,
        reservationBookerDto, reservationCompany, roomStay, rateInfoDto, additionalGuestInfoDto,
        depositPoliciesDtos, billingResponseDto, reservationPackagesDetailsResponseDtos,
        reservationPaymentCardTypeDto, reservationOverrideReasonsDto,
        reservationOverrideReasonsDto != null, guaranteeDto, reservation.getReservationStatus(),
        reservationEmailNotificationsDto, reservation.getBalanceAmount(),
        reservation.getGdsReferenceNumber(), reservationCashiering, userDefinedFieldsDto,
        mapBookingAllowances(reservation), reservation.getOperaLinkedReservation(),
        mapDepositFolios(reservation), reservation.getPreCheckInStatus(),
        mapPreferences(reservation), mapAlerts(reservation));
  }

  private BillingResponseDto buildAddressResponse(BillingResponse billingResponse) {
    return Optional.ofNullable(billingResponse)
            .map(response -> {
              AddressResponseDto addressResponseDto = Optional.ofNullable(response.getAddress())
                      .map(address -> new AddressResponseDto(
                              address.getCompanyName(),
                              address.getCountryCode(),
                              address.getCityName(),
                              address.getLine1(),
                              address.getLine2(),
                              address.getLine3(),
                              address.getLine4(),
                              address.getPostalCode()
                      ))
                      .orElse(null);

              return new BillingResponseDto(
                      addressResponseDto,
                      response.getEmail(),
                      response.getFirstName(),
                      response.getLastName(),
                      response.getTelephone(),
                      response.getLandline(),
                      response.getTitle()
              );
            })
            .orElse(null);
  }

  private BookingAllowancesResponseDto mapBookingAllowances(ReservationById reservation) {
    List<BookingAllowanceDto> bookingAllowancesDto = new ArrayList<>();

    if (Objects.nonNull(reservation.getBookingAllowancesResponse())) {
      reservation.getBookingAllowancesResponse().getBookingAllowances().forEach(bookingAllowance -> {
        var bookingAllowanceDto = BookingAllowanceDto.builder()
            .allowance(bookingAllowance.getAllowance())
            .budget(bookingAllowance.getBudget())
            .build();
        bookingAllowancesDto.add(bookingAllowanceDto);

      });
      return BookingAllowancesResponseDto.builder()
          .bookingAllowances(bookingAllowancesDto)
          .businessNotes(reservation.getBookingAllowancesResponse().getBusinessNotes())
          .build();
    }

    return new BookingAllowancesResponseDto();
  }

  private DepositFoliosResponseDto mapDepositFolios(ReservationById reservationById) {

    List<DepositFolioDto> depositFoliosDto = new ArrayList<>();
    List<DepositFolioChargeDto> chargesDto = new ArrayList<>();

    if (Objects.nonNull(reservationById.getDepositFoliosResponse())) {
      reservationById.getDepositFoliosResponse().getDepositFolios().forEach(depositFolio -> {

        depositFolio.getCharges().forEach(charge -> {
          var chargeDto = DepositFolioChargeDto.builder()
              .transactionCode(charge.getTransactionCode())
              .quantity(charge.getQuantity())
              .reference(charge.getReference())
              .currencyAmount(CurrencyAmountDto.builder()
                  .amount(charge.getCurrencyAmount().getAmount())
                  .currencyCode(charge.getCurrencyAmount().getCurrencyCode())
                  .build())
              .build();
          chargesDto.add(chargeDto);
        });
        var depositFolios = DepositFolioDto.builder()
            .reservationId(depositFolio.getReservationId())
            .hotelId(depositFolio.getHotelId())
            .vatRegion(depositFolio.getVatRegion())
            .charges(chargesDto)
            .build();
        depositFoliosDto.add(depositFolios);
      });
    }
    return DepositFoliosResponseDto.builder()
        .depositFolios(depositFoliosDto)
        .build();
  }

  private ReservationByIdGuestAddressDto getReservationByIdGuestAddressDto(GuestAddress guestAddress) {
    return ReservationByIdGuestAddressDto.builder()
        .addressType(guestAddress.getAddressType())
        .addressLine1(guestAddress.getAddressLine1())
        .addressLine2(guestAddress.getAddressLine2())
        .addressLine3(guestAddress.getAddressLine3())
        .addressLine4(guestAddress.getAddressLine4())
        .cityName(guestAddress.getCityName())
        .postalCode(guestAddress.getPostalCode())
        .countryCode(guestAddress.getCountryCode())
        .companyName(guestAddress.getCompanyName())
        .build();
  }

  private ReservationByIdGuestAddressDto getReservationByIdGuestHomeAddressDto(
      GuestAddress guestAddress) {
    return ReservationByIdGuestAddressDto.builder()
        .addressType(guestAddress.getAddressType())
        .addressLine1(guestAddress.getAddressLine1())
        .addressLine2(guestAddress.getAddressLine2())
        .addressLine3(guestAddress.getAddressLine3())
        .addressLine4(guestAddress.getAddressLine4())
        .cityName(guestAddress.getCityName())
        .postalCode(guestAddress.getPostalCode())
        .countryCode(guestAddress.getCountryCode())
        .companyName(guestAddress.getCompanyName())
        .addressId(guestAddress.getAddressId())
        .build();
  }

  private StayingGuestAdditionalDetailsDto getAdditionalDetailsDto(
      StayingGuestAdditionalDetails additionalDetails) {
    if (ObjectUtils.isNotEmpty(additionalDetails)) {
      return StayingGuestAdditionalDetailsDto.builder()
          .dob(additionalDetails.getDob())
          .nationality(additionalDetails.getNationality())
          .passportNumber(additionalDetails.getPassportNumber())
          .build();
    }
    return null;
  }

  private RateInfoDto getRateInfoDto(ReservationById reservation) {
    RateInfoDto rateInfoDto = new RateInfoDto();
    if (reservation.getRateInfo() != null) {
      var rateInfoDetailsDto = reservation.getRateInfo().getSummary()
          .getDetails().stream().map(detail -> new RateInfoDetailsDto(
              detail.getSummaryDate(), detail.getRevenue(), detail.getPackageDetails(), detail.getTax(),
              detail.getGross(), detail.getNet(), detail.getRatePlanCode(), detail.getCurrencyCode()))
          .toList();
      var rateInfoSummary = reservation.getRateInfo().getSummary();
      var rateInfoSummaryDto = new RateInfoSummaryDto(rateInfoDetailsDto, rateInfoSummary.getGross(),
          rateInfoSummary.getNet(), rateInfoSummary.getDeposit(), rateInfoSummary.getTotalCostOfStay(),
          rateInfoSummary.getOutStandingCostOfStay(), rateInfoSummary.getGuestPay(), rateInfoSummary.getRouting(),
          rateInfoSummary.getCurrencyCode(), rateInfoSummary.getStart(), rateInfoSummary.getEnd(),
          rateInfoSummary.getHasSuppressedRate());
      rateInfoDto.setSummary(rateInfoSummaryDto);
    }
    return rateInfoDto;
  }

  private BigDecimal computeCityTaxAmountPerRoom(List<RatePerNightDto> ratePerNightDtoList) {
    return ratePerNightDtoList.stream().filter(Objects::nonNull)
        .map(RatePerNightDto::getCityTaxPerNight).reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private List<ReservationAlertsDto> mapAlerts(ReservationById reservation) {
    List<ReservationAlertsDto> alerts = null;
    if (null != reservation.getAlerts()) {
      alerts = reservation.getAlerts().stream().map(
          alert -> new ReservationAlertsDto(alert.getCode(),
              alert.getArea(), alert.getType(), alert.getId(), alert.getDescription())).toList();
    }
    return alerts;
  }

  private List<ReservationEventPreferenceDto> mapPreferences(
      ReservationById reservation) {
    List<ReservationEventPreferenceDto> preferences = null;
    if (null != reservation.getPreferences()) {
      preferences = reservation.getPreferences().stream().map(
          preference -> new ReservationEventPreferenceDto(preference.getCode(),
              preference.getPreferenceType())).toList();
    }
    return preferences;
  }

  private Boolean isSameAsBooker(String guestProfileId, String bookerProfileId) {
    if (guestProfileId == null || bookerProfileId == null) {
      return false;
    }
    return guestProfileId.equals(bookerProfileId);
  }
}
