package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class GetAppCardsRequestDto extends ModelValidator<GetAppCardsRequestDto> {

  @NotBlank
  @Parameter(in = ParameterIn.QUERY, required = true)
  private String applicationGuid;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, required = true)
  private Scheme scheme;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, required = true)
  private int page;

  @NotNull
  @Parameter(in = ParameterIn.QUERY, required = true)
  private int maxDisplayRows;

  public GetAppCardsRequestDto(String applicationGuid, Scheme scheme, int page,
      int maxDisplayRows) {
    this.applicationGuid = applicationGuid;
    this.scheme = scheme;
    this.page = page;
    this.maxDisplayRows = maxDisplayRows;
    this.validate();
  }
}
