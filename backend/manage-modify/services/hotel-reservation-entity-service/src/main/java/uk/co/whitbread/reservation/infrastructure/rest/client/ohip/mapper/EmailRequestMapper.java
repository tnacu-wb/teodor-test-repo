package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.EmailRequestDto;
import uk.co.whitbread.reservation.domain.model.in.EmailRequest;

@Mapper(componentModel = "spring")
public interface EmailRequestMapper {
  @Mapping(target = "emailRequestType", source = "type")
  EmailRequestDto toDto(EmailRequest emailRequest);
}
