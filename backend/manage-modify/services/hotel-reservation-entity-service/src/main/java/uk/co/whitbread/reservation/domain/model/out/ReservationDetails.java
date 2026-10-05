package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackages;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetails {

  private List<UniqueIDType> reservationIdList;
  private RoomStay roomStay;
  private List<ReservationGuest> reservationGuests;
  private List<ReservationPackages> reservationPackages;
  private ReservationPolicies reservationPolicies;
  private List<ReservationPaymentMethod> reservationPaymentMethods;
  private AdditionalGuestInfoResponse additionalGuestInfo;
  private String hotelId;
  private String hotelName;
  private boolean roomStayReservation;
  private String reservationStatus;
  private SourceOfSale sourceOfSale;
  private Cashiering cashiering;
  private boolean extSystemSync;
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
  private UserDefinedFields userDefinedFields;

}