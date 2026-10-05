package uk.co.whitbread.basket.infrastructure.rest.controller.email.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.email.in.EmailRequest;
import uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in.EmailRequestDto;

@Mapper(componentModel = "spring")
public interface EmailMapper {

  EmailRequest toDomainModel(EmailRequestDto emailRequestDto);

}
