package uk.co.whitbread.wallet.domain.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.wallet.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class WalletRequest implements SelfValidation<WalletRequest> {

  @NotNull
  private String reservationNumber;
  @NotNull
  private String arrivalDate;
  @NotNull
  private String lastName;
  private String language;
  private String country;
  private String channel;
  private boolean excludeBarcode;

  public WalletRequest(String reservationNumber, String arrivalDate, String lastName,
      String language, String country, String channel, boolean excludeBarcode) {
    this.reservationNumber = reservationNumber;
    this.arrivalDate = arrivalDate;
    this.lastName = lastName;
    this.language = language;
    this.country = country;
    this.channel = channel;
    this.excludeBarcode = excludeBarcode;
    this.validateSelf();
  }
}
