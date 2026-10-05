package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.reservation.domain.model.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.CompanyQuestionAndAnswerDetailsRequestDto;

@Mapper(componentModel = "spring")
public interface CompanyQuestionAndAnswerRequestMapper {
  CompanyQuestionAndAnswerDetailsRequest toModel(CompanyQuestionAndAnswerDetailsRequestDto dto);
}
