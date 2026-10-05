package uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomOccupancyRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccuRuleResp;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyData;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomOccupancyRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyDataDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyResponseDto;

class MaxRoomOccupancyDtoMapperTest {

  private final MaxRoomOccupancyDtoMapper maxRoomOccupancyDtoMapper = new MaxRoomOccupancyDtoMapperImpl();

  @Test
  void toModel_givenDtoObject_shouldTransformToModelObject() {
    //Arrange
    var requestDto = MaxRoomOccupancyRequestDto.builder()
        .channelId("CCUI")
        .build();
    var request = MaxRoomOccupancyRequest.builder()
        .channelId("CCUI")
        .brand("PI")
        .build();

    //Act
    var result = maxRoomOccupancyDtoMapper.toModel(requestDto);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(request);
  }

  @Test
  void toModel_givenNullDtoObject_shouldReturnNullModelObject() {
    //Arrange

    //Act
    var result = maxRoomOccupancyDtoMapper.toModel(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenDomainObject_shouldTransformToDtoObject() {
    //Arrange
    var time = LocalDateTime.now();

    var responseDto = MaxRoomOccupancyResponseDto.builder()
        .channelId("PI")
        .brand("PI")
        .roomOccupancies(of(MaxRoomOccupancyDataDto.builder()
                .adultsNumber(2)
                .childrenNumber(0)
                .acceptedRoomTypes(of("DB","TWIN","DIS"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(2)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(2)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(1)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(1)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(1)
                .childrenNumber(0)
                .acceptedRoomTypes(of("SB", "DB", "DIS"))
                .build()))
        .generatedAt(time)
        .build();

    var response = MaxRoomOccuRuleResp.builder()
        .maxOccupancyData(of(MaxRoomOccupancyData.builder()
                .adultsNumber(2)
                .childrenNumber(0)
                .acceptedRoomTypes(of("DB","TWIN","DIS"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(2)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(2)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(1)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(1)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(1)
                .childrenNumber(0)
                .acceptedRoomTypes(of("SB", "DB", "DIS"))
                .build()))
        .channelId("PI")
        .brand("PI")
        .generatedAt(time)
        .build();

    //Act
    var result = maxRoomOccupancyDtoMapper.toDto(response);

    //Assert
    assertThat(result).usingRecursiveComparison()
        .withStrictTypeChecking()
        .isEqualTo(responseDto);
  }

  @Test
  void toDto_givenNullDomainObject_shouldReturnNullObject() {
    //Arrange

    //Act
    var result = maxRoomOccupancyDtoMapper.toDto(null);

    //Assert
    assertThat(result).isNull();
  }

  @Test
  void toDto_givenNullListOfMaxOccupancyData_shouldTransformToDtoObjectWithNullMaxOccupancyData()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();

    var response = MaxRoomOccuRuleResp.builder()
        .channelId("PI")
        .brand("PI")
        .maxOccupancyData(Collections.emptyList())
        .generatedAt(time)
        .build();

    FieldUtils.writeField(response,
        "maxOccupancyData",
        null,
        true);

    //Act
    var result = maxRoomOccupancyDtoMapper.toDto(response);

    //Assert
    assertThat(result.getRoomOccupancies()).isNull();
  }

  @Test
  void toDto_givenListOfNullMaxOccupancyData_shouldTransformToDtoObjectWithNullMaxOccupancyData()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();

    var maxOccupancyData = new ArrayList<MaxRoomOccupancyData>();
    maxOccupancyData.add(null);

    var response = MaxRoomOccuRuleResp.builder()
        .channelId("PI")
        .brand("PI")
        .maxOccupancyData(maxOccupancyData)
        .generatedAt(time)
        .build();

    //Act
    var result = maxRoomOccupancyDtoMapper.toDto(response);

    //Assert
    assertThat(result.getRoomOccupancies().get(0)).isNull();
  }

  @Test
  void toDto_givenNullListOfAcceptedRoomTypes_shouldTransformToDtoObjectWithNullAcceptedRoomTypes()
      throws IllegalAccessException {
    //Arrange
    var time = LocalDateTime.now();

    var response = MaxRoomOccuRuleResp.builder()
        .maxOccupancyData(of(MaxRoomOccupancyData.builder()
            .adultsNumber(2)
            .childrenNumber(0)
            .acceptedRoomTypes(Collections.emptyList())
            .build()))
        .channelId("PI")
        .brand("PI")
        .generatedAt(time)
        .build();

    FieldUtils.writeField(response.getMaxOccupancyData().get(0),
        "acceptedRoomTypes",
        null,
        true);

    //Act
    var result = maxRoomOccupancyDtoMapper.toDto(response);

    //Assert
    assertThat(result.getRoomOccupancies().get(0).getAcceptedRoomTypes()).isNull();
  }

}
