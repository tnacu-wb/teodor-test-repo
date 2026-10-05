package uk.co.whitbread.company.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.company.config.TestApplication;
import uk.co.whitbread.company.config.TestSecurityConfig;
import uk.co.whitbread.company.model.AnsweredQuestions;
import uk.co.whitbread.company.model.ManagementInformationAnswer;
import uk.co.whitbread.company.model.ManagementInformationQuestion;
import uk.co.whitbread.company.model.QuestionLocation;
import uk.co.whitbread.company.model.UserDefinedQuestion;
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.company.service.cdh.CdhCompanyQuestionService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.QuestionDataService;
import uk.co.whitbread.shared.cdh.model.question.Question;
import uk.co.whitbread.shared.cdh.model.question.UpdateQuestionResponse;

@SpringBootTest(
    classes = {TestApplication.class, TestSecurityConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("testCdh")
class CompanyQuestionControllerCdhTest {

    private static final String AUTHORIZATION = "Authorization";
    private static final String AUTHORIZATION_TOKEN = "Bearer Authorization";
    private static final String VALID_COMPANY_ID = "validCompanyId";
    private static final String COMPANY_ID_PARAM = "company-id";
    private static final String EMPLOYEE_ID = "employeeId";
    private static final String QUESTION_ID = "question-id";
    private static final String EMPLOYEE_ID_PARAM = "employee-id";
    private static final String REGISTRATION_QUESTION_ENDPOINT = "registration-questions";
    private static final String EMPLOYEE_QUESTIONS_ENDPOINT = "employee-questions";
    private static final String ADMIN_EMPLOYEE_QUESTIONS_ENDPOINT = "admin/employee-questions";

    private final BiFunction<String, String, String> companyEndpoint = (companyId, questionEndpoint) -> String.format("/companies/%s/%s", companyId, questionEndpoint);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CdhCompanyQuestionService cdhService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private QuestionDataService questionDataService;

    @MockitoBean
    private CdhAuthorizationService cdhAuthorizationService;

    @LocalServerPort
    int serverPort;

    private String baseUrl() {
        return "http://localhost:" + serverPort;
    }

    @Test
    void retrieveAnsweredQuestions_fromRegistrationLink_ReturnOk() throws Exception {
        AnsweredQuestions answeredQuestions = new AnsweredQuestions();
        UserDefinedQuestion userDefinedQuestion = new UserDefinedQuestion();
        userDefinedQuestion.setUserDefinedAnswer("Success");
        final List<UserDefinedQuestion> userDefinedQuestions = Collections.singletonList(userDefinedQuestion);
        answeredQuestions.setUserDefinedQuestions(userDefinedQuestions);
        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder().companyAccountId(VALID_COMPANY_ID).employeeAccountId(EMPLOYEE_ID_PARAM).userEmail("email@email.com").build();
        when(cdhService.getAnsweredQuestions(VALID_COMPANY_ID, employeeDetails))
            .thenReturn(answeredQuestions);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, REGISTRATION_QUESTION_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(COMPANY_ID_PARAM, VALID_COMPANY_ID)
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .header(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void retrieveAnsweredQuestions_fromActivationLink_ReturnOk() throws Exception {
        AnsweredQuestions answeredQuestions = new AnsweredQuestions();
        UserDefinedQuestion userDefinedQuestion = new UserDefinedQuestion();
        userDefinedQuestion.setUserDefinedAnswer("Success");
        final List<UserDefinedQuestion> userDefinedQuestions = Collections.singletonList(userDefinedQuestion);
        answeredQuestions.setUserDefinedQuestions(userDefinedQuestions);

        when(cdhService.getAnsweredQuestions(any(), any())).thenReturn(answeredQuestions);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, REGISTRATION_QUESTION_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(COMPANY_ID_PARAM, VALID_COMPANY_ID)
            .header(AUTHORIZATION, "")
            .header(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void retrieveAnsweredQuestions_fromRegistrationLink_NotFoundCompanyId_shouldReturnUnauthorized() throws Exception {
        AnsweredQuestions answeredQuestions = new AnsweredQuestions();
        UserDefinedQuestion userDefinedQuestion = new UserDefinedQuestion();
        userDefinedQuestion.setUserDefinedAnswer("Success");
        final List<UserDefinedQuestion> userDefinedQuestions = Collections.singletonList(userDefinedQuestion);
        answeredQuestions.setUserDefinedQuestions(userDefinedQuestions);
        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();
        when(cdhService.getAnsweredQuestions(VALID_COMPANY_ID, employeeDetails))
            .thenReturn(answeredQuestions);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, REGISTRATION_QUESTION_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(COMPANY_ID_PARAM, VALID_COMPANY_ID)
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .header(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void retrieveAnsweredQuestions_fromRegistrationLink_NotFoundEmployeeId_shouldReturnUnauthorized() throws Exception {
        AnsweredQuestions answeredQuestions = new AnsweredQuestions();
        UserDefinedQuestion userDefinedQuestion = new UserDefinedQuestion();
        userDefinedQuestion.setUserDefinedAnswer("Success");
        final List<UserDefinedQuestion> userDefinedQuestions = Collections.singletonList(userDefinedQuestion);
        answeredQuestions.setUserDefinedQuestions(userDefinedQuestions);
        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .userEmail("email@email.com").build();
        when(cdhService.getAnsweredQuestions(VALID_COMPANY_ID, employeeDetails))
            .thenReturn(answeredQuestions);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, REGISTRATION_QUESTION_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(COMPANY_ID_PARAM, VALID_COMPANY_ID)
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .header(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }


    @Test
    void retrieveAnsweredQuestionsForAnotherEmployeeId_ReturnOk() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/employees/" + EMPLOYEE_ID_PARAM + "/registration-questions"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void retrieveAnsweredQuestionsForAnotherEmployeeId_NotFoundEmployeeId_shouldReturnUnauthorized() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/employees/" + EMPLOYEE_ID_PARAM + "/registration-questions"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void retrieveAnsweredQuestionsForAnotherEmployeeId_NotFoundCompanyId_shouldReturnUnauthorized() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/employees/" + EMPLOYEE_ID_PARAM + "/registration-questions"))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void getCompanyUserQuestions_shouldReturnOk() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, EMPLOYEE_QUESTIONS_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getCompanyUserQuestions_NotFoundEmployeeId_shouldReturnUnauthorized() throws Exception {
        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, EMPLOYEE_QUESTIONS_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void getCompanyUserQuestions_NotFoundCompanyId_shouldReturnUnauthorized() throws Exception {
        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, EMPLOYEE_QUESTIONS_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void updateCompanyUserQuestion_shouldGetNoContentResponse() throws Exception {

        ManagementInformationQuestion managementInformationQuestion =
            createManagementInformationQuestion();

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/admin/employee-questions/" + QUESTION_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(managementInformationQuestion)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    void deleteUserQuestion_shouldGetNoContentResponse() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/admin/employee-questions/" + QUESTION_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .DELETE()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    void updateCompanyBusinessQuestion_shouldGetNoContentResponse() throws Exception {

        ManagementInformationQuestion managementInformationQuestion =
            createManagementInformationQuestion();

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/admin/business-questions/" + QUESTION_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(managementInformationQuestion)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    void getCompanyBusinessQuestion_shouldGetNoContentResponse() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/business-questions"))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

    @Test
    void getCompanyBusinessQuestion_NotFoundCompanyId_shouldReturnUnauthorized() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/business-questions"))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void getCompanyBusinessQuestion_NotFoundEmployeeId_shouldReturnUnauthorized() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/business-questions"))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void createCompanyUserQuestion_shouldReturnOk() throws Exception {

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        when(questionDataService.updateQuestion(eq(COMPANY_ID_PARAM), eq(QUESTION_ID), any(Question.class), anyString()))
            .thenReturn(new UpdateQuestionResponse());

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + companyEndpoint.apply(VALID_COMPANY_ID, ADMIN_EMPLOYEE_QUESTIONS_ENDPOINT)))
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(createManagementInformationQuestion())))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(201);
    }

    @Test
    void removeUserQuestion_shouldGetNoContentResponse() throws Exception {

        ManagementInformationQuestion managementInformationQuestion =
            createManagementInformationQuestion();

        CdhEmployeeDetails employeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(VALID_COMPANY_ID)
            .employeeAccountId(EMPLOYEE_ID_PARAM)
            .userEmail("email@email.com").build();

        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN)).thenReturn(employeeDetails);
        when(cdhAuthorizationService.isSameCompany(anyString(), anyString())).thenReturn(true);
        when(cdhAuthorizationService.isSuperAccessLevelUser(any())).thenReturn(true);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID_PARAM + "/admin/employee-questions/" + QUESTION_ID))
            .header("Content-Type", "application/json")
            .header(AUTHORIZATION, AUTHORIZATION_TOKEN)
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(managementInformationQuestion)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(204);
    }

    private ManagementInformationQuestion createManagementInformationQuestion() {
        ManagementInformationQuestion managementInformationQuestion = new ManagementInformationQuestion();
        managementInformationQuestion.setQuestionId(QUESTION_ID);
        managementInformationQuestion.setLabel("test");
        managementInformationQuestion.setMandatory(false);
        managementInformationQuestion.setManagementHeader("test");
        managementInformationQuestion.setActive(true);
        managementInformationQuestion.setLocation(QuestionLocation.R);
        ManagementInformationAnswer managementInformationAnswer = new ManagementInformationAnswer();
        managementInformationAnswer.setAnswerType(null);
        managementInformationAnswer.setAnswers(List.of());
        managementInformationQuestion.setManagementInformationAnswer(managementInformationAnswer);
        return managementInformationQuestion;
    }
}
