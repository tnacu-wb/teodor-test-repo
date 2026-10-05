package uk.co.whitbread.piba.registration.model;

import lombok.Data;

@Data
public class RegistrationAuthenticationResponse {
    private RegistrationCodeInfo registrationCodeInfo;
    private RegistrationPrePopulatedItems registrationPrePopulatedItems;
}
