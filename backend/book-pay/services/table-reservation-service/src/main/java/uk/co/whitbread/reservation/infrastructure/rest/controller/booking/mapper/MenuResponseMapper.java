package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResp;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuRespDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuResponseDto;

@Mapper(componentModel = "spring")
public interface MenuResponseMapper {

  @Mapping(target = "menus", expression = "java(toMenuResponseDto(menuResponse))")
  MenuResponseDto toDto(MenuResponse menuResponse);

  default List<MenuRespDto> toMenuResponseDto(MenuResponse menuResponse) {
    if (menuResponse == null || menuResponse.getMenus() == null) {
      return Collections.emptyList();
    }
    return menuResponse.getMenus().stream()
        .map(this::toMenuRespDto)
        .toList();

  }

  default MenuRespDto toMenuRespDto(MenuResp menuResp) {
    return MenuRespDto.builder()
        .id(menuResp.getId())
        .name(menuResp.getName())
        .description(menuResp.getDescription())
        .externalUrl(menuResp.getExternalUrl())
        .depositPerAdult(menuResp.getDepositPerAdult())
        .depositPerChild(menuResp.getDepositPerChild())
        .available(menuResp.isAvailable())
        .remainingEvents(menuResp.getRemainingEvents())
        .remainingCovers(menuResp.getRemainingCovers())
        .cardGuaranteeAlwaysRequired(menuResp.isCardGuaranteeAlwaysRequired())
        .cardGuaranteeMinCovers(menuResp.getCardGuaranteeMinCovers())
        .preAuthAlwaysRequired(menuResp.isPreAuthAlwaysRequired())
        .preAuthMinCovers(menuResp.getPreAuthMinCovers())
        .imageLinks(menuResp.getImageLinks())
        .iOrderMenus(menuResp.getIOrderMenus())
        .build();
  }

}
