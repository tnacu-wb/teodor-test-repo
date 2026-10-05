package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepositFoliosResponseDto {

  private List<DepositFolioDto> depositFolios;

}