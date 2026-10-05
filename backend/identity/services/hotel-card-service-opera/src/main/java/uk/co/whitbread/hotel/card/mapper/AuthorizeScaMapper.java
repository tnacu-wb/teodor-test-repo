package uk.co.whitbread.hotel.card.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.card.generated.models.payments.AuthorizeScaRequestDto;
import uk.co.whitbread.hotel.card.model.AuthorizeScaRequest;

@Mapper(componentModel = "spring")
public interface AuthorizeScaMapper {

  @Mapping(target = "country", defaultValue = "gb")
  AuthorizeScaRequestDto toDto(AuthorizeScaRequest authorizeScaRequest);
}
