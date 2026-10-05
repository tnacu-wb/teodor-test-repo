package uk.co.whitbread.cdh.infrastructure.rest.controller.report.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyReportResponseDto {

  private List<EmergencyResultsDto> results;


}
