package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.reservation.domain.model.out.CharacterUDFs;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.UserDefinedFields;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ReservationByBasketRefResponseMapperImpl.class,
    ReservationGuestsMapperImpl.class, BillingMapperImpl.class, AddressMapperImpl.class})
class ReservationByBasketRefResponseMapperTest {

  @Autowired
  ReservationByBasketRefResponseMapper mapper;

  @ParameterizedTest()
  @MethodSource("mapReservationByBasketRefResponse")
  void toModel_shouldMapWifiCodeOk(ReservationByBasketRefResponse source, String wifiCodeValue) {

    //Act
    var target = mapper.toDto(source);

    //Assert
    assertEquals(wifiCodeValue, target.getReservationByIdList().get(0).getWifiCode());
  }

  @ParameterizedTest()
  @MethodSource("mapIdContext")
  void toModel_shouldMapIdContextOk(ReservationByBasketRefResponse source, String idContext) {

    //Act
    var target = mapper.toDto(source);

    //Assert
    assertEquals(idContext, target.getIdContext());
  }

  private static Stream<Arguments> mapIdContext() {
    return Stream.of(
        Arguments.of(ReservationByBasketRefResponse.builder()
                .reservationByIdList(List.of(ReservationByIdResponse.builder()
                    .reservationPackageList(List.of(
                        ReservationPackagesDetailsResponse.builder().packageCode("").build()))
                    .build()))
                .idContext("CTX123").build(),
            "CTX123"),
        Arguments.of(ReservationByBasketRefResponse.builder()
                .reservationByIdList(List.of(ReservationByIdResponse.builder()
                    .reservationPackageList(List.of(
                        ReservationPackagesDetailsResponse.builder().packageCode("").build()))
                    .build()))
                .build(),
            "WB_DIGITAL"));
  }

  private static Stream<Arguments> mapReservationByBasketRefResponse() {
    CharacterUDFs udf07 = CharacterUDFs.builder().name("UDFC07").value("12345").build();
    CharacterUDFs udf09 = CharacterUDFs.builder().name("UDFC09").value("aaa").build();

    return Stream.of(
        Arguments.of(buildReservationByBasketRefResponse(List.of(udf07, udf09)), "12345"),
        Arguments.of(buildReservationByBasketRefResponse(List.of(udf09)), null),
        Arguments.of(buildReservationByBasketRefResponse(Collections.emptyList()), null),
        Arguments.of(buildReservationByBasketRefResponse(null), null));
  }

  private static ReservationByBasketRefResponse buildReservationByBasketRefResponse(
      List<CharacterUDFs> chUdf) {
    var udf = ReservationByIdResponse.builder()
        .reservationPackageList(
            List.of(ReservationPackagesDetailsResponse.builder().packageCode("").build()))
        .userDefinedFields(UserDefinedFields.builder().characterUDFs(chUdf).build()).build();
    return ReservationByBasketRefResponse.builder()
        .reservationByIdList(List.of(udf))
        .build();
  }

}
