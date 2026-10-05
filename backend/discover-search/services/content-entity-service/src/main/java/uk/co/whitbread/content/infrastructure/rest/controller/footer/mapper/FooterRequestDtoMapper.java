package uk.co.whitbread.content.infrastructure.rest.controller.footer.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.in.FooterRequestDto;

@Mapper(componentModel = "spring")
public interface FooterRequestDtoMapper {

  FooterRequest toModel(FooterRequestDto footerRequestDto);
}
