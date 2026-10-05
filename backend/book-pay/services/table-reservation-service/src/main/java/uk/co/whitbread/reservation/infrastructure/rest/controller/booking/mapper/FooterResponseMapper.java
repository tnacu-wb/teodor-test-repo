package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.aem.AemFooterResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.Column;
import uk.co.whitbread.reservation.domain.model.out.aem.LinkItem;
import uk.co.whitbread.reservation.domain.model.out.aem.SocialMediaIcon;
import uk.co.whitbread.reservation.domain.model.out.aem.Tab;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemFooterResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ColumnDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.LinkItemDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.SocialMediaIconDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.TabDto;

@Mapper(componentModel = "spring")
public interface FooterResponseMapper {

  default AemFooterResponseDto toDto(AemFooterResponse aemFooterResponse) {
    if (aemFooterResponse == null) {
      return null;
    }
    return AemFooterResponseDto.builder()
        .legalCopyRightLabel(aemFooterResponse.getLegalCopyRightLabel())
        .copyrightInfo(aemFooterResponse.getCopyrightInfo())
        .socialMediaIcons(toSocialMediaIconsDto(aemFooterResponse.getSocialMediaIcons()))
        .tabs(toTabsDto(aemFooterResponse.getTabs()))
        .build();

  }

  default List<SocialMediaIconDto> toSocialMediaIconsDto(List<SocialMediaIcon> socialMediaIcons) {
    if (socialMediaIcons == null) {
      return new ArrayList<>();
    }
    return socialMediaIcons
        .stream()
        .map(socialMediaIcon -> SocialMediaIconDto.builder()
            .label(socialMediaIcon.getLabel())
            .visible(socialMediaIcon.isVisible())
            .linkSrc(socialMediaIcon.getLinkSrc())
            .build())
        .toList();

  }

  default List<TabDto> toTabsDto(List<Tab> tab) {
    if (tab == null) {
      return new ArrayList<>();
    }
    return tab.stream()
        .map(tab1 -> TabDto.builder()
            .name(tab1.getName())
            .columns(toColumnsDto(tab1.getColumns()))
            .build())
        .toList();
  }

  default List<ColumnDto> toColumnsDto(List<Column> column) {
    if (column == null) {
      return new ArrayList<>();
    }
    return column.stream()
        .map(column1 -> ColumnDto.builder()
            .name(column1.getName())
            .linkItems(toLinkItemsDto(column1.getLinkItems()))
            .build())
        .toList();

  }

  default List<LinkItemDto> toLinkItemsDto(List<LinkItem> linkItem) {
    if (linkItem == null) {
      return new ArrayList<>();
    }
    return linkItem
        .stream()
        .map(linkItem1 -> LinkItemDto.builder()
            .openInNewTab(linkItem1.isOpenInNewTab())
            .linkSrc(linkItem1.getLinkSrc())
            .name(linkItem1.getName())
            .build())
        .toList();
  }


}
