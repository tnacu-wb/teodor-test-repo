package uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper;

import org.mapstruct.Mapper;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TransactionDetails;

@Mapper(componentModel = "spring")
public interface TransactionDetailsResponseMapper {

  TransactionDetails toDto(TransactionDetailsResponse companySpendingResponse);
}
