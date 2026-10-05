package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UniqueIDTypeDto {

  private String id;
  private String type;
}
