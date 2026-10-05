package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.aem.AemHeaderResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.LinkItem;
import uk.co.whitbread.reservation.domain.model.out.aem.NavbarItem;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemHeaderResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LinkItemDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.NavbarItemDto;

@Mapper(componentModel = "spring")
public interface HeaderResponseMapper {

  default AemHeaderResponseDto toDto(AemHeaderResponse aemHeaderResponse) {
    if (aemHeaderResponse == null) {
      return null;
    }
    return AemHeaderResponseDto.builder()
        .logoSrc(aemHeaderResponse.getLogoSrc())
        .logoAlt(aemHeaderResponse.getLogoAlt())
        .locationSrc(aemHeaderResponse.getLocationSrc())
        .moreLocationName(aemHeaderResponse.getMoreLocationName())
        .navbar(toNavBarDto(aemHeaderResponse.getNavbar()))
        .homeAltSrc(aemHeaderResponse.getHomeAltSrc()).build();
  }

  default List<NavbarItemDto> toNavBarDto(List<NavbarItem> navbarItems) {
    if (navbarItems == null) {
      return new ArrayList<>();
    }
    return navbarItems.stream()
        .map(navbarItem -> NavbarItemDto.builder()
            .name(navbarItem.getName())
            .linkItems(toLinkItemsDto(navbarItem.getLinkItems()))
            .build())
        .toList();
  }

  default List<LinkItemDto> toLinkItemsDto(List<LinkItem> linkItems) {
    if (linkItems == null) {
      return new ArrayList<>();
    }
    return linkItems.stream()
        .map(linkItem -> LinkItemDto.builder()
            .linkSrc(linkItem.getLinkSrc())
            .name(linkItem.getName())
            .openInNewTab(linkItem.isOpenInNewTab())
            .build())
        .toList();
  }

}
