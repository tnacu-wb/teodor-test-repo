package uk.co.whitbread.ondemandrefreshservice.infrastructure.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OnDemandProcessResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private HttpStatus status;
  private String message;
}
