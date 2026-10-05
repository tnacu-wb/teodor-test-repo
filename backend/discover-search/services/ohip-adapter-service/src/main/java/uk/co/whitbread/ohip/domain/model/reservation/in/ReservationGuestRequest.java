package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ReservationGuestRequest implements SelfValidation<ReservationGuestRequest> {

  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String reasonForStay;
  @NotNull
  private BookerDetails booker;
  @NotEmpty
  private List<StayingGuest> stayingGuests;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private String companyAccountId;
  private String userAccountId;
  private String bookingType;
  private String bookerProfileId;
  private String companyProfileId;
  @Default
  private Boolean preCheckIn = false;

  public ReservationGuestRequest(String hotelId, String reasonForStay, BookerDetails booker,
      List<StayingGuest> stayingGuests, Boolean sendEmailConfirmation, Boolean sendEmailInvoice,
      String companyAccountId, String userAccountId, String bookingType, String bookerProfileId,
      String companyProfileId, Boolean preCheckIn) {
    this.hotelId = hotelId;
    this.reasonForStay = reasonForStay;
    this.booker = booker;
    this.stayingGuests = stayingGuests;
    this.sendEmailConfirmation = sendEmailConfirmation;
    this.sendEmailInvoice = sendEmailInvoice;
    this.companyAccountId = companyAccountId;
    this.userAccountId = userAccountId;
    this.bookingType = bookingType;
    this.bookerProfileId = bookerProfileId;
    this.companyProfileId = companyProfileId;
    this.preCheckIn = preCheckIn;
    this.validateSelf();
  }
}