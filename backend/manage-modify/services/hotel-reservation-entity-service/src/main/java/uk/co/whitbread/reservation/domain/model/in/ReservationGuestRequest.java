package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder.Default;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class ReservationGuestRequest implements SelfValidation<ReservationGuestRequest> {

  private String companyId;
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
  @Default
  private Boolean preCheckIn = false;
  private Boolean updateProfileConsent;

  public String getCompanyId() {
    if (StringUtils.isNotBlank(companyId)) {
      return companyId;
    }
    if (StringUtils.isNotBlank(companyAccountId)) {
      return companyAccountId;
    }
    return null;
  }

  public String getCompanyAccountId() {
    return getCompanyId();
  }
}
