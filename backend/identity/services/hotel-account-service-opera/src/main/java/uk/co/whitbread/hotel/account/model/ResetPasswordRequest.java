package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResetPasswordRequest extends ChangePasswordRequest {

    @NotEmpty
    private String customerId;

}
