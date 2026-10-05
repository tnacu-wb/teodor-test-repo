package uk.co.whitbread.content.infrastructure.rest.controller.globalconfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.globalconfig.in.GlobalConfigRequest;
import uk.co.whitbread.content.domain.model.globalconfig.out.AcceptedRoomTypes;
import uk.co.whitbread.content.domain.model.globalconfig.out.GlobalConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassConfig;
import uk.co.whitbread.content.domain.model.globalconfig.out.RoomClassOrder;
import uk.co.whitbread.content.domain.model.globalconfig.out.SearchRules;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.in.GlobalConfigRequestDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.GlobalConfigDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.GlobalConfigRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.RoomClassConfigDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.mapper.SearchRulesDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.AcceptedRoomTypesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.GlobalConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomClassConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.RoomClassOrderDto;
import uk.co.whitbread.content.infrastructure.rest.controller.globalconfig.model.out.SearchRulesDto;

@ExtendWith(MockitoExtension.class)
class GlobalConfigControllerTest {

  @InjectMocks
  GlobalConfigController globalConfigControllerUnderTest;

  @Mock
  private ContentInPort contentInPort;

  @Mock
  GlobalConfigRequestDtoMapper globalConfigRequestDtoMapper;

  @Mock
  SearchRulesDtoMapper searchRulesDtoMapper;

  @Mock
  RoomClassConfigDtoMapper roomClassConfigDtoMapper;

  @Mock
  GlobalConfigDtoMapper globalConfigDtoMapper;

  @Test
  void getSearchRules__ShouldReturnOK() {
    //Arrange
    GlobalConfigRequestDto globalConfigRequestDto = getGlobalConfigRequestDto();

    GlobalConfigRequest globalConfigRequest = getGlobalConfigRequest();

    Mockito.when(globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto))
        .thenReturn(globalConfigRequest);
    Mockito.when(contentInPort.getSearchRules(globalConfigRequest)).thenReturn(getSearchRules());
    Mockito.when(searchRulesDtoMapper.toDtoModel(getSearchRules())).thenReturn(getSearchRulesDto());

    //Act
    final ResponseEntity<SearchRulesDto> response =
        globalConfigControllerUnderTest.getSearchRules(globalConfigRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  private static GlobalConfigRequest getGlobalConfigRequest() {
    return GlobalConfigRequest.builder()
        .country("gb").language("en").channelId("DISTR").brand("PI").build();
  }

  @Test
  void getSearchRules__ShouldFindHeaderData() {
    //Arrange
    GlobalConfigRequestDto globalConfigRequestDto = getGlobalConfigRequestDto();

    GlobalConfigRequest globalConfigRequest = getGlobalConfigRequest();

    Mockito.when(globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto))
        .thenReturn(globalConfigRequest);
    Mockito.when(contentInPort.getSearchRules(globalConfigRequest)).thenReturn(getSearchRules());
    Mockito.when(searchRulesDtoMapper.toDtoModel(getSearchRules())).thenReturn(getSearchRulesDto());

    //Act
    final GlobalConfigRequest request = globalConfigRequestDtoMapper.toDomainModel(
        globalConfigRequestDto);
    final SearchRules searchRules = contentInPort.getSearchRules(request);
    final SearchRulesDto domainContentRequest = searchRulesDtoMapper.toDtoModel(searchRules);
    final ResponseEntity<SearchRulesDto> response =
        globalConfigControllerUnderTest.getSearchRules(globalConfigRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(),
        domainContentRequest.getMaxNights(), response.getBody().getMaxNights());
    assertEquals(response.toString(),
        domainContentRequest.getMaxRooms(), response.getBody().getMaxRooms());
    assertEquals(response.toString(),
        domainContentRequest.getMaxArrivalDate(), response.getBody().getMaxArrivalDate());
    assertEquals(response.toString(),
        domainContentRequest.getRoomOccupancies(), response.getBody().getRoomOccupancies());
  }

  @Test
  void getSearchRules__ShouldNotFindHeaderData() {
    //Arrange
    GlobalConfigRequestDto globalConfigRequestDto = getGlobalConfigRequestDto();

    GlobalConfigRequest globalConfigRequest = getGlobalConfigRequest();

    Mockito.when(globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto))
        .thenReturn(globalConfigRequest);
    Mockito.when(contentInPort.getSearchRules(globalConfigRequest)).thenReturn(getSearchRules());
    Mockito.when(searchRulesDtoMapper.toDtoModel(getSearchRules())).thenReturn(getSearchRulesDto());

    //Act
    final GlobalConfigRequest request = globalConfigRequestDtoMapper.toDomainModel(
        globalConfigRequestDto);
    final SearchRules searchRules = contentInPort.getSearchRules(request);
    final SearchRulesDto domainContentRequest = searchRulesDtoMapper.toDtoModel(searchRules);
    final ResponseEntity<SearchRulesDto> response =
        globalConfigControllerUnderTest.getSearchRules(
            GlobalConfigRequestDto.builder().brand("PI").build()
        );

    //Assert
    Assertions.assertNull(response.getBody());
  }

  @Test
  void getRoomClassConfig__ShouldReturnConfigData() {
    //Arrange
    GlobalConfigRequestDto globalConfigRequestDto = getGlobalConfigRequestDto();

    GlobalConfigRequest globalConfigRequest = getGlobalConfigRequest();

    Mockito.when(globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto))
        .thenReturn(globalConfigRequest);
    Mockito.when(contentInPort.getRoomClassConfig(globalConfigRequest)).thenReturn(getRoomClassConfig());
    Mockito.when(roomClassConfigDtoMapper.toDtoModel(getRoomClassConfig())).thenReturn(getRoomClassConfigDto());

    //Act
    final ResponseEntity<RoomClassConfigDto> response =
        globalConfigControllerUnderTest.getRoomClassConfig(globalConfigRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    final RoomClassConfigDto roomClassConfigDto = response.getBody();
    assertThat(roomClassConfigDto, notNullValue());
    final List<RoomClassOrderDto> roomClassConfig = roomClassConfigDto.getRoomClassConfig();
    assertThat(roomClassConfig, hasSize(2));
    final RoomClassOrderDto roomClassOrderDto1 = roomClassConfig.get(0);
    assertThat(roomClassOrderDto1.getCode(), is("ON"));
    assertThat(roomClassOrderDto1.getOrder(), is(1));
    final RoomClassOrderDto roomClassOrderDto2 = roomClassConfig.get(1);
    assertThat(roomClassOrderDto2.getCode(), is("TW"));
    assertThat(roomClassOrderDto2.getOrder(), is(2));
  }

  @Test
  void getGlobalConfig__ShouldReturnOK() {
    //Arrange
    var globalConfigRequestDto = getGlobalConfigRequestDto();
    var globalConfigRequest = getGlobalConfigRequest();
    var globalConfig = getGlobalConfig();
    Mockito.when(globalConfigRequestDtoMapper.toDomainModel(globalConfigRequestDto))
        .thenReturn(globalConfigRequest);
    Mockito.when(contentInPort.getGlobalConfig(globalConfigRequest)).thenReturn(getGlobalConfig());
    Mockito.when(globalConfigDtoMapper.toDtoModel(globalConfig)).thenReturn(getGlobalConfigDto());

    //Act
    final var response = globalConfigControllerUnderTest.getGlobalConfig(globalConfigRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  private static GlobalConfigRequestDto getGlobalConfigRequestDto() {
    return GlobalConfigRequestDto.builder()
        .country("gb").language("en").channelId("DISTR").brand("PI").build();
  }

  private SearchRules getSearchRules() {
    return SearchRules.builder()
        .maxRooms(4)
        .maxNights(4)
        .maxArrivalDate(364)
        .roomOccupancies(getAcceptedRoomTypes())
        .build();
  }

  private SearchRulesDto getSearchRulesDto() {
    SearchRulesDto searchRulesDto = new SearchRulesDto();
    searchRulesDto.setMaxNights(4);
    searchRulesDto.setMaxRooms(5);
    searchRulesDto.setMaxArrivalDate(364);
    searchRulesDto.setRoomOccupancies(getAcceptedRoomTypesDto());
    return searchRulesDto;
  }

  private List<AcceptedRoomTypesDto> getAcceptedRoomTypesDto() {
    AcceptedRoomTypesDto acceptedRoomTypes =
        AcceptedRoomTypesDto.builder()
            .acceptedRoomTypes(List.of("DIST", "FAM"))
            .adultsNumber(2)
            .childrenNumber(0)
            .build();
    return List.of(acceptedRoomTypes);
  }

  private List<AcceptedRoomTypes> getAcceptedRoomTypes() {
    AcceptedRoomTypes acceptedRoomTypes = AcceptedRoomTypes.builder()
        .acceptedRoomTypes(List.of("DIST", "FAM"))
        .adultsNumber(2)
        .childrenNumber(0)
        .build();
    return List.of(acceptedRoomTypes);
  }

  private RoomClassConfig getRoomClassConfig() {
    RoomClassOrder roomClassOrder1 = RoomClassOrder.builder().code("ON").order(1).build();
    RoomClassOrder roomClassOrder2 = RoomClassOrder.builder().code("TW").order(2).build();
    return RoomClassConfig.builder().roomClassConfig(List.of(roomClassOrder1, roomClassOrder2))
        .build();
  }

  private RoomClassConfigDto getRoomClassConfigDto() {
    RoomClassOrderDto roomClassOrderDto1 = RoomClassOrderDto.builder().code("ON").order(1).build();
    RoomClassOrderDto roomClassOrderDto2 = RoomClassOrderDto.builder().code("TW").order(2).build();
    return RoomClassConfigDto.builder()
        .roomClassConfig(List.of(roomClassOrderDto1, roomClassOrderDto2)).build();
  }

  private GlobalConfig getGlobalConfig() {
    return GlobalConfig.builder()
        .maxRoomsLim(getSearchRules())
        .roomClassConfig(getRoomClassConfig().getRoomClassConfig())
        .build();
  }

  private GlobalConfigDto getGlobalConfigDto() {
    return GlobalConfigDto.builder()
        .maxRoomsLim(getSearchRulesDto())
        .roomClassConfig(getRoomClassConfigDto().getRoomClassConfig())
        .build();
  }
}
