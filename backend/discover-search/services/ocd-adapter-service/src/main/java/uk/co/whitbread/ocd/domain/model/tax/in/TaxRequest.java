package uk.co.whitbread.ocd.domain.model.tax.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxRequest {

  private String hotelId;

  private String arrivalDate;

  private String departureDate;

  private Integer adults;

  private String ratePlanCode;

  private String roomType;

}
