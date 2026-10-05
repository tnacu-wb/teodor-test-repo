package uk.co.whitbread.employee.bulk.model.companyEmployees;

import lombok.Data;

@Data
public class BookingPreference {
  
  private Boolean continentalBreakfast;
  private Boolean electronicInvoiceRequired;
  private Boolean mealDeal;
  private Boolean premierBreakfast;
  private Boolean preselectWifi;
  private String reason;
  private RoomRequirements roomRequirements;
  
}
