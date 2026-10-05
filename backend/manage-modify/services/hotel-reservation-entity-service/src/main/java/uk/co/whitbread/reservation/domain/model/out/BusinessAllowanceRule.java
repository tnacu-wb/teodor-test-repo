package uk.co.whitbread.reservation.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessAllowanceRule {

  private String pms;
  private String sourceId;
  private String sourceType;
  private String aemId;
  private String targetId;
  private boolean isTransactionCode;
  private boolean isNotesMandatory;

}
