package uk.co.whitbread.piba.registration.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegistrationAuthenticationAnswer {
    protected int questionId;

    @NotEmpty
    protected String answer;
}
