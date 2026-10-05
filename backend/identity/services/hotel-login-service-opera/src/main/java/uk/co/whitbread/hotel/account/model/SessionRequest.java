package uk.co.whitbread.hotel.account.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class SessionRequest {

    @NotBlank
    @Schema(required = true)
    private String guestHistoryNumber;

}
