package uk.co.whitbread.infrastructure.rest.client.availabilitycache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.availability.in.BookingChannel;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.in.RateV2;
import uk.co.whitbread.domain.model.availability.in.Room;
import uk.co.whitbread.domain.model.hotel.out.RoomTypeInfo;
import uk.co.whitbread.domain.model.hotel.out.RoomTypesInfo;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.in.LocationFormatEnum;
import uk.co.whitbread.domain.model.srp.in.OldWorldChannelEnum;
import uk.co.whitbread.domain.model.srp.in.RadiusUnitEnum;
import uk.co.whitbread.domain.model.srp.out.Cost;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilityResponse;
import uk.co.whitbread.domain.ports.primary.HotelInfoInPort;
import uk.co.whitbread.infrastructure.rest.client.AvailabilityCacheV1Client;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.AvailabilityCacheV1SearchOutPortImpl;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1RequestMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1ResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.mapper.AvailabilityCacheV1ResponseMapperImpl;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.AvailableCostsDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelAvailabilitiesDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.HotelOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RatePlanOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.distr.out.RoomOperaDistrDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.in.AvailabilityCacheRequestV1;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelAvailabilitiesDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.HotelOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RatePlanOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.RoomOperaDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.model.out.TotalCostDto;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.AvailabilityProperties;
import uk.co.whitbread.infrastructure.rest.client.availabilitycachev1.properties.DistributionAvailabilityProperties;

@ExtendWith(MockitoExtension.class)
public class AvailabilityCacheV1SearchOutPortImplTest {

  public static final LocalDate ARRIVAL_DATE_V2 = LocalDate.now();
  public static final LocalDate DEPARTURE_DATE_V2 = LocalDate.now().plusDays(1);

  @InjectMocks
  AvailabilityCacheV1SearchOutPortImpl outPort;
  @Mock
  private AvailabilityCacheV1RequestMapper availabilityCacheV1RequestMapper;
  @Mock
  private AvailabilityCacheV1ResponseMapper availabilityCacheV1ResponseMapper;
  @Mock
  private AvailabilityCacheV1Client availabilityCacheV1Client;
  @Mock
  private HotelInfoInPort hotelInfoInPort;
  @Mock
  private AvailabilityProperties properties;
  @Mock
  private DistributionAvailabilityProperties distProperties;
  @InjectMocks
  private AvailabilityCacheV1SearchOutPortImpl availabilityCacheV1SearchOutPort;

  @Test
  void getHotelAvailabilityByIdsFromAvCache_shouldReturnOK() {
    //Arrange
    when(availabilityCacheV1RequestMapper.toDtoByIds(any(HotelAvailabilityByIdsRequest.class)))
            .thenReturn(createAvailabilityCacheV1Request());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(any(AvailabilityCacheRequestV1.class),any()))
            .thenReturn(createAvailabilityCacheResponseDistr());
    when(hotelInfoInPort.getRoomTypesInfo(any()))
            .thenReturn(createRoomTypesInfo());

    //Act
    var availabilityResponse = outPort
            .getHotelAvailabilityByIdsFromAvCache(createRequestByIds("DB"),
                    List.of(RoomSubstitutionRuleResponse.builder()
                            .requestDetails(RoomSubstitutionRequestDetails.builder()
                                    .roomType("DB")
                                    .build())
                            .substitutionList(List.of(RoomSubstitution.builder()
                                    .type("DOUBLE")
                                    .silent(true)
                                    .build()))
                            .build()));

    //Assert
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailability().get(0).getHotelId(), is("LONLEI"));
  }

  @Test
  void getHotelAvailabilityByIdsV2FromAvCache_shouldReturnOK() {
    //Arrange
    when(availabilityCacheV1RequestMapper.toDtoByIdsV2(any(HotelAvailabilityByIdsV2Request.class)))
            .thenReturn(createAvailabilityCacheV1RequestForV2());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(any(AvailabilityCacheRequestV1.class), any()))
            .thenReturn(createAvailabilityCacheResponseDistrV2());
    when(hotelInfoInPort.getRoomTypesInfo(any()))
            .thenReturn(createRoomTypesInfoV2());

    //Act
    var availabilityResponse = outPort
            .getHotelAvailabilityByIdsV2FromAvCache(createRequestByIdsV2(),
                    List.of(RoomSubstitutionRuleResponse.builder()
                            .requestDetails(RoomSubstitutionRequestDetails.builder()
                                    .adults(2)
                                    .children(0)
                                    .cotRequired(false)
                                    .roomType("DB")
                                    .build())
                            .substitutionList(List.of(RoomSubstitution.builder()
                                    .type("DOUBLE")
                                    .silent(true)
                                    .build()))
                            .build()));

    //Assert
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailability().get(0).getHotelId(), is("TESTHOTEL"));
  }


  @Test
  void getHotelAvailabilityByIdsFromAvCache_accessible_shouldReturnOK() {
    //Arrange
    when(availabilityCacheV1RequestMapper.toDtoByIds(any(HotelAvailabilityByIdsRequest.class)))
            .thenReturn(createAvailabilityCacheV1Request());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(any(AvailabilityCacheRequestV1.class), any()))
            .thenReturn(createAvailabilityCacheResponseDistr());
    when(hotelInfoInPort.getRoomTypesInfo(any()))
            .thenReturn(createRoomTypesInfo());

    //Act
    var availabilityResponse = outPort
            .getHotelAvailabilityByIdsFromAvCache(createRequestByIds("DIS"),
                    List.of(RoomSubstitutionRuleResponse.builder()
                            .requestDetails(RoomSubstitutionRequestDetails.builder()
                                    .roomType("DIS")
                                    .build())
                            .substitutionList(List.of(RoomSubstitution.builder()
                                    .type("DOUBLE")
                                    .silent(true)
                                    .build()))
                            .build()));

    //Assert
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailability().get(0).getHotelId(), is("LONLEI"));
  }

  @Test
  void getHotelAvailabilityByIdsFromAvCache__noAvailability() {
    //Arrange
    when(availabilityCacheV1RequestMapper.toDtoByIds(any(HotelAvailabilityByIdsRequest.class)))
            .thenReturn(createAvailabilityCacheV1Request());
    var avCacheResponse = new HotelAvailabilitiesDistrDto();
    avCacheResponse.setOperaHotelAvailabilities(Collections.emptyList());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(any(AvailabilityCacheRequestV1.class), any()))
            .thenReturn(avCacheResponse);
    when(hotelInfoInPort.getRoomTypesInfo(any()))
            .thenReturn(createRoomTypesInfo());

    //Act
    var availabilityResponse = outPort
            .getHotelAvailabilityByIdsFromAvCache(createRequestByIds("DB"),
                    List.of(RoomSubstitutionRuleResponse.builder()
                            .requestDetails(RoomSubstitutionRequestDetails.builder()
                                    .roomType("DB")
                                    .build())
                            .substitutionList(List.of(RoomSubstitution.builder()
                                    .type("DOUBLE")
                                    .silent(true)
                                    .build()))
                            .build()));

    //Assert
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailability().size(), is(0));
  }

  private Map<String, RoomTypesInfo> createRoomTypesInfo() {
    return Map.of("LONLEI", RoomTypesInfo.builder()
            .roomType(List.of(RoomTypeInfo.builder()
                    .roomClass("ST")
                    .roomType("DOUBLE")
                    .build()))
            .build());
  }

  private Map<String, RoomTypesInfo> createRoomTypesInfoV2() {
    return Map.of("TESTHOTEL", RoomTypesInfo.builder()
            .roomType(List.of(RoomTypeInfo.builder()
                    .roomClass("ST")
                    .roomType("DOUBLE")
                    .numberOfRooms("34")
                    .build()))
            .build());
  }

  private Map<String, RoomTypesInfo> createRoomTypesInfoV3() {
    var roomTypesInfo = RoomTypesInfo.builder()
            .roomType(List.of(
                            RoomTypeInfo.builder().roomClass("ST").roomType("DOUBLE").numberOfRooms("10").build(),
                            RoomTypeInfo.builder().roomClass("ST").roomType("FMTRPL").numberOfRooms("5").build(),
                            RoomTypeInfo.builder().roomClass("ST").roomType("WETDBL").numberOfRooms("14").build()
                    )
            )
            .build();
    return Map.of("LONLEI", roomTypesInfo, "LONEUS", roomTypesInfo);
  }

  private HotelAvailabilitiesDistrDto createAvailabilityCacheResponseDistr() {
    return new HotelAvailabilitiesDistrDto(2, createOperaHotelsDistr());
  }

  private HotelAvailabilitiesDistrDto createAvailabilityCacheResponseDistrV2() {
    return new HotelAvailabilitiesDistrDto(1, createOperaHotelsDistrV2());
  }

  private List<HotelOperaDistrDto> createOperaHotelsDistr() {
    List<HotelOperaDistrDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelOperaDistrDto("LONLEI", "Test Hotel Name", "pi",
            true, false, false, false, "OPERA", createRatesListDistr()));
    return hotelDtoList;
  }

  private List<HotelOperaDistrDto> createOperaHotelsDistrV2() {
    List<HotelOperaDistrDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelOperaDistrDto("TESTHOTEL", "Test Hotel Name", "pi",
            true, false, false, false, "OPERA", createRatesListDistrV2()));
    return hotelDtoList;
  }

  private List<RatePlanOperaDistrDto> createRatesListDistr() {
    List<RatePlanOperaDistrDto> ratePlanDtoList = new LinkedList<>();
    List<RoomOperaDistrDto> rooms1 = List.of(RoomOperaDistrDto.builder()
            .roomType("DOUBLE")
            .availableCosts(List.of(AvailableCostsDistrDto.builder()
                    .date("2023-06-01")
                    .amount(BigDecimal.valueOf(500))
                    .qtyAvailable(5)
                    .build()))
            .build());
    ratePlanDtoList.add(new RatePlanOperaDistrDto("A",
            "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
            "FLEXRATE", "1", "aaa", rooms1));
    return ratePlanDtoList;
  }

  private List<RatePlanOperaDistrDto> createRatesListDistrV2() {
    List<RatePlanOperaDistrDto> ratePlanDtoList = new LinkedList<>();
    List<RoomOperaDistrDto> rooms1 = List.of(RoomOperaDistrDto.builder()
            .roomType("DOUBLE")
            .availableCosts(List.of(AvailableCostsDistrDto.builder()
                    .date("2023-06-01")
                    .amount(BigDecimal.valueOf(500))
                    .qtyAvailable(5)
                    .build()))
            .build());
    ratePlanDtoList.add(new RatePlanOperaDistrDto("FLEXRATE",
            "Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival",
            "FLEXRATE", "1", "aaa", rooms1));
    return ratePlanDtoList;
  }

  private HotelAvailabilityByIdsRequest createRequestByIds(String roomType) {
    return HotelAvailabilityByIdsRequest.builder()
            .hotelIds(new ArrayList<>(List.of("TEST")))
            .roomTypes(List.of(roomType))
            .adultsNumber(List.of(1))
            .arrivalDate(ARRIVAL_DATE_V2.toString())
            .departureDate(DEPARTURE_DATE_V2.toString())
            .childrenNumber(List.of(0))
            .cotsRequired(List.of(false))
            .channel("DISTR")
            .globalCompanyId("1234")
            .build();
  }

  private HotelAvailabilityByIdsV2Request createRequestByIdsV2() {
    return HotelAvailabilityByIdsV2Request.builder()
            .hotelIds(List.of("TEST"))
            .arrivalDate(ARRIVAL_DATE_V2)
            .departureDate(DEPARTURE_DATE_V2)
            .bookingChannel(BookingChannel.builder().channel("DISTR").subchannel("WEB").language("en").build())
            .rates(RateV2.builder().ratePlanCodes(List.of("FLEXRATE", "STANDARD")).build())
            .rooms(List.of(Room.builder().numberOfRooms(1).children(0).adults(2).tag("DB").build()))
            .build();
  }

  @Test
  void getAvailabilitiesFromAvailabilityCache_shouldReturnOK() {
    //Arrange
    List<String> listOfHotelCodesByLocation = List.of("LONLEI", "LONEUS");
    List<String> substitutionsForDB = List.of("DBLWIN", "WINCMB", "NWDCMB", "WINSPL", "NWDSPL", "DOUBLE",
            "ZPLDBL", "FMTHRE", "FMTRPL", "FMTRSC", "FMFOUR", "DBLDBL", "FMQUAD", "FMBUNK", "DBLNWD", "EXTDBL",
            "EXDZPL", "PPLDBL", "PDBZPL", "PFAMIL", "LOWDBL", "WETDBL", "BRFDBL", "BRFZPL", "BIGWIN", "BIGNWD",
            "BIGZPL", "ACCWIN", "ACCNWD", "EXDLOW", "EXDWET", "PPDLOW", "PPDWET");
    List<String> substitutesForFAM = List.of("FMQUAD", "DBLDBL", "FMFOUR", "FMBUNK", "PFAMIL", "FMTHRE",
            "FMTRPL","FMTRSC");
    List<List<String>> roomSubstitutionRuleResponseList = List.of(substitutionsForDB,substitutesForFAM);

    when(availabilityCacheV1RequestMapper.toDto(any(HotelAvailabilitiesRequest.class)))
            .thenReturn(createAvailabilityCacheV1Request());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1(
            any(AvailabilityCacheRequestV1.class)))
            .thenReturn(createAvailabilityCacheResponse());
    when(properties.getEmployeeRatePlanClasses()).thenReturn(List.of("A", "B", "C", "S", "O"));
    when(hotelInfoInPort.getRoomTypesInfo(any())).thenReturn(createRoomTypesInfoV3());
    AvailabilityCacheV1ResponseMapper availabilityCacheV1ResponseMapper =
            new AvailabilityCacheV1ResponseMapperImpl();
    AvailabilityCacheV1SearchOutPortImpl outPort = new AvailabilityCacheV1SearchOutPortImpl(
            availabilityCacheV1RequestMapper, availabilityCacheV1ResponseMapper, availabilityCacheV1Client,
            hotelInfoInPort, properties,distProperties);

    //Act
    var availabilityResponse = outPort.getAvailabilitiesFromAvCache(
            createRequest(), listOfHotelCodesByLocation, roomSubstitutionRuleResponseList, false);

    //Assert
    assertThat(availabilityResponse, notNullValue());
    List<HotelAvailabilityResponse> hotelAvailabilityList = availabilityResponse.getHotelAvailabilityList();
    hotelAvailabilityList.forEach(hotelAvailabilityResponse -> {
      assertThat(hotelAvailabilityResponse.getLowestRoomRate(),
              is(new Cost(new BigDecimal(200.00), "USD")));
      assertThat(hotelAvailabilityResponse.getAvailable(), is(true));
      assertThat(hotelAvailabilityResponse.getNumberOfRoomsAvailable(), is(4));
    });
  }

  @Test
  void getAvailabilitiesFromAvailabilityCacheWithEmployeeRate_shouldReturnOK() {
    //Arrange
    List<String> listOfHotelCodesByLocation = List.of("LONLEI", "LONEUS");
    List<String> substitutionsForDB = List.of("DBLWIN", "WINCMB", "NWDCMB", "WINSPL", "NWDSPL", "DOUBLE",
            "ZPLDBL", "FMTHRE", "FMTRPL", "FMTRSC", "FMFOUR", "DBLDBL", "FMQUAD", "FMBUNK", "DBLNWD", "EXTDBL",
            "EXDZPL", "PPLDBL", "PDBZPL", "PFAMIL", "LOWDBL", "WETDBL", "BRFDBL", "BRFZPL", "BIGWIN", "BIGNWD",
            "BIGZPL", "ACCWIN", "ACCNWD", "EXDLOW", "EXDWET", "PPDLOW", "PPDWET");
    List<String> substitutesForFAM = List.of("FMQUAD", "DBLDBL", "FMFOUR", "FMBUNK", "PFAMIL", "FMTHRE",
            "FMTRPL","FMTRSC");
    List<List<String>> roomSubstitutionRuleResponseList = List.of(substitutionsForDB,substitutesForFAM);

    when(availabilityCacheV1RequestMapper.toDto(any(HotelAvailabilitiesRequest.class)))
            .thenReturn(createAvailabilityCacheV1Request());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1(any(AvailabilityCacheRequestV1.class)))
            .thenReturn(createAvailabilityCacheResponse());
    when(availabilityCacheV1ResponseMapper.toModel(any(HotelAvailabilitiesDto.class)))
            .thenReturn(mapAvailabilityCacheResponse());
    when(properties.getEmployeeRatePlanClasses()).thenReturn(List.of("A","B","C","S","O"));

    //Act
    var availabilityResponse = outPort.getAvailabilitiesFromAvCache(
            createRequest(), listOfHotelCodesByLocation, roomSubstitutionRuleResponseList, false);

    //Assert
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailabilityList().get(0).getLowestRoomRate(), is(new Cost(new BigDecimal(60.00),"USD")));
    assertThat(availabilityResponse.getHotelAvailabilityList().get(0).getAvailable(), is(true));
    assertThat(availabilityResponse.getHotelAvailabilityList().get(0).getCellCode(), is("EMP01"));
  }


  @Test
  void getAvailabilitiesFromAvailabilityCacheDistrChannel_shouldReturnOk() {
    //Arrange
    List<String> listOfHotelCodesByLocation = List.of("LONLEI", "LONEUS");
    List<String> substitutionsForDB = List.of("DBLWIN", "WINCMB", "NWDCMB", "WINSPL", "NWDSPL", "DOUBLE",
            "ZPLDBL", "FMTHRE", "FMTRPL", "FMTRSC", "FMFOUR", "DBLDBL", "FMQUAD", "FMBUNK", "DBLNWD", "EXTDBL",
            "EXDZPL", "PPLDBL", "PDBZPL", "PFAMIL", "LOWDBL", "WETDBL", "BRFDBL", "BRFZPL", "BIGWIN", "BIGNWD",
            "BIGZPL", "ACCWIN", "ACCNWD", "EXDLOW", "EXDWET", "PPDLOW", "PPDWET");
    List<String> substitutesForFAM = List.of("FMQUAD", "DBLDBL", "FMFOUR", "FMBUNK", "PFAMIL", "FMTHRE",
            "FMTRPL","FMTRSC");
    List<List<String>> roomSubstitutionRuleResponseList = List.of(substitutionsForDB,substitutesForFAM);

    when(availabilityCacheV1RequestMapper.toDto(any(HotelAvailabilitiesRequest.class)))
            .thenReturn(createAvailabilityCacheV1RequestDistr());
    when(availabilityCacheV1Client.getAvailabilitiesResponseFromCacheV1Distr(any(AvailabilityCacheRequestV1.class), any()))
            .thenReturn(createAvailabilityCacheResponseDistr());
    when(availabilityCacheV1ResponseMapper.toHotelAvailabilitiesDto(any(HotelAvailabilitiesDistrDto.class)))
            .thenReturn(createAvailabilityCacheResponse());
    when(availabilityCacheV1ResponseMapper.toModel(any(HotelAvailabilitiesDto.class)))
            .thenReturn(mapAvailabilityCacheResponse());
    when(properties.getEmployeeRatePlanClasses()).thenReturn(List.of("A","B","C","S","O"));

    //Act
    var availabilityResponse = outPort.getAvailabilitiesFromAvCache(
            createDistrRequest(), listOfHotelCodesByLocation, roomSubstitutionRuleResponseList, false);

    //Assert
    verify(availabilityCacheV1Client, times(1)).getAvailabilitiesResponseFromCacheV1Distr(any(AvailabilityCacheRequestV1.class), any());
    assertThat(availabilityResponse, notNullValue());
    assertThat(availabilityResponse.getHotelAvailabilityList().get(0).getLowestRoomRate(), is(new Cost(new BigDecimal(60.00),"USD")));
    assertThat(availabilityResponse.getHotelAvailabilityList().get(0).getAvailable(), is(true));
    assertThat(availabilityResponse.getHotelAvailabilityList().get(0).getCellCode(), is("EMP01"));
  }

  private RoomSubstitutionRuleResponse buildRoomSubstitutionRuleResponse() {
    List<RoomSubstitution> roomSubstitutionList = List.of(
            RoomSubstitution.builder().type("DBLWIN").silent(false).build());
    return RoomSubstitutionRuleResponse.builder()
            .requestDetails(RoomSubstitutionRequestDetails.builder()
                    .adults(1)
                    .children(0)
                    .pms("OP")
                    .roomType("DB")
                    .build())
            .substitutionList(roomSubstitutionList)
            .build();
  }

  private HotelAvailabilitiesDto createAvailabilityCacheResponse() {
    return new HotelAvailabilitiesDto(2, createOperaHotels());
  }

  private List<HotelOperaDto> createOperaHotels() {
    List<HotelOperaDto> hotelDtoList = new LinkedList<>();
    hotelDtoList.add(new HotelOperaDto("LONLEI", "Test Hotel Name", "pi",
            true, false, false, false, "OPERA", createRatesList(), "EMP01", false, 1));
    hotelDtoList.add(new HotelOperaDto("LONEUS", "Test Hotel Name", "pi",
            true, false, false, false, "OPERA", createRatesList(), "EMP01", true, 1));
    return hotelDtoList;
  }

  private List<RatePlanOperaDto> createRatesList() {
    List<RatePlanOperaDto> ratePlanDtoList = new LinkedList<>();
    List<List<RoomOperaDto>> rooms1 = List.of(createRoomsDB(new LinkedList()),createRoomsFAM(new LinkedList<>()));
    List<List<RoomOperaDto>> rooms2 = List.of(createRoomsDB(new LinkedList()),createRoomsFAM(new LinkedList<>()));
    List<List<RoomOperaDto>> rooms3 = List.of(createRoomsDB(new LinkedList()),createRoomsFAM(new LinkedList<>()));
    ratePlanDtoList.add(new RatePlanOperaDto("A",
            "A",
            "FLEXRATE", "1", "aaa", rooms1));
    ratePlanDtoList.add(new RatePlanOperaDto("C",
            "Bla bla semiflex",
            "SEMIFLEX", "1", "bbb", rooms2));
    ratePlanDtoList.add(new RatePlanOperaDto("S",
            "Bla bla standard",
            "Standard", "1", "ccc", rooms3));
    ratePlanDtoList.add(new RatePlanOperaDto("EMPLOYEE",
            "B",
            "Employee Rate", "1", "ccc", rooms3));
    return ratePlanDtoList;
  }

  private List<RoomOperaDto> createRoomsDB(LinkedList roomDtoList) {
    roomDtoList.add(new RoomOperaDto("DOUBLE", false, 3, 2,
            new TotalCostDto(new BigDecimal(40.00), "USD")));
    roomDtoList.add(new RoomOperaDto("FMTRPL", false, 3, 1,
            new TotalCostDto(new BigDecimal(70.00), "USD")));
    roomDtoList.add(new RoomOperaDto("WETDBL", false, 3, 1,
            new TotalCostDto(new BigDecimal(50.00), "USD")));
    return roomDtoList;
  }

  private List<RoomOperaDto> createRoomsFAM(LinkedList roomDtoList) {
    roomDtoList.add(new RoomOperaDto("FMTRPL", false, 1, 2,
            new TotalCostDto(new BigDecimal(70.00), "USD")));
    return roomDtoList;
  }

  private HotelAvailabilitiesResponse mapAvailabilityCacheResponse() {
    return HotelAvailabilitiesResponse.builder()
            .total(2)
            .page(1)
            .pageSize(2)
            .hotelAvailabilityList(createListOfAvailabilities())
            .build();
  }

  private List<HotelAvailabilityResponse> createListOfAvailabilities() {
    List<HotelAvailabilityResponse> hotelResponses = new LinkedList<>();
    hotelResponses.add(HotelAvailabilityResponse.builder()
            .hotelId("LONLEI")
            .name("Test Hotel Name")
            .distance(100.0)
            .unit("MILE")
            .available(true)
            .limitedAvailability(false)
            .lowestRoomRate(new Cost(new BigDecimal(60.00), "USD"))
            .pmsSource("OPERA")
            .cellCode("EMP01")
            .build());
    return hotelResponses;
  }

  private AvailabilityCacheRequestV1 createAvailabilityCacheV1Request() {
    List<String> listOfHotelCodesByLocation = List.of("LONLEI", "LONEUS");
    List<String> substitutionsForDB = List.of("DBLWIN","WINCMB","NWDCMB","WINSPL","NWDSPL","DOUBLE",
            "ZPLDBL","FMTHRE","FMTRPL","FMTRSC","FMFOUR","DBLDBL","FMQUAD","FMBUNK","DBLNWD","EXTDBL",
            "EXDZPL","PPLDBL","PDBZPL","PFAMIL","LOWDBL","WETDBL","BRFDBL","BRFZPL","BIGWIN","BIGNWD",
            "BIGZPL","ACCWIN","ACCNWD","EXDLOW","EXDWET","PPDLOW","PPDWET");
    List<String> substitutesForFAM = List.of("FMQUAD","DBLDBL","FMFOUR","FMBUNK","PFAMIL","FMTHRE",
            "FMTRPL","FMTRSC");
    List<List<String>> roomSubstitutionRuleResponseList = List.of(substitutionsForDB,substitutesForFAM);
    return AvailabilityCacheRequestV1.builder()
            .hotelCodes(Arrays.asList("LONLEI", "LONEUS"))
            .arrival(LocalDate.now().toString())
            .departure(LocalDate.now().plusDays(4).toString())
            .language("en")
            .country("gb")
            .rooms(2)
            .roomQty(List.of(2,1))
            .adults(List.of(1,2))
            .children(List.of(0,1))
            .roomTypesList(roomSubstitutionRuleResponseList)
            .build();
  }

  private AvailabilityCacheRequestV1 createAvailabilityCacheV1RequestDistr() {
    AvailabilityCacheRequestV1 request = createAvailabilityCacheV1Request();
    request.setChannel(BookingChannel.DISTR_BOOKING_CHANNEL);
    return request;
  }

  private HotelAvailabilitiesRequest createRequest(){
    return HotelAvailabilitiesRequest.builder()
            .arrivalDate(LocalDate.now().toString())
            .departureDate(LocalDate.now().plusDays(4).toString())
            .language("en")
            .country("gb")
            .roomTypes(List.of("DB","FAM"))
            .adultsNumber(List.of(1,2))
            .childrenNumber(List.of(0,1))
            .location("2.5")
            .locationFormat(LocationFormatEnum.PLACEID)
            .radiusUnit(RadiusUnitEnum.MILES)
            .radius(40)
            .page(1)
            .pageSize(40)
            .oldWorldChannel(OldWorldChannelEnum.WEB)
            .channel("PI")
            .subChannel("PI")
            .ratePlanCodes(Collections.singletonList("EMP01"))
            .build();
  }

  private HotelAvailabilitiesRequest createDistrRequest() {
    return HotelAvailabilitiesRequest.builder()
            .arrivalDate(LocalDate.now().toString())
            .departureDate(LocalDate.now().plusDays(4).toString())
            .language("en")
            .country("gb")
            .roomTypes(List.of("DB","FAM"))
            .adultsNumber(List.of(1,2))
            .childrenNumber(List.of(0,1))
            .location("2.5")
            .locationFormat(LocationFormatEnum.PLACEID)
            .radiusUnit(RadiusUnitEnum.MILES)
            .radius(40)
            .page(1)
            .pageSize(40)
            .oldWorldChannel(OldWorldChannelEnum.WEB)
            .channel("DISTR")
            .subChannel("AMADEUS")
            .ratePlanCodes(Collections.singletonList("EMP01"))
            .build();
  }

  private AvailabilityCacheRequestV1 createAvailabilityCacheV1RequestForV2() {
    List<String> substitutionsForDB = List.of("DBLWIN", "WINCMB", "NWDCMB", "WINSPL", "NWDSPL", "DOUBLE",
            "ZPLDBL", "FMTHRE", "FMTRPL", "FMTRSC", "FMFOUR", "DBLDBL", "FMQUAD", "FMBUNK", "DBLNWD", "EXTDBL",
            "EXDZPL", "PPLDBL", "PDBZPL", "PFAMIL", "LOWDBL", "WETDBL", "BRFDBL", "BRFZPL", "BIGWIN", "BIGNWD",
            "BIGZPL", "ACCWIN", "ACCNWD", "EXDLOW", "EXDWET", "PPDLOW", "PPDWET");
    List<String> substitutesForFAM = List.of("FMQUAD", "DBLDBL", "FMFOUR", "FMBUNK", "PFAMIL", "FMTHRE",
            "FMTRPL", "FMTRSC");
    List<List<String>> roomSubstitutionRuleResponseList = List.of(substitutionsForDB, substitutesForFAM);
    return AvailabilityCacheRequestV1.builder()
            .hotelCodes(Arrays.asList("TESTHOTEL"))
            .arrival(LocalDate.now().toString())
            .departure(LocalDate.now().plusDays(4).toString())
            .language("en")
            .country("gb")
            .rooms(2)
            .roomQty(List.of(2, 1))
            .adults(List.of(1, 2))
            .children(List.of(0, 1))
            .roomTypesList(roomSubstitutionRuleResponseList)
            .build();
  }
}
