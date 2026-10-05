package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.in;

import lombok.Data;

@Data
public class AxpGenerateOtpRequestDto {
  private String type;
  private String destination;
}
