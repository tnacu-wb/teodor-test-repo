package uk.co.whitbread.basket.infrastructure.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import uk.co.whitbread.basket.domain.model.basket.out.BasketErrorType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class BasketErrorEntity {

  private String code;
  private String description;
  private BasketErrorType type;
}
