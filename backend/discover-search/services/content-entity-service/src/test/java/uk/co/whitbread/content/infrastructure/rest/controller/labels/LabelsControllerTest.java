package uk.co.whitbread.content.infrastructure.rest.controller.labels;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.labels.in.CategoryEnum;
import uk.co.whitbread.content.domain.model.labels.in.LabelsRequest;
import uk.co.whitbread.content.domain.model.labels.in.MultipleLabelsRequest;
import uk.co.whitbread.content.domain.model.labels.out.Extras;
import uk.co.whitbread.content.domain.model.labels.out.ExtrasLabel;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper.ExtrasDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper.LabelsRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.mapper.MultipleLabelsRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.CategoryEnumDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.LabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.in.MultipleLabelsRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.out.ExtrasDto;
import uk.co.whitbread.content.infrastructure.rest.controller.labels.model.out.ExtrasLabelDto;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationDto;

@ExtendWith(MockitoExtension.class)
class LabelsControllerTest {


  private static final String COUNTRY_GB = "gb";
  private static final String LANGUAGE_EN = "en";
  @InjectMocks
  LabelsController labelsControllerUnderTest;
  @Mock
  private ContentInPort contentInPort;
  @Mock
  private LabelsRequestDtoMapper labelsRequestDtoMapper;
  @Mock
  private MultipleLabelsRequestDtoMapper multipleLabelsRequestDtoMapper;
  @Mock
  private ExtrasDtoMapper extrasDtoMapper;

  @Test
  void getLabels__ShouldFindLabels() {
    //Arrange
    var labelsRequestDto = LabelsRequestDto.builder()
        .category(CategoryEnumDto.BOOKING)
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    var labelsRequest = LabelsRequest.builder()
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .category(CategoryEnum.BOOKING)
        .build();
    Mockito.when(labelsRequestDtoMapper.toDomainModel(labelsRequestDto)).thenReturn(labelsRequest);
    Mockito.when(contentInPort.getLabels(labelsRequest)).thenReturn(getLabels());

    //act
    var request = labelsRequestDtoMapper.toDomainModel(labelsRequestDto);
    var getLabelsResponse = contentInPort.getLabels(request);
    final Map<String, String> response = labelsControllerUnderTest.getLabels(labelsRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), getLabelsResponse.get("account.dashboard.and"),
        response.get("account.dashboard.and"));
    assertEquals(response.toString(), getLabelsResponse.get("account.dashboard.room"),
        response.get("account.dashboard.room"));
    assertEquals(response.toString(),
        getLabelsResponse.get("config.errorMessages.cardDetails.yourReference.valid"),
        response.get("config.errorMessages.cardDetails.yourReference.valid"));
  }

  @Test
  void getLabels__ShouldFindMultipleLabels() {
    //Arrange
    var multipleLabelsRequestDto = MultipleLabelsRequestDto.builder()
        .categories(of(
                CategoryEnumDto.BOOKING,
                CategoryEnumDto.MAIN,
                CategoryEnumDto.PI_PRE_CHECKIN,
                CategoryEnumDto.PI_GROUP_BOOKING,
                CategoryEnumDto.EXTRAS,
                CategoryEnumDto.PROMOTIONS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    MultipleLabelsRequest multipleLabelsRequest = MultipleLabelsRequest.builder()
        .categories(of(
                CategoryEnum.BOOKING,
                CategoryEnum.PI_BOOKINGS,
                CategoryEnum.MAIN,
                CategoryEnum.PI_PRE_CHECKIN,
                CategoryEnum.PI_GROUP_BOOKING,
                CategoryEnum.EXTRAS,
                CategoryEnum.PROMOTIONS))
        .country(COUNTRY_GB)
        .language(LANGUAGE_EN)
        .build();
    Mockito.when(multipleLabelsRequestDtoMapper.toDomainModel(multipleLabelsRequestDto))
        .thenReturn(multipleLabelsRequest);
    Mockito.when(contentInPort.getMultipleLabels(multipleLabelsRequest))
        .thenReturn(getMultipleLabels());

    //act
    var domainContentRequest = multipleLabelsRequestDtoMapper.toDomainModel(
        multipleLabelsRequestDto);
    var getMultipleLabelsResponse = contentInPort.getMultipleLabels(domainContentRequest);
    final Map<String, Map<String, String>> response = labelsControllerUnderTest.getLabels(
        multipleLabelsRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), getMultipleLabelsResponse.get("account.dashboard.and"),
        response.get("account.dashboard.and"));
  }

  @Test
  void getExtras__ShouldReturnExtras() {
    //Arrange
    var localizationRequestDto = LocalizationDto.builder()
            .country(COUNTRY_GB)
            .language(LANGUAGE_EN)
            .build();
    Mockito.when(extrasDtoMapper.toDto(getExtras())).thenReturn(getExtrasDto());
    Mockito.when(contentInPort.getExtras(COUNTRY_GB, LANGUAGE_EN)).thenReturn(getExtras());

    //act
    ResponseEntity<ExtrasLabelDto> response = labelsControllerUnderTest.getExtras(localizationRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertThat(response.getBody(), notNullValue());
    assertThat(response.getBody().getExtrasLabels(), hasSize(3));
  }

  private Map<String, String> getLabels() {
    Map<String, String> labels = new HashMap<>();
    labels.put("account.dashboard.and", "and");
    labels.put("account.dashboard.room", "Room");
    labels.put("config.errorMessages.cardDetails.yourReference.valid",
        "Customer Reference may not exceed 24 characters and may only contain letters, numbers, spaces, hyphens or dots. (ER9)");
    return labels;
  }

  Map<String, Map<String, String>> getMultipleLabels() {
    Map<String, Map<String, String>> multipleLabels = new HashMap<>();
    Map<String, String> multipleLabelsValue = new HashMap<>();
    multipleLabelsValue.put("account.dashboard.and", "and");
    multipleLabelsValue.put("account.dashboard.room", "Room");
    multipleLabelsValue.put("config.errorMessages.cardDetails.yourReference.valid",
        "Customer Reference may not exceed 24 characters and may only contain letters, numbers, spaces, hyphens or dots. (ER9)");
    multipleLabels.put("account.dashboard.and", multipleLabelsValue);
    return multipleLabels;
  }

  private ExtrasLabel getExtras() {
    return ExtrasLabel.builder().extrasLabels(List.of(
        Extras.builder().id("HSCKIN").name("Early check-in").order(1).build(),
        Extras.builder().id("HSCOU2").name("Late check-out").order(2).build(),
        Extras.builder().id("DBPROS").name("Bottle of prosecco").order(3).build())).build();
  }

  private ExtrasLabelDto getExtrasDto() {
    return ExtrasLabelDto.builder().extrasLabels(List.of(
        ExtrasDto.builder().id("HSCKIN").name("Early check-in").order(1).build(),
        ExtrasDto.builder().id("HSCOU2").name("Late check-out").order(2).build(),
        ExtrasDto.builder().id("DBPROS").name("Bottle of prosecco").order(3).build())).build();
  }
}