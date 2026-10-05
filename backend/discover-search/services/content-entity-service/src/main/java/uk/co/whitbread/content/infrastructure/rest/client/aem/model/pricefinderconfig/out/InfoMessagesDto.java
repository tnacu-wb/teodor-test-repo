package uk.co.whitbread.content.infrastructure.rest.client.aem.model.pricefinderconfig.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoMessagesDto {
  private String messageTitle;
  private String messageSubtitle;
  private String messageType;
  private Integer messageOrder;
}
