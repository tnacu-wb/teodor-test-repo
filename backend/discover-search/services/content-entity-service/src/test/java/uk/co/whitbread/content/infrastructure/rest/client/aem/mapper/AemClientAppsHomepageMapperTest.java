package uk.co.whitbread.content.infrastructure.rest.client.aem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;

import java.util.Objects;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsHomepageContentCard;
import uk.co.whitbread.content.domain.model.apps.homepage.out.AppsNotification;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsHomepageCardDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.apps.homepage.out.AppsNotificationDto;

@SpringBootTest
class AemClientAppsHomepageMapperTest {

  @Autowired
  private AemClientAppsHomepageMapper homepageMapper;
  private ListAppender<ILoggingEvent> listAppender;

  @BeforeEach
  void init() {
    final var log = (Logger) LoggerFactory.getLogger(AemClientAppsHomepageMapper.class);
    listAppender = new ListAppender<>();
    listAppender.start();
    log.addAppender(listAppender);
  }

  @ParameterizedTest
  @MethodSource
  void toCardModelEmptyList_test(List<AppsHomepageCardDto> input) {
    assertNotNull(input);
    //Act
    List<AppsHomepageCard> result = homepageMapper.toValidCardModel(input);
    //Assert
    assertEquals(0, result.size());
    String message = listAppender.list.get(0).getFormattedMessage();
    assert (message).contains("Missing the imagePath");
  }

  static Stream<List<AppsHomepageCardDto>> toCardModelEmptyList_test() {
    var cardEmptyDto = getCardEmptyDto();
    var cardNullDto = getCardNullDto();
    return Stream.of(List.of(cardEmptyDto), List.of(cardNullDto));
  }

  @Test
  void toCardModelEqualsList_test() {
    //Arrange
    var cardDto = getHomepageAppsCardDto();
    var list = List.of(cardDto);
    var expected = getHomepageAppsCard();
    //Act
    List<AppsHomepageCard> result = homepageMapper.toValidCardModel(list);
    //Assert
    assertEquals(1, result.size());
    assertEquals(expected, result.get(0));
    assertEquals(0, listAppender.list.size());
  }

  @ParameterizedTest
  @MethodSource
  void toCardModelNotEqualsList_test(List<AppsHomepageCardDto> input) {
    assertNotNull(input);
    //Arrange
    var expected = getHomepageAppsCard();
    //Act
    List<AppsHomepageCard> result = homepageMapper.toValidCardModel(input);
    //Assert
    assertEquals(1, result.size());
    assertEquals(expected, result.get(0));
    String message = listAppender.list.get(0).getFormattedMessage();
    assert (message).contains("Missing the imagePath");
  }


  static Stream<List<AppsHomepageCardDto>> toCardModelNotEqualsList_test() {
    var cardDto = getHomepageAppsCardDto();
    var cardNullDto = getCardNullDto();
    var cardEmptyDto = getCardEmptyDto();
    return Stream.of(List.of(cardEmptyDto, cardDto), List.of(cardDto, cardNullDto));
  }

  @ParameterizedTest
  @MethodSource
  void toContentCardModelEmptyList_test(List<AppsHomepageCardDto> input) {
    assertNotNull(input);
    //Act
    List<AppsHomepageContentCard> result = homepageMapper.toValidContentCardModel(input);
    //Assert
    assertEquals(0, result.size());
    String message = listAppender.list.get(0).getFormattedMessage();
    assert (message).contains("Missing the imagePath");
  }

  static Stream<List<AppsHomepageCardDto>> toContentCardModelEmptyList_test() {
    var cardEmptyDto = getContentCardEmptyDto();
    var cardNullDto = getContentCardNullDto();
    var cardNoOrderDto = getContentCardNoOrderDto();
    return Stream.of(List.of(cardEmptyDto), List.of(cardNullDto), List.of(cardNoOrderDto));
  }

  @Test
  void toContentCardModelEqualsList_test() {
    //Arrange
    var cardDto = getHomepageAppsContentCardDto();
    var list = List.of(cardDto);
    var expected = getHomepageAppsContentCard();
    //Act
    List<AppsHomepageContentCard> result = homepageMapper.toValidContentCardModel(list);
    //Assert
    assertEquals(1, result.size());
    assertEquals(expected, result.get(0));
    assertEquals(0, listAppender.list.size());
  }

  @ParameterizedTest
  @MethodSource
  void toContentCardModelNotEqualsList_test(List<AppsHomepageCardDto> input) {
    assertNotNull(input);
    //Arrange
    var expected = getHomepageAppsContentCard();
    //Act
    List<AppsHomepageContentCard> result = homepageMapper.toValidContentCardModel(input);
    //Assert
    assertEquals(1, result.size());
    assertEquals(expected, result.get(0));
    String message = listAppender.list.get(0).getFormattedMessage();
    assert (message).contains("Missing the imagePath");
  }

  static Stream<List<AppsHomepageCardDto>> toContentCardModelNotEqualsList_test() {
    var cardDto = getHomepageAppsContentCardDto();
    var cardNullDto = getContentCardNullDto();
    var cardEmptyDto = getContentCardEmptyDto();
    return Stream.of(List.of(cardEmptyDto, cardDto), List.of(cardDto, cardNullDto));
  }

  @ParameterizedTest
  @MethodSource
  void toNotification_test(AppsNotificationDto input) {
    //Act
    AppsNotification result = homepageMapper.toValidNotificationModel(input);
    //Assert
    if (Objects.isNull(input)) {
      assertNull(result);
    } else if (Strings.isBlank(input.getMessage()) || Strings.isBlank(input.getType())) {
      String logMess = listAppender.list.get(0).getFormattedMessage();
      assert (logMess).contains("Missing the type or message");
      assertNull(result);
    } else {
      assertEquals(input.getMessage(), result.getMessage());
      assertEquals(input.getType(), result.getType());
    }
  }

  static Stream<AppsNotificationDto> toNotification_test() {
    AppsNotificationDto dto = AppsNotificationDto.builder().type("type").message("message").build();
    return Stream.of(AppsNotificationDto.builder().type("type").build(),
        AppsNotificationDto.builder().message("message").build(), dto, null);
  }

  private static AppsHomepageCardDto getCardEmptyDto() {
    return AppsHomepageCardDto.builder().title("").build();
  }

  private static AppsHomepageCardDto getCardNullDto() {
    return AppsHomepageCardDto.builder().build();
  }

  private static AppsHomepageCardDto getContentCardNullDto() {
    return AppsHomepageCardDto.builder().build();
  }

  private static AppsHomepageCardDto getContentCardEmptyDto() {
    return AppsHomepageCardDto.builder().title("").order(1).build();
  }

  private static AppsHomepageCardDto getContentCardNoOrderDto() {
    return AppsHomepageCardDto.builder().title("x").build();
  }

  private static AppsHomepageCardDto getHomepageAppsContentCardDto() {
    return AppsHomepageCardDto.builder().imagePath("path").order(1).build();
  }

  private static AppsHomepageCardDto getHomepageAppsCardDto() {
    return AppsHomepageCardDto.builder()
        .imagePath("path")
        .title("title")
        .trackingId("id")
        .order(1)
        .build();
  }

  private static AppsHomepageCard getHomepageAppsCard() {
    return AppsHomepageCard.builder()
        .title("title")
        .imagePath("path")
        .trackingId("id")
        .order(1)
        .build();
  }

  private static AppsHomepageContentCard getHomepageAppsContentCard() {
    return AppsHomepageContentCard.builder()
        .imagePath("path")
        .order(1)
        .build();
  }
}

