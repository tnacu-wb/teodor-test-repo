package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInReservation {

  private List<CheckInReservationIdList> reservationIdList;
  private SourceOfSale sourceOfSale;
  private CheckInRoomStay roomStay;
  private List<CheckInReservationGuests> reservationGuests;
  private List<ReservationPackages> reservationPackages;
  private List<String> reservationMemberships;
  private List<CheckInReservationPaymentMethods> reservationPaymentMethods;
  private Cashiering cashiering;
  private boolean extSystemSync;
  private UserDefinedFields userDefinedFields;
  private List<ReservationIndicators> reservationIndicators;
  private List<String> alerts;
  private String hotelId;
  private boolean roomStayReservation;
  private String reservationStatus;
  private String computedReservationStatus;
  private boolean walkIn;
  private boolean printRate;
  private String createDateTime;
  private String creatorId;
  private String lastModifyDateTime;
  private String lastModifierId;
  private String createBusinessDate;
  private boolean preRegistered;
  private boolean upgradeEligible;
  private boolean allowAutoCheckin;
  private boolean hasOpenFolio;
  private boolean allowMobileCheckout;
  private boolean allowMobileViewFolio;
  private boolean allowPreRegistration;
  private boolean optedForCommunication;

}
