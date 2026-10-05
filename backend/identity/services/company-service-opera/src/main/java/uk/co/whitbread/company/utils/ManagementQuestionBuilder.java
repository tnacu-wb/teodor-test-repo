package uk.co.whitbread.company.utils;

import uk.co.whitbread.company.model.ManagementInformationQuestion;

public class ManagementQuestionBuilder {

  private static ManagementInformationQuestion question;

  private ManagementQuestionBuilder() {
  }

  public static ManagementInformationQuestion getEmptyQuestion() {
    if (question == null) {
      question = new ManagementInformationQuestion();
    }

    return question;
  }
}