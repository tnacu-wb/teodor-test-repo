package uk.co.whitbread.company.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.model.AnswerType.F;
import static uk.co.whitbread.company.model.QuestionLocation.R;
import static uk.co.whitbread.company.service.cdh.CdhCompanyQuestionService.PURCHASE_ORDER_NUMBER;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
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
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.CompanyDataService;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.QuestionDataService;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.CreateQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;

@SpringBootTest(
    classes = {TestApplication.class, TestSecurityConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("testCdh")
class CdhCompanyQuestionControllerTest {

  private static final String COMPANY_ID = "companyId";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final HttpClient httpClient = HttpClient.newHttpClient();

  @LocalServerPort
  private int serverPort;
  @MockitoBean
  private CdhAuthorizationService cdhAuthorizationService;
  @MockitoBean
  private CompanyDataService companyDataService;
  @MockitoBean
  private EmployeeDataService employeeDataService;
  @MockitoBean
  private QuestionDataService questionDataService;
  @MockitoBean
  private TokenService tokenService;

  private String baseUrl() {
    return "http://localhost:" + serverPort;
  }

  @Test
  void getUserCreatedQuestions_shouldGetQuestions() throws Exception {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(questionDataService.getQuestions(COMPANY_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(buildGetQuestionsResponse());

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/employee-questions"))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .GET()
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    JsonNode body = objectMapper.readTree(response.body());
    assertThat(body).hasSize(1);
    assertThat(body.get(0).get("questionId").asText()).isEqualTo("2376cc80-533a-454b-bd48-885e4736408d");
  }

  @Test
  void getRegistrationQuestions_shouldGetQuestions() throws Exception {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();

    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(companyDataService.getCompany(COMPANY_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(Optional.of(buildGetCompanyResponse()));
    when(employeeDataService.getEmployee(COMPANY_ID, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN,
        USER_EMAIL_FROM_TOKEN)).thenReturn(Optional.of(buildGetEmployeeResponse()));

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/registration-questions"))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .GET()
        .build();

    HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(httpResponse.statusCode()).isEqualTo(200);
    AnsweredQuestions response = objectMapper.readValue(httpResponse.body(), AnsweredQuestions.class);

    assertEquals(R, response.getCustomerReferenceManagement().getLocation());
    assertEquals("My Customer Reference Answer",
        response.getCustomerReferenceManagement().getAnswer());
    assertEquals(R, response.getPurchaseOrderManagement().getLocation());
    assertEquals("My Purchase Order Answer", response.getPurchaseOrderManagement().getAnswer());
    assertEquals(2, response.getUserDefinedQuestions().size());
    assertEquals(R, response.getUserDefinedQuestions().get(0).getLocation());
    assertEquals("My answer", response.getUserDefinedQuestions().get(0).getUserDefinedAnswer());
    assertEquals(R, response.getUserDefinedQuestions().get(1).getLocation());
    assertEquals("My answer 2", response.getUserDefinedQuestions().get(1).getUserDefinedAnswer());
  }

  @Test
  void createCompanyUserQuestion_shouldCreateQuestions() throws Exception {
    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    var question = buildManagementInformationQuestion();

    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(cdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(questionDataService.getQuestions(COMPANY_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(buildGetQuestionsResponse());
    when(questionDataService.createQuestion(eq(COMPANY_ID), any(Question.class),
        eq(USER_EMAIL_FROM_TOKEN))).thenReturn(buildCreateQuestionResponse());

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/admin/employee-questions"))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(question)))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(201);
  }

  @Test
  void updateCompanyBusinessQuestion_shouldUpdateBusinessQuestions() throws Exception {
    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    var question = buildManagementInformationQuestion();

    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(cdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);
    when(companyDataService.getCompany(COMPANY_ID, USER_EMAIL_FROM_TOKEN))
        .thenReturn(Optional.of(buildGetCompanyResponse()));

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(baseUrl() + "/companies/" + COMPANY_ID + "/admin/employee-questions/" + PURCHASE_ORDER_NUMBER))
        .header("Content-Type", "application/json")
        .header(AUTHORIZATION_PARAM, AUTHORIZATION)
        .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(question)))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(204);
  }

  private ManagementInformationQuestion buildManagementInformationQuestion() {
    var question = new ManagementInformationQuestion();
    var answer = new ManagementInformationAnswer();
    answer.setAnswerType(F);
    question.setLabel("Age question");
    question.setMandatory(true);
    question.setManagementHeader("How old are you ?");
    question.setActive(true);
    question.setLocation(R);
    question.setManagementInformationAnswer(answer);
    return question;
  }

  private GetCompanyResponse buildGetCompanyResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mappings/cdh/GetCompanyResponse.json"),
        GetCompanyResponse.class);
  }

  private GetEmployeeResponse buildGetEmployeeResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mappings/cdh/GetEmployeeResponse.json"),
        GetEmployeeResponse.class);
  }

  private List<GetQuestionResponse> buildGetQuestionsResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mappings/cdh/GetQuestionsResponse.json"),
        new TypeReference<List<GetQuestionResponse>>() {});
  }

  private CreateQuestionResponse buildCreateQuestionResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mappings/cdh/CreateQuestionResponse.json"),
        CreateQuestionResponse.class);
  }
}
