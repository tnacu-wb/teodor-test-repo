package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class Breakfast {

    @NotEmpty
    @Schema(required = true, example = "17")
    private String code;
    @NotNull
    @Schema(required = true, example = "1")
    private Integer roomNumber;
    @NotNull
    @Schema(required = true, example = "1")
    private Integer adults;
    @NotNull
    @Schema(required = true, example = "0")
    private Integer children;
}
