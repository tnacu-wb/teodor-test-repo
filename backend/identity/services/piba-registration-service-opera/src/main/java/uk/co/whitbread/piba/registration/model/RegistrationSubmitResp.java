package uk.co.whitbread.piba.registration.model;

import lombok.Data;

@Data
public class RegistrationSubmitResp {
    protected RegistrationCodeInfo registrationCodeInfo;

    protected TetherDetails tetherDetails;
}
