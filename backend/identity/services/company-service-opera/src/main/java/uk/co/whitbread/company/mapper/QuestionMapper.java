package uk.co.whitbread.company.mapper;

import static uk.co.whitbread.company.utils.EnumConverter.getEnum;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.company.model.AnswerType;
import uk.co.whitbread.company.model.AnsweredQuestions;
import uk.co.whitbread.company.model.BusinessQuestions;
import uk.co.whitbread.company.model.ManagementInformationAnswer;
import uk.co.whitbread.company.model.ManagementInformationQuestion;
import uk.co.whitbread.company.model.ManagementInformationQuestionAnswered;
import uk.co.whitbread.company.model.QuestionLocation;
import uk.co.whitbread.company.model.UserDefinedQuestion;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.CompanyManagementDetails;
import uk.co.whitbread.shared.cdh.model.company.CompanyQuestion;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.Answers;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

  @Mapping(target = "answerType", qualifiedByName = "toAnswerType", source = "type")
  @Mapping(target = "answers", qualifiedByName = "toAnswers", source = "answers")
  ManagementInformationAnswer toManagementInformationAnswer(Answers answers);

  @Mapping(target = "type", source = "answerType")
  @Mapping(target = "answerValues", source = "answers")
  Answers toAnswers(ManagementInformationAnswer response);

  @Mapping(target = "header", source = "managementHeader")
  @Mapping(target = "location", source = "location.value")
  @Mapping(target = "answers", source = "managementInformationAnswer")
  Question toQuestion(ManagementInformationQuestion response);

  List<ManagementInformationQuestion> toManagementInformationQuestion(
      List<GetQuestionResponse> response);

  @Mapping(target = "userDefinedQuestions", source = "questions")
  AnsweredQuestions toAnsweredQuestions(CompanyManagementDetails companyManagementDetails);

  @Mapping(target = "questionId", source = "id")
  @Mapping(target = "managementHeader", source = "header")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "managementInformationAnswer", source = "answers")
  @Mapping(target = "location", source = "location", qualifiedByName = "toQuestionLocation")
  @Mapping(target = "positionId", source = "position")
  UserDefinedQuestion toUserDefinedQuestion(GetQuestionResponse response);

  @Mapping(target = "questionId", source = "id")
  @Mapping(target = "managementHeader", source = "header")
  @Mapping(target = "managementInformationAnswer", source = "answers")
  @Mapping(target = "location", source = "location", qualifiedByName = "toQuestionLocation")
  @Mapping(target = "positionId", source = "position")
  ManagementInformationQuestionAnswered toManagementInformationQuestionAnswered(
      CompanyQuestion question);

  BusinessQuestions toBusinessQuestions(CompanyManagementDetails companyManagementDetails);

  @Mapping(target = "id", source = "questionId")
  @Mapping(target = "header", source = "managementHeader")
  @Mapping(target = "location", source = "location.value")
  @Mapping(target = "answers", source = "managementInformationAnswer")
  @Mapping(target = "position", source = "positionId")
  CompanyQuestion toCompanyQuestion(ManagementInformationQuestion response);

  @Mapping(target = "companyManagementDetails", expression = "java(toCompanyManagementDetailsWithUpdatedCustomerReference(getCompanyResponse.getCompanyManagementDetails(), customerReference))")
  CompanyAccountRequest toCompanyAccountRequestWithUpdatedCustomerReference(
      GetCompanyResponse getCompanyResponse, ManagementInformationQuestion customerReference);

  @Mapping(target = "customerReferenceManagement", source = "customerReferenceManagement")
  @Mapping(target = "questions", source="oldDetails.questions", qualifiedByName = "toCustomQuestion")
  CompanyManagementDetails toCompanyManagementDetailsWithUpdatedCustomerReference(
      CompanyManagementDetails oldDetails,
      ManagementInformationQuestion customerReferenceManagement);

  @Mapping(target = "companyManagementDetails", expression = "java(toCompanyManagementDetailsWithUpdatedPurchaseOrder(getCompanyResponse.getCompanyManagementDetails(), purchaseOrderManagement))")
  CompanyAccountRequest toCompanyAccountRequestWithUpdatedPurchaseOrder(
      GetCompanyResponse getCompanyResponse, ManagementInformationQuestion purchaseOrderManagement);

  @Mapping(target = "purchaseOrderManagement", source = "purchaseOrderManagement")
  @Mapping(target = "questions", source="oldDetails.questions", qualifiedByName = "toCustomQuestion")
  CompanyManagementDetails toCompanyManagementDetailsWithUpdatedPurchaseOrder(
      CompanyManagementDetails oldDetails, ManagementInformationQuestion purchaseOrderManagement);

  ManagementInformationQuestionAnswered toManagementInformationQuestionAnswered(ManagementInformationQuestion question);

  UserDefinedQuestion toUserDefinedQuestion(ManagementInformationQuestion question);

  @Named("toAnswerType")
  default AnswerType toAnswerType(String answerType) {
    return getEnum(AnswerType.class, answerType);
  }

  @Named("toQuestionLocation")
  default QuestionLocation toQuestionLocation(String questionLocation) {
    return getEnum(QuestionLocation.class, questionLocation);
  }

  @Named("toCustomQuestion")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "deleted", constant = "false")
  GetQuestionResponse toCustomQuestion(GetQuestionResponse customQuestion);

  @Named("toAnswers")
  default List<String> toAnswerList(Answers answers) {
    if (AnswerType.U.getValue().equalsIgnoreCase(answers.getType())) {
      return answers.getAnswerValues();
    }
    return null;
  }


}
