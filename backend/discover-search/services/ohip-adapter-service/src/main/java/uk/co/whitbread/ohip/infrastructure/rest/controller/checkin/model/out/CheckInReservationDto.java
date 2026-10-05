package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInReservationDto {

  private List<CheckInReservationIdListDto> reservationIdList;
  private SourceOfSaleDto sourceOfSale;
  private CheckInRoomStayDto roomStay;
  private List<CheckInReservationGuestsDto> reservationGuests;
  private List<ReservationPackagesDto> reservationPackages;
  private List<String> reservationMemberships;
  private List<CheckInReservationPaymentMethodsDto> reservationPaymentMethods;
  private CashieringDto cashiering;
  private boolean extSystemSync;
  private UserDefinedFieldsDto userDefinedFields;
  private List<ReservationIndicatorsDto> reservationIndicators;
  private boolean roomStayReservation;
  private List<String> alerts;
  private boolean walkIn;
  private String hotelId;
  private boolean printRate;
  private boolean preRegistered;
  private String reservationStatus;
  private String computedReservationStatus;
  private boolean upgradeEligible;
  private String createBusinessDate;
  private String creatorId;
  private String createDateTime;
  private String lastModifierId;
  private String lastModifyDateTime;
  private boolean hasOpenFolio;
  private boolean allowAutoCheckin;
  private boolean optedForCommunication;
  private boolean allowPreRegistration;
  private boolean allowMobileViewFolio;
  private boolean allowMobileCheckout;
}
