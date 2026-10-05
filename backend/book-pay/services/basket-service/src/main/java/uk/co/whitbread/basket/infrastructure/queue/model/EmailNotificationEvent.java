package uk.co.whitbread.basket.infrastructure.queue.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.email.out.TransactionData;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotificationEvent {

  private String id;
  private String type;
  private String bookingReference;
  private String hotelId;
  private List<EmailItem> items;
  private TransactionData transactionData;
  private List<String> excludedPurposes;
  private Map<String, String> details;
  private String createdAt;
}
