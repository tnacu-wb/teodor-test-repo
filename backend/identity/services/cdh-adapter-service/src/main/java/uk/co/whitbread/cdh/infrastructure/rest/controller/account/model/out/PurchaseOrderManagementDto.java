package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderManagementDto {
  private boolean active;
  private String label;
  private String location;
  private String header;
  private AnswersDto answers;
  private boolean mandatory;
  private String id;
  private String type;
}
