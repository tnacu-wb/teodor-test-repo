package uk.co.whitbread.piba.registration.util;

import uk.co.whitbread.piba.registration.model.*;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationAuthenticationQuestionType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationCodeInfoType;
import worldline.mst.bsm.api.b2b.pi.data.RegistrationPrepopulatedItemsType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestUtil {
    public static final String REGISTRATION_CODE = "A6ZN-2AZZ-Z2T6-A3DE";
    public static final String REGISTRATION_CODE_DE = "A6ZN-2AZZ-Z2T6-A3DE";
    public static final String REGISTRATION_CODE_GB = "A6ZN-2AZZ-Z2T6-A3GB";
    public static final int PRIMARY_SCHEME_CUSTOMER_ID =778652;
    public static final int SCHEME_CUSTOMER_ID =778653;
    public static final String REGISTRATION_ROLE="AccountHolder";
    public static final String TETHERED_USER_GUID="327f7a0c-9a33-41c2-808d-74f15f24797c";

    public RegistrationAuthenticationRequest buildAuthenticationRequest(String registrationCode){
        RegistrationAuthenticationRequest registrationAuthenticationRequest = new RegistrationAuthenticationRequest();
        registrationAuthenticationRequest.setRegistrationCode(registrationCode);
        List<RegistrationAuthenticationAnswer> registrationAuthenticationAnswers= new ArrayList<>();
        registrationAuthenticationAnswers.add(new RegistrationAuthenticationAnswer(1,"Answer"));
        registrationAuthenticationRequest.setAuthenticationAnswers(registrationAuthenticationAnswers);
        return registrationAuthenticationRequest;
    }

    public RegistrationAuthenticationResponse buildAuthenticationResponse(){
        RegistrationCodeInfo registrationCodeInfo=new RegistrationCodeInfo();
        RegistrationAuthenticationResponse registrationAuthenticationResponse=new RegistrationAuthenticationResponse();
        registrationCodeInfo.setRegistrationCode(REGISTRATION_CODE);
        registrationAuthenticationResponse.setRegistrationCodeInfo(registrationCodeInfo);
        registrationAuthenticationResponse.setRegistrationPrePopulatedItems(buildPrePopulatedItems());
        return registrationAuthenticationResponse;
    }

    public RegistrationPrepopulatedItemsType buildPrePopulatedItemsType(){

        RegistrationPrepopulatedItemsType registrationPrePopulatedItems=new RegistrationPrepopulatedItemsType();
        registrationPrePopulatedItems.setEmailAddress("aa@bb.cc");
        registrationPrePopulatedItems.setForename("Forename");
        registrationPrePopulatedItems.setLandlineNumber("01457891111");
        registrationPrePopulatedItems.setMobileNumber("01457891111");
        registrationPrePopulatedItems.setSurname("Surname");
        registrationPrePopulatedItems.setTitle("Title");
        return registrationPrePopulatedItems;
    }

    public RegistrationPrePopulatedItems buildPrePopulatedItems(){

        RegistrationPrePopulatedItems registrationPrePopulatedItems=new RegistrationPrePopulatedItems();
        registrationPrePopulatedItems.setEmailAddress("aa@bb.cc");
        registrationPrePopulatedItems.setForename("Forename");
        registrationPrePopulatedItems.setLandlineNumber("01457891111");
        registrationPrePopulatedItems.setMobileNumber("01457891111");
        registrationPrePopulatedItems.setSurname("Surname");
        registrationPrePopulatedItems.setTitle("Title");
        return registrationPrePopulatedItems;
    }

    public RegistrationCodeInfoType buildRegistrationCodeInfoType(boolean addQuestions){
        RegistrationCodeInfoType registrationCodeInfoType=new RegistrationCodeInfoType();
        registrationCodeInfoType.setPrimarySchemeCustomerId(PRIMARY_SCHEME_CUSTOMER_ID);
        registrationCodeInfoType.setRegistrationCode(REGISTRATION_CODE);
        registrationCodeInfoType.setRegistrationRole(REGISTRATION_ROLE);
        registrationCodeInfoType.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        if (addQuestions) {
            RegistrationAuthenticationQuestionType registrationAuthenticationQuestionType = new RegistrationAuthenticationQuestionType();
            registrationAuthenticationQuestionType.setQuestionId(1);
            registrationAuthenticationQuestionType.setQuestion("Question");
            registrationCodeInfoType.getAuthenticationQuestions().add(registrationAuthenticationQuestionType);
        }
        return registrationCodeInfoType;
    }

    public RegistrationCodeInfo buildRegistrationCodeInfo(){
        RegistrationCodeInfo registrationCodeInfo=new RegistrationCodeInfo();
        registrationCodeInfo.setPrimarySchemeCustomerId(PRIMARY_SCHEME_CUSTOMER_ID);
        registrationCodeInfo.setRegistrationCode(REGISTRATION_CODE);
        registrationCodeInfo.setRegistrationRole(REGISTRATION_ROLE);
        registrationCodeInfo.setSchemeCustomerId(SCHEME_CUSTOMER_ID);
        RegistrationAuthenticationQuestion registrationAuthenticationQuestion=new RegistrationAuthenticationQuestion();
        registrationAuthenticationQuestion.setQuestionId(1);
        registrationAuthenticationQuestion.setQuestion("Question");
        registrationCodeInfo.setAuthenticationQuestions(Collections.singletonList(registrationAuthenticationQuestion));
        return registrationCodeInfo;
    }

    public RegistrationSubmitRequest buildRegistrationSubmitRequest(String registrationCode) {
        RegistrationSubmitRequest registrationSubmitRequest=new RegistrationSubmitRequest();
        registrationSubmitRequest.setRegistrationCode(registrationCode);
        List<RegistrationAuthenticationAnswer> registrationAuthenticationAnswers= new ArrayList<>();
        registrationAuthenticationAnswers.add(new RegistrationAuthenticationAnswer(1,"Answer"));
        registrationSubmitRequest.setAuthenticationAnswers(registrationAuthenticationAnswers);

        RegistrationDetails registrationDetails=new RegistrationDetails();
        registrationDetails.setEmailAddress("aa@bb.cc");
        registrationDetails.setForename("Forename");
        registrationDetails.setLandlineNumber("01457891111");
        registrationDetails.setMobileNumber("01457891111");
        registrationDetails.setSurname("Surname");
        registrationDetails.setTitle("Title");
        registrationDetails.setMemorableWord("MemorableWord");
        registrationSubmitRequest.setRegistrationDetails(registrationDetails);
        return registrationSubmitRequest;
    }

    public RegistrationSubmitResp buildRegistrationSubmitResp(){
        RegistrationSubmitResp registrationSubmitResp =new RegistrationSubmitResp();
        TetherDetails tetherDetailsType=new TetherDetails();
        tetherDetailsType.setTetheredUserGuid(TETHERED_USER_GUID);
        registrationSubmitResp.setTetherDetails(tetherDetailsType);
        registrationSubmitResp.setRegistrationCodeInfo(buildRegistrationCodeInfo());
        return registrationSubmitResp;
    }

    public RegistrationInfoResponse buildRegistrationInfoResponse(){
        RegistrationInfoResponse registrationInfoResponse =new RegistrationInfoResponse();

        registrationInfoResponse.setRegistrationCodeInfo(buildRegistrationCodeInfo());
        return registrationInfoResponse;
    }
}
