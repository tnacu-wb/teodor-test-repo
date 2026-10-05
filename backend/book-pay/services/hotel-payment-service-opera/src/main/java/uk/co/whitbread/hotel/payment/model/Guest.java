package uk.co.whitbread.hotel.payment.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = {"title", "firstName", "lastName"})
public class Guest {


    @NotEmpty
    @Schema(required = true, example = "Mr")
    private String title;
    @NotEmpty
    @Schema(required = true, example = "Mark")
    private String firstName;
    @NotEmpty
    @Schema(required = true, example = "Hicks")
    private String lastName;
    @Schema(example = "STRING")
    private String guestHistoryNumber;
    @NotNull
    @Schema(required = true, example = "1")
    @Min(1)
    private Integer roomNumber;
    private Long guestId;


}
