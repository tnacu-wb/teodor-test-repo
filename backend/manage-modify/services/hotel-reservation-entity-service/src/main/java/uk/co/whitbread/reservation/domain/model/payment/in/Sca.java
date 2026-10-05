package uk.co.whitbread.reservation.domain.model.payment.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Sca {

  private String captureMethod;
  private String protocolVersion;
  private Integer eci;
  private String xid;
  private String dsTransactionID;
  private String transStatus;
  private Integer cavv;
  private Float authorisedAmount;
}
