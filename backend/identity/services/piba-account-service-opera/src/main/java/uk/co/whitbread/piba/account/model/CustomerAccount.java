package uk.co.whitbread.piba.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.piba.account.model.enums.RegistrationRoles;
import uk.co.whitbread.piba.account.model.enums.Scheme;

import java.util.List;

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

    public CustomerAccount(String errorCode) {
        this.errorCode = errorCode;
    }
}
