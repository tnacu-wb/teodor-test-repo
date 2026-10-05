package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.COMPANY;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.QUESTION;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.QUESTIONS;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.model.question.CreateQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;
import uk.co.whitbread.shared.cdh.model.question.UpdateQuestionResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionDataService {

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;

  public List<GetQuestionResponse> getQuestions(String companyAccountId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + QUESTIONS);
    return cdhClient.getListCDH(
        builder.buildAndExpand(companyAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetQuestionResponse.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyAccountId")
  public UpdateQuestionResponse updateQuestion(String companyAccountId,
      String questionId, Question question, String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + QUESTION);
    return cdhClient
        .putCDH(builder.buildAndExpand(companyAccountId, questionId).toUriString(), question,
            buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
            Question.class, UpdateQuestionResponse.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyAccountId")
  public Void deleteQuestion(String companyAccountId, String questionId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + QUESTION);
    return cdhClient.deleteCDH(
        builder.buildAndExpand(companyAccountId, questionId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy), Void.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyAccountId")
  public CreateQuestionResponse createQuestion(String companyAccountId, Question question,
      String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY + QUESTIONS);
    return cdhClient
        .postCDH(builder.buildAndExpand(companyAccountId).toUriString(), question,
            buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
            Question.class, CreateQuestionResponse.class);
  }
}
