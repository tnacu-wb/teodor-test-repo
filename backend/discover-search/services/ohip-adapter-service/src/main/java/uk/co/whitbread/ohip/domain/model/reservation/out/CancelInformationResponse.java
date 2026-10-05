package uk.co.whitbread.ohip.domain.model.reservation.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CancelInformationResponse {

  private Boolean isCancellable;
}
