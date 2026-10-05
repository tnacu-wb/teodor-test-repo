package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.payapp.domain.model.out.ApplicationDetails;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;

@Mapper(componentModel = "spring")
public interface ApplicationDetailsMapper {

  ApplicationDetails toModel(ApplicationResponse applicationResponse);

}
