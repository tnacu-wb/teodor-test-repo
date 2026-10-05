package uk.co.whitbread.availabilitycacheservice.infrastructure.model.contentservice;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class RateClassificationsList implements Serializable {

  private List<RateClassification> rateClassifications;
}
