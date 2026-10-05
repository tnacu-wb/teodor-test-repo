package uk.co.whitbread.company.service.cdh;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.company.model.QuestionLocation.R;
import static uk.co.whitbread.company.service.cdh.CdhCompanyQuestionService.CUSTOMER_REFERENCE;
import static uk.co.whitbread.company.service.cdh.CdhCompanyQuestionService.PURCHASE_ORDER_NUMBER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.company.exceptions.ExceededLimitOfQuestionsException;
import uk.co.whitbread.company.exceptions.UnsupportedQuestionIdException;
import uk.co.whitbread.company.mapper.QuestionMapper;
import uk.co.whitbread.company.model.AnsweredQuestions;
import uk.co.whitbread.company.model.BusinessQuestions;
import uk.co.whitbread.company.model.ManagementInformationQuestion;
import uk.co.whitbread.company.model.QuestionLocation;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.CreateQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;

@ExtendWith(MockitoExtension.class)
class CdhCompanyQuestionServiceTest {

  private static final String COMPANY_ID = "125";
  private static final String EMPLOYEE_ID = "111";
  private static final String QUESTION_ID = "e72c9798-f868-4db2-83fd-b26edba01234";
  private static final String EMAIL = "user@mail.com";

  @Mock
  private CdhService cdhService;
  @Mock
  private QuestionMapper questionMapper;

  @InjectMocks
  private CdhCompanyQuestionService cdhCompanyQuestionService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void getUserCreatedQuestions_success() {
    List<GetQuestionResponse> questions = List.of(GetQuestionResponse.builder().build());
    var managementInformationQuestions = List.of(new ManagementInformationQuestion());
    when(cdhService.getQuestions(COMPANY_ID, EMAIL)).thenReturn(questions);
    when(questionMapper.toManagementInformationQuestion(questions))
        .thenReturn(managementInformationQuestions);

    var response = cdhCompanyQuestionService.getUserCreatedQuestions(COMPANY_ID, EMAIL);

    assertEquals(1, response.size());
    assertEquals(managementInformationQuestions, response);
  }

  @Test
  void updateQuestion_success() {
    cdhCompanyQuestionService.updateQuestion(COMPANY_ID, QUESTION_ID,
        new ManagementInformationQuestion(), EMAIL);

    verify(cdhService).updateQuestion(any(), any(), any(), any());
  }

  @Test
  void deleteQuestion_success() {
    cdhCompanyQuestionService.deleteQuestion(COMPANY_ID, QUESTION_ID, EMAIL);

    verify(cdhService).deleteQuestion(COMPANY_ID, QUESTION_ID, EMAIL);
  }

  @Test
  void postQuestion_success() {
    var managementInformationQuestion = new ManagementInformationQuestion();
    var question = Question.builder().build();
    var position = 1;
    var createQuestionResponse = CreateQuestionResponse.builder()
        .id(QUESTION_ID)
        .position(position)
        .build();
    when(cdhService.getQuestions(COMPANY_ID, EMAIL)).thenReturn(List.of());
    when(questionMapper.toQuestion(managementInformationQuestion)).thenReturn(question);
    when(cdhService.createQuestion(COMPANY_ID, question, EMAIL))
        .thenReturn(createQuestionResponse);

    var questionId = cdhCompanyQuestionService.postQuestion(COMPANY_ID, managementInformationQuestion, EMAIL);

    assertEquals(QUESTION_ID, questionId);
  }

  @Test
  void postQuestion_throwsExceededLimitOfQuestionsException() {
    var managementInformationQuestion = new ManagementInformationQuestion();
    var questions = buildMaxQuestions();
    when(cdhService.getQuestions(COMPANY_ID, EMAIL)).thenReturn(questions);

    assertThatThrownBy(
        () -> cdhCompanyQuestionService.postQuestion(COMPANY_ID, managementInformationQuestion, EMAIL))
        .isInstanceOf(ExceededLimitOfQuestionsException.class)
        .hasMessageContaining("Limit of questions has been reached!");
  }

  @Test
  void getEmployeeAnswersForCompanyQuestions_success() throws IOException {
    var companyResponse = buildGetCompanyResponse();
    var getEmployeeResponse = buildGetEmployeeResponse();
    var answeredQuestion = buildAnsweredQuestions();

    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(
        companyResponse);
    when(cdhService.getEmployeeById(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(getEmployeeResponse);
    when(questionMapper.toAnsweredQuestions(companyResponse.getCompanyManagementDetails()))
        .thenReturn(answeredQuestion);

    var answeredQuestions = cdhCompanyQuestionService.getEmployeeAnswersForCompanyQuestions(
        COMPANY_ID, EMPLOYEE_ID, EMAIL);

    assertEquals(QuestionLocation.R, answeredQuestions.getPurchaseOrderManagement().getLocation());
    assertEquals(QuestionLocation.R,
        answeredQuestions.getCustomerReferenceManagement().getLocation());

    for (var question : answeredQuestions.getUserDefinedQuestions()) {
      assertEquals(QuestionLocation.R, question.getLocation());
      assertNotNull(question.getManagementInformationAnswer());
    }

    assertNotNull(answeredQuestions.getPurchaseOrderManagement().getAnswer());
    assertNotNull(answeredQuestions.getCustomerReferenceManagement().getAnswer());
    assertEquals(2, answeredQuestions.getUserDefinedQuestions().size());
  }

  @Test
  void getAnsweredQuestions_success() throws IOException {
    var company = buildGetCompanyResponse();
    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(company);
    when(cdhService.getEmployeeById(COMPANY_ID, EMPLOYEE_ID, EMAIL)).thenReturn(
        buildGetEmployeeResponse());
    when(questionMapper.toAnsweredQuestions(company.getCompanyManagementDetails()))
        .thenReturn(buildAnsweredQuestions());
    var cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ID)
        .employeeAccountId(EMPLOYEE_ID)
        .userEmail(EMAIL)
        .build();

    var answeredQuestions = cdhCompanyQuestionService.getAnsweredQuestions(COMPANY_ID,
        cdhEmployeeDetails);

    assertRegistrationQuestions(answeredQuestions);
  }

  @Test
  void getCompanyBusinessQuestions_success() throws IOException {
    var companyResponse = buildGetCompanyResponse();
    when(cdhService.getCompanyDetails(COMPANY_ID, EMAIL)).thenReturn(companyResponse);
    when(questionMapper.toBusinessQuestions(companyResponse.getCompanyManagementDetails())).thenReturn(new BusinessQuestions());

    var businessQuestion = cdhCompanyQuestionService.getBusinessQuestions(COMPANY_ID, EMAIL);

    assertNotNull(businessQuestion);
  }

  @Test
  void updateBusinessQuestionsWithPurchaseOrderNumber_success() {
    cdhCompanyQuestionService.updateBusinessQuestions(COMPANY_ID, PURCHASE_ORDER_NUMBER,
        new ManagementInformationQuestion(), EMAIL);

    verify(cdhService).updateCompanyDetails(any(), any(), any());
  }

  @Test
  void updateBusinessQuestionsWithCustomerReference_success() {
    cdhCompanyQuestionService.updateBusinessQuestions(COMPANY_ID, CUSTOMER_REFERENCE,
        new ManagementInformationQuestion(), EMAIL);

    verify(cdhService).updateCompanyDetails(any(), any(), any());
  }

  @Test
  void updateBusinessQuestions_throwsUnsupportedQuestionIdException() {
    ManagementInformationQuestion question = new ManagementInformationQuestion();
    assertThatThrownBy(
        () -> cdhCompanyQuestionService.updateBusinessQuestions(COMPANY_ID, QUESTION_ID,
            question, EMAIL))
        .isInstanceOf(UnsupportedQuestionIdException.class)
        .hasMessageContaining("Unsupported question id:");
  }

  private void assertRegistrationQuestions(AnsweredQuestions answeredQuestions) {
    assertEquals(R, answeredQuestions.getCustomerReferenceManagement().getLocation());
    assertNotNull(answeredQuestions.getCustomerReferenceManagement().getAnswer());
    assertEquals(R, answeredQuestions.getPurchaseOrderManagement().getLocation());
    assertNotNull(answeredQuestions.getPurchaseOrderManagement().getAnswer());
    assertEquals(2, answeredQuestions.getUserDefinedQuestions().size());
    assertEquals(R, answeredQuestions.getUserDefinedQuestions().get(0).getLocation());
    assertNotNull(answeredQuestions.getUserDefinedQuestions().get(0).getUserDefinedAnswer());
    assertEquals(R, answeredQuestions.getUserDefinedQuestions().get(1).getLocation());
    assertNotNull(answeredQuestions.getUserDefinedQuestions().get(1).getUserDefinedAnswer());
  }

  private List<GetQuestionResponse> buildMaxQuestions() {
    var questions = new ArrayList<GetQuestionResponse>();
    for (int i = 0; i < 8; i++) {
      questions.add(GetQuestionResponse.builder().build());
    }
    return questions;
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

  private AnsweredQuestions buildAnsweredQuestions() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mappings/cdh/AnsweredQuestions.json"),
        AnsweredQuestions.class);
  }
}
