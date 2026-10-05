package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BBStaysRequestV2 extends BBStaysRequest{
  @Min(1)
  private Integer pageIndex;
  @Min(1)
  private Integer pageSize;
}
