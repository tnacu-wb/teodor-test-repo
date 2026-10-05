package uk.co.whitbread.basket.infrastructure.rest.client.reservation.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.basket.domain.model.payments.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.CompanyQuestionAndAnswerDetailsDto;

@Mapper(componentModel = "spring")
public interface CompanyQuestionAndAnswerDetailsMapper {
  CompanyQuestionAndAnswerDetailsDto toDto(CompanyQuestionAndAnswerDetails companyQuestionAndAnswerDetails);
}
