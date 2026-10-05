package uk.co.whitbread.cdh.domain.model.booking.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceTseData {

  @JsonProperty("Fn")
  private String fn;

  @JsonProperty("StartDateTime")
  private String startDateTime;

  @JsonProperty("EndDateTime")
  private String endDateTime;

  @JsonProperty("Serial")
  private String serial;

  @JsonProperty("SignCnt")
  private String signCnt;

  @JsonProperty("Sign")
  private String sign;

  @JsonProperty("Code")
  private String code;

  @JsonProperty("Sq")
  private String sq;

  @JsonProperty("Tn")
  private String tn;

  @JsonProperty("SeqNo")
  private String seqNo;
}

