package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class ForgottenPasswordResponse {

    @NotNull
    private Boolean success;
}
