package uk.co.whitbread.ohip.domain.model.packages.out;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCodes {

  private String packageCode;
  private String packageDescription;
}
