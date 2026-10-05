package uk.co.whitbread.cdh.infrastructure.rest.controller.reservationsearch.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTseDataDto {

  private String fn;
  private String startDateTime;
  private String endDateTime;
  private String serial;
  private String signCnt;
  private String sign;
  private String code;
  private String sq;
  private String tn;
  private String seqNo;
}
