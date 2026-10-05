package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPreferenceDto {
  private boolean premierBreakfast;
  private boolean continentalBreakfast;
  private boolean mealDeal;
  private boolean electronicInvoiceRequired;
  private boolean preselectWifi;
  private String reason;
  private RoomRequirementsDto roomRequirements;
}
