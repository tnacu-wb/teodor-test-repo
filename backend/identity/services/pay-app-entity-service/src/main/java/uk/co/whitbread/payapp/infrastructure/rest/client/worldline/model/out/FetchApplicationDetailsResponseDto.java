package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchApplicationDetailsResponseDto {

  private String responseCode;
  private AppDetailsDataDto data;
  private List<WLErrorDto> errors;

}
