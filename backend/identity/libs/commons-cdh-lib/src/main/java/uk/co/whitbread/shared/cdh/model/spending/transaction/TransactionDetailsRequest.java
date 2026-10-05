package uk.co.whitbread.shared.cdh.model.spending.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDetailsRequest {

  private static final String DATE_FORMAT = "dd-MM-yyyy";

  @JsonProperty("CompanyAccountId")
  private String companyAccountId;

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("PIBAAccountNo")
  private String pibaAccountNo;

  @JsonProperty("fromdate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate fromDate;

  @JsonProperty("todate")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
  private LocalDate toDate;

  @JsonProperty("PageSize")
  private Integer pageSize;

  @JsonProperty("PageNumber")
  private Integer pageNumber;

}
