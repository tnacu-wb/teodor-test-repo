package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class Reservation implements SelfValidation<Reservation> {

  @NotNull
  private String hotelId;
  @NotNull
  private String arrival;
  @NotNull
  private String departure;
  private String externalReferenceId;
  @NotEmpty
  @Valid
  private RoomRate roomRates;
  @Min(1)
  @Max(2)
  private Integer adultsNumber;
  @Min(0)
  @Max(3)
  private Integer childrenNumber;
  private Boolean cotRequired;
  private String bookingNotes;
  private String gdsReferenceNumber;
  private String distributionUsername;
  private String distributionIATANumber;
  private String userAccountId;
  private String companyAccountId;
  private String bookingType;
  private LeadGuest leadGuest;
  private String ccuiUserEmailId;
  private String operaCompanyId;
  private List<ReservationPackages> reservationPackages;

  public Reservation(String hotelId, String arrival, String departure, String externalReferenceId,
      RoomRate roomRates, Integer adultsNumber, Integer childrenNumber, Boolean cotRequired,
      String bookingNotes, String gdsReferenceNumber, String distributionUsername,
      String distributionIATANumber, String userAccountId, String companyAccountId,
      String bookingType, LeadGuest leadGuest, String ccuiUserEmailId, String operaCompanyId) {

    this.hotelId = hotelId;
    this.arrival = arrival;
    this.departure = departure;
    this.externalReferenceId = externalReferenceId;
    this.roomRates = roomRates;
    this.adultsNumber = adultsNumber;
    this.childrenNumber = childrenNumber;
    this.cotRequired = cotRequired;
    this.bookingNotes = bookingNotes;
    this.gdsReferenceNumber = gdsReferenceNumber;
    this.distributionUsername = distributionUsername;
    this.distributionIATANumber = distributionIATANumber;
    this.userAccountId = userAccountId;
    this.companyAccountId = companyAccountId;
    this.bookingType = bookingType;
    this.leadGuest = leadGuest;
    this.ccuiUserEmailId = ccuiUserEmailId;
    this.operaCompanyId = operaCompanyId;
    this.validateSelf();
  }

  public void setReservationPackages(
      List<ReservationPackages> reservationPackages) {
    this.reservationPackages = reservationPackages;
  }

  public List<ReservationPackages> getReservatopnPackages() {
    return this.reservationPackages;
  }
}
