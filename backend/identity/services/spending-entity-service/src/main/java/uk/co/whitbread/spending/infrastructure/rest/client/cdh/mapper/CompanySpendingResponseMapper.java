package uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.shared.cdh.model.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.CompanySpending;

@Mapper(componentModel = "spring")
public interface CompanySpendingResponseMapper {

  List<CompanySpending> toDto(List<CompanySpendingResponse> companySpendingResponse);

  CompanySpending toDto(CompanySpendingResponse companySpendingResponse);

}
