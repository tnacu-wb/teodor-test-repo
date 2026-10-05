package uk.co.whitbread.basket.infrastructure.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingCompletedEvent {
  private String basketReference;
  private String status;
}
