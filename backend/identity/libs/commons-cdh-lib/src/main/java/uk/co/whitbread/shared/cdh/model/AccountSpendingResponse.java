package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountSpendingResponse {

  @JsonProperty("PIBAAccountId")
  private String pibaAccountId;

  @JsonProperty("Year")
  private Integer year;

  @JsonProperty("Month")
  private Integer month;

  @JsonProperty("NoOfBookings")
  private Integer noOfBookings;

  @JsonProperty("BookingValue")
  private BigDecimal bookingValue;
}
