package uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.index.header.data.out.IndexHeaderData;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.IndexHeaderDataDto;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface IndexHeaderDataDtoMapper {

  @Mapping(source = "content.form.childrenHelperText", target = "form.childrenHelperText")
  @Mapping(source = "content.form.adultsHelperText", target = "form.adultsHelperText")
  @Mapping(source = "content.form.includeCot", target = "form.includeCot")
  @Mapping(source = "content.form.cotLimit", target = "form.cotLimit")
  @Mapping(source = "content.form.removeRoom", target = "form.removeRoom")
  @Mapping(source = "content.form.checkout", target = "form.checkout")
  @Mapping(source = "content.form.roomType", target = "form.roomType")
  @Mapping(source = "content.form.where", target = "form.where")
  @Mapping(source = "content.form.datePicker.reset", target = "datePicker.reset")
  @Mapping(source = "content.form.datePicker.invalidDate", target = "datePicker.invalidDate")
  @Mapping(source = "content.form.datePicker.checkOut", target = "datePicker.checkOut")
  @Mapping(source = "content.form.invalidLocation", target = "form.invalidLocation")
  @Mapping(source = "content.global.addRoom", target = "content.global.addRoom")
  @Mapping(source = "content.results.notifications.emp01groupBookingMessage",
      target = "results.notifications.emp01groupBookingMessage")
  @Mapping(source = "content.results.notifications.groupBookingHeader",
      target = "results.notifications.groupBookingHeader")
  @Mapping(source = "content.results.notifications.groupBookingMessage",
      target = "results.notifications.groupBookingMessage")
  @Mapping(source = "content.results.notifications.ccuiGroupBookingMessage",
      target = "results.notifications.ccuiGroupBookingMessage")
  @Mapping(source = "content.results.notifications.noResults", target = "results.notifications.noResults")
  @Mapping(source = "content.results.notifications.availabilitiesErrorMessage",
      target = "results.notifications.availabilitiesErrorMessage")
  @Mapping(source = "content.results.notifications.errorTitle", target = "results.notifications.errorTitle")
  @Mapping(source = "content.results.notifications.groupBookingFormPageMessage",
          target = "results.notifications.groupBookingFormPageMessage")
  @Mapping(source = "config.roomCodes.doubleValue", target = "config.roomCodes.doubleValue")
  @Mapping(source = "config.roomCodes.family", target = "config.roomCodes.family")
  @Mapping(source = "config.roomCodes.accessible", target = "config.roomCodes.accessible")
  @Mapping(source = "config.roomCodes.single", target = "config.roomCodes.single")
  @Mapping(source = "config.roomCodes.twin", target = "config.roomCodes.twin")
  @Mapping(source = "config.authentication.accountLinks", target = "config.authentication.accountLinks")
  @Mapping(source = "content.form.invalidPastDate", target = "form.invalidPastDate")
  @Mapping(source = "content.form.invalidDate", target = "form.invalidDate")
  @Mapping(source = "content.form.invalidNights", target = "form.invalidNights")
  @Mapping(source = "content.form.invalidRooms", target = "form.invalidRooms")
  @Mapping(source = "content.form.snowdropError", target = "form.snowdropError")
  @Mapping(source = "content.form.snowdropErrorRetry", target = "form.snowdropErrorRetry")
  @Mapping(source = "content.form.invalidFutureDate", target = "form.invalidFutureDate")
  @Mapping(source = "content.announcement.text", target = "announcement.text")
  @Mapping(source = "content.announcement.type", target = "announcement.type")
  @Mapping(source = "content.announcement.browserCompatibilityMessage",
      target = "announcement.browserCompatibilityMessage")
  @Mapping(source = "content.contactBanner.text", target = "contactBanner.text")
  @Mapping(source = "content.contactBanner.type", target = "contactBanner.type")
  @Mapping(source = "content.contactBanner.date", target = "contactBanner.date")
  @Mapping(source = "content.contactBanner.enabledPages", target = "contactBanner.enabledPages")
  @Mapping(source = "content.form.findBookingTitle", target = "form.findBookingTitle")
  @Mapping(source = "content.form.findBookingDescription", target = "form.findBookingDescription")
  @Mapping(source = "content.form.bookingReferenceLabel", target = "form.bookingReferenceLabel")
  @Mapping(source = "content.form.bookingSurnameLabel", target = "form.bookingSurnameLabel")
  @Mapping(source = "content.form.arrivalDateLabel", target = "form.arrivalDateLabel")
  @Mapping(source = "content.form.invalidReference", target = "form.invalidReference")
  @Mapping(source = "content.form.invalidSurname", target = "form.invalidSurname")
  @Mapping(source = "content.form.bookingInvalid", target = "form.bookingInvalid")
  @Mapping(source = "content.form.searchBookingError", target = "form.searchBookingError")
  @Mapping(source = "config.amazonChat.amazonChatUrl", target = "config.amazonChat.amazonChatUrl")
  @Mapping(source = "config.amazonChat.chatIcon", target = "config.amazonChat.chatIcon")
  @Mapping(source = "config.amazonChat.amazonAuthUrl", target = "config.amazonChat.amazonAuthUrl")
  @Mapping(source = "config.amazonChat.amazonChatId", target = "config.amazonChat.amazonChatId")
  @Mapping(source = "config.amazonChat.amazonChatSnippetId", target = "config.amazonChat.amazonChatSnippetId")
  @Mapping(source = "config.amazonChat.chatBotStatus", target = "config.amazonChat.chatBotStatus")
  @Mapping(source = "config.amazonChat.webChatEnabledPages", target = "config.amazonChat.webChatEnabledPages")
  IndexHeaderDataDto toDtoModel(IndexHeaderData indexHeaderData);
}
