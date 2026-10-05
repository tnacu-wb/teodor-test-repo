package uk.co.whitbread.hotel.register.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomCriteria {

    @NotNull
    private RoomType type;

    private String lettingType;

    @Min(0)
    @NotNull
    private Integer adults;

    @Min(0)
    @NotNull
    private Integer children;

    @NotNull
    private Boolean cotRequired;

    private String hotelBrand;
}
