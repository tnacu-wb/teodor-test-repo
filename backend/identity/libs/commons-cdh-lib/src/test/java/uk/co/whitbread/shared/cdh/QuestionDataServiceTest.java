package uk.co.whitbread.shared.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.question.CreateQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;
import uk.co.whitbread.shared.cdh.model.question.Question;
import uk.co.whitbread.shared.cdh.model.question.UpdateQuestionResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;
import uk.co.whitbread.shared.cdh.properties.UriPaths;

@ExtendWith(MockitoExtension.class)
public class QuestionDataServiceTest {

  private static final String COMPANY_ACCOUNT_ID = "COMP5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String QUESTION_ID = "2376cc80-533a-454b-bd48-885e4736408d";
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";
  private static final String OK = "OK";
  private static final String STATUS_200 = "200";
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;
  @Mock
  private CustomerDataHubClient cdhClient;
  @InjectMocks
  private QuestionDataService questionDataService;

  @BeforeEach
  public void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  public void getQuestions_success() throws IOException {
    when(cdhClient.getListCDH(anyString(), any(HttpHeaders.class),
        eq(GetQuestionResponse.class)))
        .thenReturn(List.of(buildGetQuestionResponse()));

    var questions = questionDataService.getQuestions(COMPANY_ACCOUNT_ID, ACCESSED_BY);
    assertEquals(1, questions.size());
    assertEquals(QUESTION_ID, questions.get(0).getId());
  }

  @Test
  public void updateQuestion_success() throws IOException {
    var updateQuestionResponse = UpdateQuestionResponse.builder().message(OK).status(STATUS_200)
        .build();
    when(cdhClient.putCDH(any(), any(), any(), any(), any())).thenReturn(updateQuestionResponse);

    final var response = questionDataService
        .updateQuestion(COMPANY_ACCOUNT_ID, QUESTION_ID, buildQuestion(), ACCESSED_BY);

    assertNotNull(response);
    assertThat(response.getMessage(), is(OK));
    assertThat(response.getStatus(), is(STATUS_200));
  }

  @Test
  public void deleteQuestion_success() {
    when(cdhClient.deleteCDH(anyString(),
        any(HttpHeaders.class),
        eq(Void.class))).thenReturn(null);
    final var response = questionDataService
        .deleteQuestion(COMPANY_ACCOUNT_ID, QUESTION_ID, ACCESSED_BY);
    assertThat(response, nullValue());
  }

  @Test
  public void deleteQuestion_urlIsBuiltCorrectly() {
    questionDataService.deleteQuestion(COMPANY_ACCOUNT_ID, QUESTION_ID, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).deleteCDH(urlCaptor.capture(),
        any(HttpHeaders.class), eq(Void.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.QUESTIONS + "/" + QUESTION_ID;

    assertThat(url, is(expectedUrl));
  }

  @Test
  public void createQuestion_success() throws IOException {
    var position = 1;
    var createQuestionResponse = CreateQuestionResponse.builder()
        .id(QUESTION_ID)
        .position(position)
        .build();
    var question = buildQuestion();
    when(cdhClient.postCDH(anyString(), eq(question), any(HttpHeaders.class), eq(Question.class),
        eq(CreateQuestionResponse.class))).thenReturn(createQuestionResponse);

    var response = questionDataService
        .createQuestion(COMPANY_ACCOUNT_ID, question, ACCESSED_BY);

    assertNotNull(response);
    assertEquals(QUESTION_ID, response.getId());
    assertEquals(position, response.getPosition());
  }
  
  private GetQuestionResponse buildGetQuestionResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_question_response.json"),
        GetQuestionResponse.class);
  }

  private Question buildQuestion() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/question_request.json"),
        Question.class);
  }
}
