package uk.co.whitbread.content.infrastructure.rest.client.footer.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.content.domain.model.footer.in.FooterRequest;
import uk.co.whitbread.content.infrastructure.rest.client.footer.model.out.FooterRequestAemDto;

@Mapper(componentModel = "spring")
public interface FooterRequestMapper {

  FooterRequestAemDto toDto(FooterRequest footerRequest);
}
