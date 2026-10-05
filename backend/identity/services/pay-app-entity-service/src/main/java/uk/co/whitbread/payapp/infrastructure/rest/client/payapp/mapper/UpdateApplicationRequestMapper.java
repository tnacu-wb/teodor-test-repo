package uk.co.whitbread.payapp.infrastructure.rest.client.payapp.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationRequest;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;

@Mapper(componentModel = "spring")
public interface UpdateApplicationRequestMapper {

  UpdateApplicationRequest toModel(ApplicationResponse applicationResponse);

}
