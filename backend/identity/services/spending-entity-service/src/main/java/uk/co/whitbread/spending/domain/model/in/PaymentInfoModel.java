package uk.co.whitbread.spending.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentInfoModel {

  @NotBlank
  private String accountId;
  @NotNull
  private Integer page;
  @NotNull
  Integer size;
  private boolean nonInvoiceOnly;
  @NotBlank
  private String authorization;
  @NotBlank
  private String ipAddress;
  private String tetheredUserGuid;

  public PaymentInfoModel(String accountId, Integer page, Integer size, boolean nonInvoiceOnly,
      String authorization, String ipAddress) {
    this.accountId = accountId;
    this.page = page;
    this.size = size;
    this.nonInvoiceOnly = nonInvoiceOnly;
    this.authorization = authorization;
    this.ipAddress = ipAddress;
  }

  public PaymentInfoModel(String accountId, Integer page, Integer size, boolean nonInvoiceOnly,
      String authorization, String ipAddress, String tetheredUserGuid) {
    this.accountId = accountId;
    this.page = page;
    this.size = size;
    this.nonInvoiceOnly = nonInvoiceOnly;
    this.authorization = authorization;
    this.ipAddress = ipAddress;
    this.tetheredUserGuid = tetheredUserGuid;
  }
}
