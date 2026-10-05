package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.exception;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AxpErrorResponse {
  private String error;
}
