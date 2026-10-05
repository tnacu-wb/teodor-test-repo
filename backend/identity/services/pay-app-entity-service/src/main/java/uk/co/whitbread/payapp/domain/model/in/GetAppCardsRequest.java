package uk.co.whitbread.payapp.domain.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.whitbread.payapp.domain.model.validation.DomainValidator;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class GetAppCardsRequest extends DomainValidator<GetAppCardsRequest> {

  @NotBlank
  private String applicationGuid;

  @NotNull
  private Scheme scheme;

  @NotNull
  private int page;

  @NotNull
  private int maxDisplayRows;

  public GetAppCardsRequest(String applicationGuid, Scheme scheme, int page, int maxDisplayRows) {
    this.applicationGuid = applicationGuid;
    this.scheme = scheme;
    this.page = page;
    this.maxDisplayRows = maxDisplayRows;
    this.validateSelf();
  }
}
