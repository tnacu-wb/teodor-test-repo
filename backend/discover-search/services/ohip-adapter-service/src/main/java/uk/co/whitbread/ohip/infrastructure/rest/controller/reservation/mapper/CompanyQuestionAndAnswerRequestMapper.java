package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;

@Mapper(componentModel = "spring")
public interface CompanyQuestionAndAnswerRequestMapper {
  CompanyQuestionAndAnswerDetailsRequest toModel(CompanyQuestionAndAnswerDetailsRequestDto dto);
}
