package uk.co.whitbread.domain.model.basket.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Basket {

  private String bookingReference;
  private List<BasketItem> items;
}