package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.exceptions.RateCodePricingException;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.RateCodePricingRequestDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = RateCodeCriteriaDomainMapperImpl.class)
@Slf4j
class RateCodeCriteriaDomainMapperTest {

  @Autowired
  RateCodeCriteriaDomainMapper rateCodeCriteriaDomainMapper;

  @Test
  void toDomain__ShouldReturnOK() {

    RateCodePricingRequestDto requestDto = createRateCodePricingRqDto(List.of("DOUBLE", "FMQUAD"),
        List.of(1, 1), List.of(1, 1));
    RateCodeCriteria result = rateCodeCriteriaDomainMapper.toDomainModel("TestHotelId",
        requestDto);

    assertEquals("TestHotelId", result.getHotelId());
    assertEquals(requestDto.getRatePlanCode(), result.getRatePlanCode());
    assertEquals(requestDto.getRoomTypes().size(), result.getRoomInfoCriteriaList().size());
  }

  @Test
  void toDomain__ShouldReplaceChildNoWithZeroAndReturnOK() {

    RateCodePricingRequestDto requestDto = createRateCodePricingRqDto(List.of("DOUBLE", "FMQUAD"),
        List.of(1, 1), List.of(1));
    RateCodeCriteria result = rateCodeCriteriaDomainMapper.toDomainModel("TestHotelId",
        requestDto);

    assertEquals("TestHotelId", result.getHotelId());
    assertEquals(requestDto.getRatePlanCode(), result.getRatePlanCode());
    assertEquals(requestDto.getRoomTypes().size(), result.getRoomInfoCriteriaList().size());
  }

  @Test
  void toDomain__SizeMismatchShouldThrowError() {

    RateCodePricingRequestDto requestDto = createRateCodePricingRqDto(List.of("DOUBLE", "FMQUAD"),
        List.of(1), List.of(1));

    assertThrows(RateCodePricingException.class,
        () -> rateCodeCriteriaDomainMapper.toDomainModel("TestHotelId",
            requestDto));
  }

  private RateCodePricingRequestDto createRateCodePricingRqDto(List<String> roomTypes,
      List<Integer> adultsNo, List<Integer> childrenNo) {
    return RateCodePricingRequestDto.builder()
        .arrivalDate("2022-12-20")
        .departureDate("2022-12-22")
        .ratePlanCode("FLEX")
        .roomTypes(roomTypes)
        .adultsNo(adultsNo)
        .childrenNo(childrenNo)
        .build();
  }
}
