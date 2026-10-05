package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.out;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBasketResponseDto {

  private String reference;
  private String bookingReference;
  private String createdAt;
  private String userId;
  private String originalBasketId;
  private String status;
  private String channel;
  private String subChannel;
  private Set<String> itemsTypes;
  private List<BasketItemDto> items;
  private String idContext;
}
