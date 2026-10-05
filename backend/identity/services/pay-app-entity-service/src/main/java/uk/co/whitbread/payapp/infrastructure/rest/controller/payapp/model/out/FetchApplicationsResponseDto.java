package uk.co.whitbread.payapp.infrastructure.rest.controller.payapp.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchApplicationsResponseDto {

  private List<ApplicationDetailsDto> applications;

}
