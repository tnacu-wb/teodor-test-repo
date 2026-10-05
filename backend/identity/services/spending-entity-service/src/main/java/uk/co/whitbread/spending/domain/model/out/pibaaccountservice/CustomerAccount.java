package uk.co.whitbread.spending.domain.model.out.pibaaccountservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.spending.domain.model.in.Scheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAccount {

  protected int schemeCustomerId;
  protected String tetheredGuid;
  protected String accountName;
  protected String accountNumber;
  protected String apiUserGuid;
  protected List<RegistrationRoles> registrationRoles;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  protected String errorCode;
  protected Scheme scheme;
}