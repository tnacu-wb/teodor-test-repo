package uk.co.whitbread.basket.domain.model.basket.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ReservationGuestRequest {

  @NotEmpty
  private String basketReference;
  @NotEmpty
  private String hotelId;
  @NotEmpty
  private String reasonForStay;
  private BookerDetails booker;
  @NotEmpty
  @Size(min = 1)
  @Valid
  private List<StayingGuest> stayingGuests;
  private Boolean sendEmailConfirmation;
  private Boolean sendEmailInvoice;
  private String companyAccountId;
  private String userAccountId;
  private String bookingType;
  private String bookerProfileId;
  private String companyProfileId;
}
