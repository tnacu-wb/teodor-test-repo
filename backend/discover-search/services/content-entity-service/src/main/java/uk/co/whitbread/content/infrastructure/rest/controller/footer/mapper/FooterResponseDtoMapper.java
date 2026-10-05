package uk.co.whitbread.content.infrastructure.rest.controller.footer.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.footer.out.FooterResponse;
import uk.co.whitbread.content.infrastructure.rest.controller.footer.model.out.FooterResponseDto;

@Mapper(componentModel = "spring")
public interface FooterResponseDtoMapper {

  FooterResponseDto toDto(FooterResponse footerResponse);
}
