package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntroViewDto {

  private String title;
  private String description;
  @JsonProperty("manageButtonText")
  private String manageButtonText;
  @JsonProperty("acceptAllButtonText")
  private String acceptAllButtonText;
  private String necessaryOnlyButtonText;

}