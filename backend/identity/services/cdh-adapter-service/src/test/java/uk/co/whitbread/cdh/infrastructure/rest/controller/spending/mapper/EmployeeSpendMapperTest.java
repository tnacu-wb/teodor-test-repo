package uk.co.whitbread.cdh.infrastructure.rest.controller.spending.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendReport;
import uk.co.whitbread.cdh.domain.model.spending.EmployeeSpendRequest;
import uk.co.whitbread.cdh.infrastructure.rest.controller.spending.model.in.EmployeeSpendRequestDto;

class EmployeeSpendMapperTest {

  private EmployeeSpendMapper mapper;

  @BeforeEach
  void init() {
    mapper = Mappers.getMapper(EmployeeSpendMapper.class);
  }

  @Test
  void shouldMapRequestDtoToModel() {
    // Given
    EmployeeSpendRequestDto dto = EmployeeSpendRequestDto.builder()
        .fromMonthYear("01-2024")
        .toMonthYear("03-2026")
        .accessContext("test-context")
        .accessedBy("test-user")
        .build();

    // When
    EmployeeSpendRequest result = mapper.toModel("COMP123", "EMP456", dto);

    // Then
    assertNotNull(result);
    assertEquals("COMP123", result.getCompanyAccountId());
    assertEquals("EMP456", result.getEmployeeAccountId());
    assertEquals("01-2024", result.getFromMonthYear());
    assertEquals("03-2026", result.getToMonthYear());
    assertEquals("test-context", result.getAccessContext());
    assertEquals("test-user", result.getAccessedBy());
  }

  @Test
  void shouldMapReportListToDto() {
    // Given
    List<EmployeeSpendReport> reports = Arrays.asList(
        EmployeeSpendReport.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2024)
            .month(9)
            .noOfBookings(2)
            .bookingValue(96.00)
            .bookingCurrency("EUR")
            .build(),
        EmployeeSpendReport.builder()
            .companyAccountId("COMP123")
            .employeeAccountId("EMP456")
            .year(2026)
            .month(2)
            .noOfBookings(1)
            .bookingValue(71.00)
            .bookingCurrency("GBP")
            .build()
    );

    // When
    var result = mapper.toDto(reports);

    // Then
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("COMP123", result.getFirst().getCompanyAccountId());
    assertEquals("EMP456", result.getFirst().getEmployeeAccountId());
    assertEquals(2024, result.getFirst().getYear());
    assertEquals(9, result.getFirst().getMonth());
    assertEquals(96.00, result.getFirst().getBookingValue());
    assertEquals("EUR", result.getFirst().getBookingCurrency());
  }
}
