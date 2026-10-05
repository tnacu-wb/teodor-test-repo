package uk.co.whitbread.wallet.infrastructure.rest.controller.wallet.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WalletRequestDto {

  private String reservationNumber;
  private String arrivalDate;
  private String lastName;
  private String language;
  private String country;
  private String channel;
  private boolean excludeBarcode;
}
