package uk.co.whitbread.digitalkey.infrastructure.rest.controller.digitalkey.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alerts {

  private String code;
  private String area;
  private String type;
  private String id;
  private String description;

}
