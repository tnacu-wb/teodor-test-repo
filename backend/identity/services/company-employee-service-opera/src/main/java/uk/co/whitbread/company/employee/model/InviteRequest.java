package uk.co.whitbread.company.employee.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InviteRequest {
    @NotNull
    private String emailAddress;
    private String centralCardId;
    private Boolean innBusiness;
}
