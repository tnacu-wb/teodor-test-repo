package uk.co.whitbread.payments.model.card;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

  private String line1;
  private String line2;
  private String line3;
  private String line4;
  private String line5;

  private String postCode;
  private String countryCode;

  private String type;
  private String companyName;
}