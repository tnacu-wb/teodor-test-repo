package uk.co.whitbread.company.utils;

import org.springframework.stereotype.Component;
import uk.co.whitbread.company.mapper.QuestionMapper;
import uk.co.whitbread.company.model.ManagementInformationQuestion;
import uk.co.whitbread.company.model.ManagementInformationQuestionAnswered;
import uk.co.whitbread.company.model.UserDefinedQuestion;

@Component
public class RegistrationQuestionsTransformer {

  private final QuestionMapper questionMapper;

  public RegistrationQuestionsTransformer(QuestionMapper questionMapper) {
    this.questionMapper = questionMapper;
  }

  public UserDefinedQuestion transformToUserDefinedQuestion(
      ManagementInformationQuestion question) {
    return questionMapper.toUserDefinedQuestion(question);
  }

  public ManagementInformationQuestionAnswered transformToManagementInformationQuestionAnswered(
      ManagementInformationQuestion question) {
    return questionMapper.toManagementInformationQuestionAnswered(question);
  }

}
