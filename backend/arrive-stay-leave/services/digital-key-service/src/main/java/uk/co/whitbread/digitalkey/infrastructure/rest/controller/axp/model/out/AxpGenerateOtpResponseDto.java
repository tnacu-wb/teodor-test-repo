package uk.co.whitbread.digitalkey.infrastructure.rest.controller.axp.model.out;

import lombok.Data;

@Data
public class AxpGenerateOtpResponseDto {
  private boolean success;
  private String id;
}
