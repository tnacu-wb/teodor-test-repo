package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpsellItem {

    @Schema(required = true, example = "2018-11-22")
    @NotNull
    private LocalDate postingDate;
    @NotNull
    @Schema(required = true, example = "1")
    private Integer quantity;
    @NotEmpty
    @Schema(required = true, example = "17")
    private String code;
    @NotEmpty
    @Schema(required = true, example = "1")
    private String roomNumber;
    @Schema(example = "Ultimate Wi-Fi - 24 hours")
    private String legend;
    @Schema(example = "")
    private String roomId;
    @Schema(example = "")
    private String category;
    @Valid
    private Price unitCost;
    @Valid
    private Price subtotal;

}
