package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentDetails;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CommentDetailsDto;

@Mapper(componentModel = "spring")
public interface CommentDetailsRequestMapper {

  CommentDetails toModel(CommentDetailsDto commentDetailsDto);

}
