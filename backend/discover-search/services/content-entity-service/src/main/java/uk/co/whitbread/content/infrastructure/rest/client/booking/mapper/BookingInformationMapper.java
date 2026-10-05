package uk.co.whitbread.content.infrastructure.rest.client.booking.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.content.domain.model.booking.out.BookingInformation;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.AemBookingInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.booking.model.in.MessageDto;

@Mapper(componentModel = "spring", uses = {
    BookingDonationMapper.class, PrivacyPolicyMapper.class, PromotionPanelMapper.class,
    ItemMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BookingInformationMapper {

  @Mapping(source = "donation", target = "bookingDonation")
  @Mapping(source = "infoMessage", target = "infoMessages")
  @Mapping(source = "upsellitemsConfiguration", target = "upsellItems")
  BookingInformation toDomainModel(AemBookingInformationDto aemBookingInformationDto);

  default List<String> toMessageModel(List<MessageDto> messages) {
    return messages == null ? null : messages.stream().map(MessageDto::getMessage).toList();
  }

}

