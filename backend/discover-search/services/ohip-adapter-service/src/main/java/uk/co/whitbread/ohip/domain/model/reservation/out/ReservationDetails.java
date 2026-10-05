package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.checkin.out.Cashiering;
import uk.co.whitbread.ohip.domain.model.checkin.out.ReservationPackages;
import uk.co.whitbread.ohip.domain.model.checkin.out.ReservationPaymentMethod;
import uk.co.whitbread.ohip.domain.model.checkin.out.ReservationPolicies;
import uk.co.whitbread.ohip.domain.model.checkin.out.SourceOfSale;
import uk.co.whitbread.ohip.domain.model.checkin.out.UserDefinedFields;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.Housekeeping;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDetails {

  private List<UniqueIdType> reservationIdList;
  private RoomStay roomStay;
  private List<ReservationGuest> reservationGuest;
  private AdditionalGuestInfo additionalGuestInfo;
  private List<ReservationPackages> reservationPackages;
  private ReservationPolicies reservationPolicies;
  private List<ReservationPaymentMethod> reservationPaymentMethods;
  private Housekeeping housekeeping;
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