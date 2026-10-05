package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class ScaDto implements SelfValidation<ScaDto> {

  private String captureMethod;
  private String protocolVersion;
  private Integer eci;
  private String xid;
  private String dsTransactionID;
  private String transStatus;
  private Integer cavv;
  private Float authorisedAmount;

  public ScaDto(String captureMethod, String protocolVersion, Integer eci, String xid, String dsTransactionID,
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