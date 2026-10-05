package uk.co.whitbread.company.employee.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordChange {

    @NotEmpty
    private String currentPassword;

    @NotEmpty
    private String newPassword;
}
