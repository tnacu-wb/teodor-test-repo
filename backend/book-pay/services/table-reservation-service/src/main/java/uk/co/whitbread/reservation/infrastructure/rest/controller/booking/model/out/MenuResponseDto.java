package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class MenuResponseDto {

  private List<MenuRespDto> menus;

  public MenuResponseDto(List<MenuRespDto> menus) {
    this.menus = menus;
  }
}
