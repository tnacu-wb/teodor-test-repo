package uk.co.whitbread.basket.infrastructure.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class CcuiExtraItemsEntity {

  private AccountCompanyItemsEntity accountCompanyItems;
  private NonguaranteedItemsEntity nonguaranteedItems;
  private Boolean cardPresent;
  private String addressCompanyName;
}
