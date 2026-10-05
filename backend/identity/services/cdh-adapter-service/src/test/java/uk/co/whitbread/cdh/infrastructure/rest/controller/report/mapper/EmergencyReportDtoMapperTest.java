package uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper;

import static org.junit.Assert.assertEquals;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.in.EmergencyReportRequestDto;

@Slf4j
class EmergencyReportDtoMapperTest {

  private EmergencyReportDtoMapper mapper;

  @BeforeEach
  void init() {
    mapper = Mappers.getMapper(EmergencyReportDtoMapper.class);
  }

  @Test
  void shouldMapManagementInformationDtoToModel() {
    var dtoData = EmergencyReportRequestDto.builder()
        .accessContext("ccui")
        .accessedBy("test")
        .fromDate("2023-01-01")
        .toDate("2023-01-15")
        .build();

    var result = mapper.toModel(dtoData);

    assertEquals(dtoData.getFromDate(), result.getFromDate());
    assertEquals(dtoData.getToDate(), result.getToDate());
  }

}
