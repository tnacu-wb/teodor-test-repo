package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import lombok.Data;

@Data
public class BookingPreferenceDto {
  private Long foodPreference;
  private Boolean preselectWifi;
  private String reason;
  private RoomRequirementsDto roomRequirements;
  private Boolean wantSmsConfirmations;
}
