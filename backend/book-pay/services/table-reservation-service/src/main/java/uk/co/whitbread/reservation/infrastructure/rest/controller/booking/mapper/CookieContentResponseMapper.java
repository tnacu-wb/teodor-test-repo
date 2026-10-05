package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.reservation.domain.model.out.aem.AemCookieContentResponse;
import uk.co.whitbread.reservation.domain.model.out.aem.CookieGroup;
import uk.co.whitbread.reservation.domain.model.out.aem.CookiePolicies;
import uk.co.whitbread.reservation.domain.model.out.aem.IntroView;
import uk.co.whitbread.reservation.domain.model.out.aem.ManageView;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.AemCookieContentResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.CookieGroupDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.CookiePoliciesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.IntroViewDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.aemresponse.ManageViewDto;

@Mapper(componentModel = "spring")
public interface CookieContentResponseMapper {

  @Mapping(target = "cookiePolicies", expression =
      "java(toCookiePoliciesDto(aemCookieContentResponse.getCookiePolicies()))")
  AemCookieContentResponseDto toDto(AemCookieContentResponse aemCookieContentResponse);

  default CookiePoliciesDto toCookiePoliciesDto(CookiePolicies cookiePolicies) {
    if (cookiePolicies == null) {
      return null;
    }
    return CookiePoliciesDto.builder()
        .brand(cookiePolicies.getBrand())
        .manageView(toManageViewDto(cookiePolicies.getManageView()))
        .version(cookiePolicies.getVersion())
        .introView(toIntroViewDto(cookiePolicies.getIntroView()))
        .build();
  }

  default ManageViewDto toManageViewDto(ManageView manageView) {
    if (manageView == null) {
      return null;
    }
    return ManageViewDto.builder()
        .title(manageView.getTitle())
        .description(manageView.getDescription())
        .alwaysActiveText(manageView.getAlwaysActiveText())
        .saveSettingsButtonText(manageView.getSaveSettingsButtonText())
        .cookieGroup(toCookieGroupDto(manageView.getCookieGroup()))
        .build();
  }

  default IntroViewDto toIntroViewDto(IntroView introView) {
    if (introView == null) {
      return null;
    }
    return IntroViewDto.builder()
        .acceptAllButtonText(introView.getAcceptAllButtonText())
        .manageButtonText(introView.getManageButtonText())
        .description(introView.getDescription())
        .title(introView.getTitle())
        .necessaryOnlyButtonText(introView.getNecessaryOnlyButtonText())
        .build();
  }

  default List<CookieGroupDto> toCookieGroupDto(List<CookieGroup> cookieGroup) {
    if (cookieGroup == null) {
      return new ArrayList<>();
    }
    return cookieGroup.stream()
        .map(cookieGroup1 -> CookieGroupDto.builder()
            .cookieName(cookieGroup1.getCookieName())
            .toggleLabel(cookieGroup1.getToggleLabel())
            .alwaysActive(cookieGroup1.isAlwaysActive())
            .title(cookieGroup1.getTitle())
            .description(cookieGroup1.getDescription())
            .build())
        .toList();
  }
}