package uk.co.whitbread.shared.auth.account;

import java.io.Serial;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Account implements Serializable {

  @Serial
  private static final long serialVersionUID = 4633002920497708948L;
  private String accessLevel;
  private String customerId;
  private String employeeId;
  private String companyId;
  private String operaCompanyId;
  private String bartId;
  private String bartEmployeeId;
  private String email;
}
