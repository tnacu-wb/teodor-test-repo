package uk.co.whitbread.hotel.payment.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidateBinRequest {

    @NotNull
    @Size(min = 6, max = 6)
    private String bin;
    @Size(min = 6, max = 6)
    private String hotelCode;

}
