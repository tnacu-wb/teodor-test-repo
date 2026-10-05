package uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import uk.co.whitbread.shared.cdh.model.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;

@Mapper(componentModel = "spring")
public interface AccountSpendingResponseMapper {

  List<AccountSpending> toDto(List<AccountSpendingResponse> accountSpendingResponse);

  AccountSpending toDto(AccountSpendingResponse accountSpendingResponse);

}
