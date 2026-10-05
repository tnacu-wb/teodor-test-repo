package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.validation.ModelValidator;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
public class ShareAppRequestDto extends ModelValidator<ShareAppRequestDto> {

  @NotBlank
  private String applicationId;

  @NotBlank
  private String applicationGuid;

  @NotNull
  private int employeeId;

  @NotBlank
  private String email;

  @NotBlank
  private String fullName;

  public ShareAppRequestDto(String applicationId, String applicationGuid, int employeeId,
      String email, String fullName) {
    this.applicationId = applicationId;
    this.applicationGuid = applicationGuid;
    this.employeeId = employeeId;
    this.email = email;
    this.fullName = fullName;
    this.validate();
  }
}
