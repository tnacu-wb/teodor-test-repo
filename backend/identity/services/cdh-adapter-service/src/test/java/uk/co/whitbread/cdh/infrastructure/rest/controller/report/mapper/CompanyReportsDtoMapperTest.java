package uk.co.whitbread.cdh.infrastructure.rest.controller.report.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.cdh.domain.model.report.out.CompanyReports;
import uk.co.whitbread.cdh.domain.model.report.out.Reports;

@Slf4j
class CompanyReportsDtoMapperTest {

  private CompanyReportsDtoMapper mapper;

  @BeforeEach
  void init() {
    mapper = Mappers.getMapper(CompanyReportsDtoMapper.class);
  }

  @Test
  void shouldMapCompanyReportsModelToDto() {

    var modelData = CompanyReports.builder()
        .results(Collections.singletonList(
            Reports.builder().bookingReference("1bnmosg").bookingStatus("Done").build()))
        .build();

    var result = mapper.toDto(modelData);

    assertNotNull(result);
    assertEquals(modelData.getResults().getFirst().getBookingReference(),
        result.getResults().getFirst().getBookingReference());
    assertEquals(modelData.getResults().getFirst().getBookingStatus(),
        result.getResults().getFirst().getBookingStatus());
  }

}
