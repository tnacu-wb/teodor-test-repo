package uk.co.whitbread.cdh.infrastructure.rest.client.spending.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSpendResponse {

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;
  
  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;
  
  @JsonProperty("Year")
  private Integer year;
  
  @JsonProperty("Month")
  private Integer month;
  
  @JsonProperty("NoOfBookings")
  private Integer noOfBookings;
  
  @JsonProperty("BookingValue")
  private Double bookingValue;
  
  @JsonProperty("BookingCurrency")
  private String bookingCurrency;
}
