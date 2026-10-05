package uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper;

import java.util.Collections;
import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.out.events.Consent;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.events.Menus;
import uk.co.whitbread.reservation.domain.model.out.events.OrderMenu;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EventsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenusDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OrderMenuDto;

@Mapper(componentModel = "spring")
public interface EventResponseMapper {


  default EventsResponseDto toDto(EventsResponse eventsResponse) {
    if (eventsResponse == null) {
      return null;
    }

    return EventsResponseDto.builder()
        .adults(eventsResponse.getAdults())
        .areaId(eventsResponse.getAreaId())
        .areaName(eventsResponse.getAreaName())
        .bookingReference(eventsResponse.getBookingReference())
        .cancelLink(eventsResponse.getCancelLink())
        .children(eventsResponse.getChildren())
        .consent(toConsentDto(eventsResponse.getConsent()))
        .date(eventsResponse.getDate())
        .editLink(eventsResponse.getEditLink())
        .emailAddress(eventsResponse.getEmailAddress())
        .firstname(eventsResponse.getFirstname())
        .id(eventsResponse.getId())
        .lastname(eventsResponse.getLastname())
        .name(eventsResponse.getName())
        .occasionId(eventsResponse.getOccasionId())
        .occasionName(eventsResponse.getOccasionName())
        .siteId(eventsResponse.getSiteId())
        .siteName(eventsResponse.getSiteName())
        .specialRequest(eventsResponse.getSpecialRequest())
        .telephoneNumber(eventsResponse.getTelephoneNumber())
        .siteTimezone(eventsResponse.getSiteTimezone())
        .time(eventsResponse.getTime())
        .turnTimeMinutes(eventsResponse.getTurnTimeMinutes())
        .menus(toMenusDto(eventsResponse.getMenus()))
        .build();
  }

  default List<MenusDto> toMenusDto(List<Menus> menus) {
    if (menus.isEmpty()) {
      return Collections.emptyList();
    }
    return menus.stream().map(
        menu -> MenusDto.builder()
            .id(menu.getId())
            .name(menu.getName())
            .iOrderMenus(toOrderMenuDto(menu.getIOrderMenus())).build()).toList();
  }

  default List<OrderMenuDto> toOrderMenuDto(List<OrderMenu> orderMenus) {
    if (orderMenus.isEmpty()) {
      return Collections.emptyList();
    }
    return orderMenus.stream().map(
        orderMenu -> OrderMenuDto.builder()
            .id(orderMenu.getId()).build()
    ).toList();
  }

  default ConsentDto toConsentDto(Consent consent) {
    if (consent == null) {
      return null;
    }

    return ConsentDto.builder()
        .email(consent.isEmail())
        .phone(consent.isPhone())
        .sms(consent.isSms())
        .postal(consent.isPostal())
        .pushNotification(consent.isPushNotification())
        .profiling(consent.isProfiling())
        .privacyStatement(consent.isPrivacyStatement())
        .consentStatement(consent.isConsentStatement())
        .termsAndConditions(consent.isTermsAndConditions())
        .build();
  }
}