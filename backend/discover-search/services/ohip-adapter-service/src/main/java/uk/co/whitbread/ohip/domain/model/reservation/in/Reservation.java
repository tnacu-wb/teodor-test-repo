package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class Reservation implements SelfValidation<Reservation> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String arrival;
  @NotEmpty
  private String departure;
  @NotEmpty
  private String externalReferenceId;
  @NotNull
  private RoomRateReservation roomRates;
  @NotNull
  @Min(1)
  @Max(2)
  private Integer adults;
  @Min(0)
  @Max(3)
  private Integer children;
  private Boolean cotRequired;
  private String sourceCode;
  private String gdsReferenceNumber;
  private String bookingNotes;
  private String distributionUsername;
  private String distributionIATANumber;
  private String userAccountId;
  private String companyAccountId;
  private String bookingType;
  private LeadGuest leadGuest;
  private String ccuiUserEmailId;
  private String operaCompanyId;
  private List<ReservationPackages> reservationPackages;

  public Reservation(String hotelId, String arrival, String departure,
      String externalReferenceId,
      RoomRateReservation roomRates, Integer adults, Integer children,
      Boolean cotRequired, String sourceCode, String gdsReferenceNumber, String bookingNotes,
      String distributionUsername, String distributionIATANumber,
      String userAccountId, String companyAccountId, String bookingType, LeadGuest leadGuest,
      String ccuiUserEmailId, String operaCompanyId, List<ReservationPackages> reservationPackages) {

    this.hotelId = hotelId;
    this.arrival = arrival;
    this.departure = departure;
    this.externalReferenceId = externalReferenceId;
    this.roomRates = roomRates;
    this.adults = adults;
    this.children = Optional.ofNullable(children).orElse(0);
    this.cotRequired = Optional.ofNullable(cotRequired).orElse(false);
    this.sourceCode = sourceCode;
    this.gdsReferenceNumber = gdsReferenceNumber;
    this.bookingNotes = bookingNotes;
    this.distributionUsername = distributionUsername;
    this.distributionIATANumber = distributionIATANumber;
    this.userAccountId = userAccountId;
    this.companyAccountId = companyAccountId;
    this.bookingType = bookingType;
    this.leadGuest = leadGuest;
    this.ccuiUserEmailId = ccuiUserEmailId;
    this.operaCompanyId = operaCompanyId;
    this.reservationPackages = reservationPackages;
    this.validateSelf();
  }
}
