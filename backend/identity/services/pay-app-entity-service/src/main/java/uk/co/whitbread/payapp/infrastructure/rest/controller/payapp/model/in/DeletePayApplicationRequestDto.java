package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import uk.co.whitbread.payapp.domain.model.in.Scheme;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletePayApplicationRequestDto extends ModelValidator<DeletePayApplicationRequestDto> {
  @NotEmpty
  private String applicationId;

  @NotEmpty
  private String applicationGuid;

  private Scheme scheme;
}
