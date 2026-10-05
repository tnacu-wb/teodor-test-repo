package uk.co.whitbread.company.service.cdh;

import static uk.co.whitbread.company.utils.QuestionUtils.getRegistrationQuestionsAndAnswers;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.company.exceptions.UnsupportedQuestionIdException;
import uk.co.whitbread.company.exceptions.ExceededLimitOfQuestionsException;
import uk.co.whitbread.company.mapper.QuestionMapper;
import uk.co.whitbread.company.model.AnsweredQuestions;
import uk.co.whitbread.company.model.BusinessQuestions;
import uk.co.whitbread.company.model.ManagementInformationQuestion;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.cdh.model.EmployeeAnswers;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class CdhCompanyQuestionService {

  private static final int QUESTIONS_LIMIT = 8;
  public static final String PURCHASE_ORDER_NUMBER = "purchase_order_number";
  public static final String CUSTOMER_REFERENCE = "customer_reference";

  private final CdhService cdhService;
  private final QuestionMapper questionMapper;

  public List<ManagementInformationQuestion> getUserCreatedQuestions(String companyId,
      String userEmail) {
    log.debug("Called CdhCompanyQuestionService.getUserCreatedQuestions");
    return questionMapper.toManagementInformationQuestion(
        cdhService.getQuestions(companyId, userEmail));
  }

  public void updateQuestion(String companyId, String questionId,
      ManagementInformationQuestion question, String userEmail) {
    log.debug("Called CdhCompanyQuestionService.updateQuestion");
    cdhService
        .updateQuestion(companyId, questionId, questionMapper.toQuestion(question), userEmail);
  }

  public void deleteQuestion(String companyId, String questionId, String userEmail) {
    log.debug("Called CdhCompanyQuestionService.deleteQuestionById");
    cdhService.deleteQuestion(companyId, questionId, userEmail);
  }

  public String postQuestion(String companyId, ManagementInformationQuestion question,
      String userEmail) {
    var existingQuestions = cdhService.getQuestions(companyId, userEmail);
    if (existingQuestions.size() >= QUESTIONS_LIMIT) {
      throw new ExceededLimitOfQuestionsException();
    }
    return cdhService.createQuestion(companyId, questionMapper.toQuestion(question), userEmail)
        .getId();
  }

  public AnsweredQuestions getEmployeeAnswersForCompanyQuestions(String companyAccountId,
      String employeeAccountId, String accessedBy) {
    GetCompanyResponse companyResponse = cdhService.getCompanyDetails(companyAccountId, accessedBy);
    EmployeeAnswers employeeAnswers = cdhService.getEmployeeById(companyAccountId,
        employeeAccountId, accessedBy).getEmployeeAnswers();
    AnsweredQuestions answeredQuestions = questionMapper.toAnsweredQuestions(
        companyResponse.getCompanyManagementDetails());
    return getRegistrationQuestionsAndAnswers(answeredQuestions, employeeAnswers);
  }

  public AnsweredQuestions getAnsweredQuestions(String companyId,
      CdhEmployeeDetails cdhEmployeeDetails) {
    var employeeId = cdhEmployeeDetails.getEmployeeAccountId();
    var userEmail = cdhEmployeeDetails.getUserEmail();
    var company = cdhService.getCompanyDetails(companyId, userEmail);
    var employeeAnswers = cdhService.getEmployeeById(companyId, employeeId, userEmail)
        .getEmployeeAnswers();
    var answeredQuestions = questionMapper.toAnsweredQuestions(
        company.getCompanyManagementDetails());
    return getRegistrationQuestionsAndAnswers(answeredQuestions, employeeAnswers);
  }

  public BusinessQuestions getBusinessQuestions(String companyAccountId, String accessedBy) {
    GetCompanyResponse companyResponse = cdhService.getCompanyDetails(companyAccountId, accessedBy);
    return questionMapper.toBusinessQuestions(companyResponse.getCompanyManagementDetails());
  }

  public void updateBusinessQuestions(String companyAccountId, String questionId,
      ManagementInformationQuestion userQuestion, String accessedBy) {
    if (PURCHASE_ORDER_NUMBER.equals(questionId) || CUSTOMER_REFERENCE.equals(questionId)) {
      GetCompanyResponse companyResponse = cdhService
          .getCompanyDetails(companyAccountId, accessedBy);
      CompanyAccountRequest request;
      if (questionId.equals(PURCHASE_ORDER_NUMBER)) {
        request = questionMapper
            .toCompanyAccountRequestWithUpdatedPurchaseOrder(companyResponse, userQuestion);
      } else {
        request = questionMapper
            .toCompanyAccountRequestWithUpdatedCustomerReference(companyResponse, userQuestion);
      }
      cdhService.updateCompanyDetails(companyAccountId, request, accessedBy);
    } else {
      throw new UnsupportedQuestionIdException("Unsupported question id: " + questionId +
          ". Question id must be " + PURCHASE_ORDER_NUMBER +
          " or " + CUSTOMER_REFERENCE);
    }
  }
}
