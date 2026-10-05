package uk.co.whitbread.basket.domain.model.payments.in;

import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Sca implements SelfValidation<Sca> {

  private String captureMethod;
  private String protocolVersion;
  private Integer eci;
  private String xid;
  private String dsTransactionID;
  private String transStatus;
  private Integer cavv;
  private Float authorisedAmount;

  public Sca(String captureMethod, String protocolVersion, Integer eci, String xid, String dsTransactionID,
             String transStatus, Integer cavv, Float authorisedAmount) {
    this.captureMethod = captureMethod;
    this.protocolVersion = protocolVersion;
    this.eci = eci;
    this.xid = xid;
    this.dsTransactionID = dsTransactionID;
    this.transStatus = transStatus;
    this.cavv = cavv;
    this.authorisedAmount = authorisedAmount;
    this.validateSelf();
  }
}