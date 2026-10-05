package uk.co.whitbread.company.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAccessLevelRequest {

    @NotNull
    private AccessLevel accessLevel;
}
