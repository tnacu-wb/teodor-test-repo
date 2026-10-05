package uk.co.whitbread.piba.registration.model;

import lombok.Data;

import java.util.List;

@Data
public class RegistrationCodeInfo {
    private String registrationCode ;
    private int primarySchemeCustomerId;
    private int schemeCustomerId;
    private String registrationRole;
    private List<RegistrationAuthenticationQuestion> authenticationQuestions;

}
