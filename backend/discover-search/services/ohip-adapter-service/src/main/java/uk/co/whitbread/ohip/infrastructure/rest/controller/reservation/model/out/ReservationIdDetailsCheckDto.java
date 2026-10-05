package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.checkin.out.Cashiering;
import uk.co.whitbread.ohip.domain.model.checkin.out.SourceOfSale;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackages;
import uk.co.whitbread.ohip.domain.model.reservation.out.AdditionalGuestInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.Housekeeping;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationIdDetailsCheckDto {

  private List<UniqueIdTypeDto> reservationIdList;
  private SourceOfSale sourceOfSale;
  private RoomStayDto roomStay;
  private List<ReservationGuest> reservationGuests;
  private AdditionalGuestInfo additionalGuestInfo;
  private List<ReservationPackages> reservationPackages;
  private ReservationPoliciesDto reservationPolicies;
  private List<ReservationPaymentMethodDto> reservationPaymentMethods;
  private Cashiering cashiering;
  private Housekeeping housekeeping;
  private Boolean extSystemSync;
  private String hotelId;
  private Boolean roomStayReservation;
  private String reservationStatus;
  private String computedReservationStatus;
  private Boolean walkIn;
  private Boolean printRate;
  private String createDateTime;
  private String creatorId;
  private String lastModifyDateTime;
  private String lastModifierId;
  private String createBusinessDate;
  private Boolean preRegistered;
  private Boolean upgradeEligible;
  private Boolean allowAutoCheckin;
  private Boolean hasOpenFolio;
  private Boolean allowMobileCheckout;
  private Boolean allowMobileViewFolio;
  private Boolean allowPreRegistration;
  private Boolean optedForCommunication;
  private UserDefinedFieldsDto userDefinedFields;
}