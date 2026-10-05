package uk.co.whitbread.piba.registration.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationAuthenticationQuestion {
    protected int questionId;
    protected String question;
}
