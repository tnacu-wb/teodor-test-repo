package uk.co.whitbread.hotel.account.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.hotel.account.validation.ValidRoomType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ValidRoomType
public class RoomCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private String type;

    private String lettingType;

    @Min(value = 1, message = "Adults must be at least 1")
    @Max(value = 2, message = "Adults must be at most 2")
    @NotNull
    private Long adults;

    @Min(value = 0, message = "Children must be at least 0")
    @Max(value = 2, message = "Children must be at most 2")
    @NotNull
    private Long children;

    @NotNull
    private Boolean cotRequired;

    private HotelBrandCode hotelBrand;
}
