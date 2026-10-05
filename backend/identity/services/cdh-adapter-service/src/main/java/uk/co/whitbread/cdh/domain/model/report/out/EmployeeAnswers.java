package uk.co.whitbread.cdh.domain.model.report.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAnswers {

  @JsonProperty("PurchaseOrderAnswer")
  private String purchaseOrder;

  @JsonProperty("CustomerReferenceAnswer")
  private String customerReference;

  @JsonProperty("CompanyAnswers")
  private List<CompanyAnswers> companyAnswer;
}
