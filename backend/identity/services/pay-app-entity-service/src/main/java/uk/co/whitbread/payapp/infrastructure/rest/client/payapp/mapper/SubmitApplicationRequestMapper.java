package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.in.SubmitApplicationRequest;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.model.in.SubmitApplicationRequestDetailsDto;

@Mapper(componentModel = "spring")
public interface SubmitApplicationRequestMapper {

  SubmitApplicationRequestDetailsDto toModel(SubmitApplicationRequest submitApplicationRequest);
}
