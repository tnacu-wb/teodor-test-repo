package uk.co.whitbread.content.infrastructure.rest.controller.inn.business.cardmanagement.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonIconsDto {

  private PaymentDto payment;
  private ChevronDto chevron;
  private NotificationDto notification;
  private ArrowDto arrow;

}
