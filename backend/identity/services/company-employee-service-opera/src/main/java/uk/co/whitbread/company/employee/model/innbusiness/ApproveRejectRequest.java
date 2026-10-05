package uk.co.whitbread.company.employee.model.innbusiness;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.company.employee.model.AccessLevel;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveRejectRequest {

  @Email
  @NotBlank
  private String email;
  @NotNull
  private AccessLevel accessLevel;
  @NotNull
  private Boolean approved;
  @NotBlank
  private String language;
}
