package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPreference {

  @JsonProperty("PremierBreakfast")
  private boolean premierBreakfast;
  @JsonProperty("ContinentalBreakfast")
  private boolean continentalBreakfast;
  @JsonProperty("MealDeal")
  private boolean mealDeal;
  @JsonProperty("ElectronicInvoiceRequired")
  private boolean electronicInvoiceRequired;
  @JsonProperty("PreselectWifi")
  private boolean preselectWifi;
  @JsonProperty("Reason")
  private String reason;
  @JsonProperty("RoomRequirements")
  private RoomRequirements roomRequirements;
}
