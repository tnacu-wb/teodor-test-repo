package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareAppResponseDto {

  private int status;
  private String message;
}
