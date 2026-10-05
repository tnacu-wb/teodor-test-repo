package uk.co.whitbread.booking.infrastructure.rest.client.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.channel.BookingChannelValues;
import uk.co.whitbread.booking.domain.model.email.in.ResendConfirmationEmailRequest;
import uk.co.whitbread.booking.domain.model.email.in.ResendInvoiceEmailRequest;
import uk.co.whitbread.booking.domain.model.history.in.BookingRequest;
import uk.co.whitbread.booking.domain.model.information.in.BookingInfoRequest;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.channel.BartChannelCodeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.channel.LanguageCountryCodeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayConfirmationRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.email.in.EmailStayInvoiceRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.history.in.StayRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.in.StayInfoRequestDto;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;

@Mapper(componentModel = "spring")
public interface StayRequestMapper {

  StayRequestDto toDto(BookingRequest bookingRequest);

  StayRequestDto toDto(BookingRequest bookingRequest, EmployeeDetails employeeDetails);

  @Mapping(target = "arrival", expression = "java(bookingRequest.getArrival().substring(0, 10))")
  StayInfoRequestDto toDto(BookingInfoRequest bookingRequest);

  @Mapping(target = "destination", source = "email")
  @Mapping(target = "type", constant = "EMAIL")
  EmailStayConfirmationRequestDto toDto(ResendConfirmationEmailRequest request);

  @Mapping(target = "guestHistoryNumber", constant = "1234")
  @Mapping(target = "emailAddress", source = "request.email")
  @Mapping(target = "sessionId", source = "sessionId")
  @Mapping(target = "invoiceRecordNumber", source = "request.invoiceRecordNumber")
  EmailStayInvoiceRequestDto toDto(ResendInvoiceEmailRequest request, String sessionId);

  default String toBartStayCodeDto(BookingChannel bookingChannel) {
    if (BookingChannelValues.CHANNEL_BB.getValue().equalsIgnoreCase(bookingChannel.getChannel())) {
      return BartChannelCodeDto.CBT.getValue();
    }

    if (BookingChannelValues.CHANNEL_PI.getValue().equalsIgnoreCase(bookingChannel.getChannel())
        && LanguageCountryCodeDto.LANGUAGE_DE.getValue().equalsIgnoreCase(bookingChannel.getLanguage())
        && BookingChannelValues.SUBCHANNEL_PI.getValue().equalsIgnoreCase(bookingChannel.getSubchannel())) {
      return BartChannelCodeDto.WEB_DE.getValue();
    }

    return BartChannelCodeDto.WEB.getValue();
  }

}
