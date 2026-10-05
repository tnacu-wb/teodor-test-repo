package uk.co.whitbread.basket.infrastructure.repository.model;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;
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
public class BasketItemEntity {
  @NotEmpty
  private String type;
  @NotEmpty
  private String sourceId;
  private String reqAction;
  private Integer ack;
  private Map<String, String> details;
  private Boolean hasOccupancySup;
}
