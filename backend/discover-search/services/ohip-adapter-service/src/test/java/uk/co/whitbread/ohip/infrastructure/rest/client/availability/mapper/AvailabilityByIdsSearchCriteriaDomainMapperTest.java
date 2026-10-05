package uk.co.whitbread.ohip.infrastructure.rest.client.availability.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilityRoomSearchCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.AvailabilitySearchRequest;
import uk.co.whitbread.ohip.domain.model.availability.in.CorporateRate;
import uk.co.whitbread.ohip.domain.model.availability.in.RateV2;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.mapper.AvailabilityByIdsSearchCriteriaDomainMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.in.CorporateRateDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AvailabilityByIdsSearchCriteriaDomainMapper.class)
class AvailabilityByIdsSearchCriteriaDomainMapperTest {

  private AvailabilityByIdsSearchCriteriaDomainMapper availabilityRequestMapper =
      Mappers.getMapper(AvailabilityByIdsSearchCriteriaDomainMapper.class);

  @Test
  void toCorporateRateDtoShouldMapCorrectly() {
    CorporateRateDto dto = CorporateRateDto.builder()
        .corporateId("15017452")
        .ratePlanSets(Collections.singletonList("FLEXRATE"))
        .build();

    List<CorporateRate> result = availabilityRequestMapper.toCorporateRateDto(dto);

    assertEquals(1, result.size());
    assertEquals("15017452", result.get(0).getCorporateId());
    assertEquals(Collections.singletonList("FLEXRATE"), result.get(0).getRatePlanSets());
  }

  @Test
  void toRateV2DtoShouldReturnNullForEmptyList() {
    RateV2 result = availabilityRequestMapper.toRateV2Dto(Collections.emptyList());
    assertNull(result);
  }

  @Test
  void toRateV2DtoShouldMapListCorrectly() {
    CorporateRateDto dto = CorporateRateDto.builder()
        .corporateId("15017452")
        .ratePlanSets(Collections.singletonList("FLEXRATE"))
        .build();

    RateV2 result = availabilityRequestMapper.toRateV2Dto(List.of(dto));

    assertNotNull(result);
    assertEquals(1, result.getCorporateRates().size());
    assertEquals("15017452", result.getCorporateRates().get(0).getCorporateId());
  }

}