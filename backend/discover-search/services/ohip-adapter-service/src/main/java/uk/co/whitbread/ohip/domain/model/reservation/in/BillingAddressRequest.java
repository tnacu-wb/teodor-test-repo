package uk.co.whitbread.ohip.domain.model.reservation.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class BillingAddressRequest implements SelfValidation<BillingAddressRequest> {

  @NotEmpty
  private String hotelId;

  @Schema(example = "PAY_NOW")
  private String paymentOption;

  @NotEmpty
  @Valid
  @Schema
  private List<String> reservationIds;

  @NotNull
  private BookerDetails booker;

  private ProfileUpdateIndicators profileUpdateIndicators;

  private String channel;

  public BillingAddressRequest(String hotelId, String paymentOption, List<String> reservationIds,
      BookerDetails booker, ProfileUpdateIndicators profileUpdateIndicators, String channel) {
    this.hotelId = hotelId;
    this.paymentOption = paymentOption;
    this.reservationIds = reservationIds;
    this.booker = booker;
    this.profileUpdateIndicators = profileUpdateIndicators;
    this.channel = channel;

    this.validateSelf();
  }

  public boolean isUpdateGuestProfile() {
    return profileUpdateIndicators.updateGuestProfile();
  }

  public boolean isUpdateCompanyProfile() {
    return profileUpdateIndicators.updateCompanyProfile();
  }

  public boolean isUpdateContactProfile() {
    return profileUpdateIndicators.updateContactProfile();
  }
}