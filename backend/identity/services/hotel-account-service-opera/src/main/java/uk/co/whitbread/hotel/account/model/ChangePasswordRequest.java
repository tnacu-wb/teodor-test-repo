package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotEmpty
    private String newPassword;

}
