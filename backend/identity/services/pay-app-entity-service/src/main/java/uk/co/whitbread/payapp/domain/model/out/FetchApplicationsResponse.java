package uk.co.whitbread.payapp.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchApplicationsResponse {

  private List<ApplicationDetails> applications;

}
