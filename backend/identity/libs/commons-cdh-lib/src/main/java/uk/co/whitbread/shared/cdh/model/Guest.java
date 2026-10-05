package uk.co.whitbread.shared.cdh.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Guest extends Booker {

  @JsonProperty("EmployeeAccountId")
  private String employeeAccountId;

  @JsonProperty("GlobalCompanyId")
  private Integer globalCompanyId;

  @JsonProperty("BartEmployeeId")
  private Integer bartEmployeeId;

  @JsonProperty("BartGuestHistoryNumber")
  private String bartGuestHistoryNumber;

  @JsonProperty("LeadGuest")
  private boolean leadGuest;

  @JsonProperty("Initials")
  private String initials;

  @JsonProperty("Nationality")
  private String nationality;

  @JsonProperty("Passport")
  private BusinessPassport passport;
}
