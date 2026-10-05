package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSpendingResponseDto {

  private List<AccountSpendingDto> accountSpendingDtoList;

}
