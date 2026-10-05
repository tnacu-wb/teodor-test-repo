package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class BookingPreference {

  private Long foodPreference;
  private Boolean preselectWifi;
  private String reason;
  private RoomRequirements roomRequirements;
  private Boolean wantSmsConfirmations;
}
