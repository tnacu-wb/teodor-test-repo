package uk.co.whitbread.company.utils;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.company.model.QuestionLocation.R;

import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.company.model.AnsweredQuestions;
import uk.co.whitbread.shared.cdh.model.EmployeeAnswers;

@UtilityClass
public class QuestionUtils {

  public static AnsweredQuestions getRegistrationQuestionsAndAnswers(
      AnsweredQuestions answeredQuestions, EmployeeAnswers employeeAnswers) {
    removeBookingQuestions(answeredQuestions);
    setQuestionAnswers(employeeAnswers, answeredQuestions);
    return answeredQuestions;
  }

  private static void removeBookingQuestions(AnsweredQuestions answeredQuestions) {
    if (Objects.nonNull(answeredQuestions.getCustomerReferenceManagement())
        && !R.equals(answeredQuestions.getCustomerReferenceManagement().getLocation())) {
      answeredQuestions.setCustomerReferenceManagement(null);
    }

    if (Objects.nonNull(answeredQuestions.getPurchaseOrderManagement())
        && !R.equals(answeredQuestions.getPurchaseOrderManagement().getLocation())) {
      answeredQuestions.setPurchaseOrderManagement(null);
    }

    if (Objects.nonNull(answeredQuestions.getUserDefinedQuestions())) {
      var registrationQuestions = answeredQuestions.getUserDefinedQuestions()
          .stream()
          .filter(question -> R.equals(question.getLocation()))
          .collect(Collectors.toList());
      answeredQuestions.setUserDefinedQuestions(registrationQuestions);
    }
  }

  private static void setQuestionAnswers(EmployeeAnswers employeeAnswers,
      AnsweredQuestions answeredQuestions) {
    if (Objects.isNull(employeeAnswers)) {
      return;
    }
    ofNullable(answeredQuestions.getCustomerReferenceManagement()).ifPresent(
        customerReferenceManagement -> customerReferenceManagement.setAnswer(
            employeeAnswers.getCustomerReferenceAnswer() == null ? "" : employeeAnswers.getCustomerReferenceAnswer()));

    ofNullable(answeredQuestions.getPurchaseOrderManagement()).ifPresent(
        purchaseOrderManagement -> purchaseOrderManagement.setAnswer(
            employeeAnswers.getPurchaseOrderAnswer() == null ? "" : employeeAnswers.getPurchaseOrderAnswer()));

    ofNullable(answeredQuestions.getUserDefinedQuestions()).ifPresent(userDefinedQuestions ->
        ofNullable(employeeAnswers.getUserDefinedAnswers()).ifPresent(userDefinedAnswers ->
            userDefinedQuestions.forEach(question -> userDefinedAnswers.stream()
                .filter(Objects::nonNull)
                .filter(answer -> StringUtils.equals(question.getQuestionId(), answer.getQuestionId()))
                .findFirst()
                .ifPresent(answer -> question.setUserDefinedAnswer(answer.getAnswer())))));
  }
}
