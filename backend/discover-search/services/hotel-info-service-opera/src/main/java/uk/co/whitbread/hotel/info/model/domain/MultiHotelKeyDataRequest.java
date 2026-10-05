package uk.co.whitbread.hotel.info.model.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@ToString
public class MultiHotelKeyDataRequest extends HotelKeyDataRequest {

    @NotNull
    @Size(min = 1, max = 100)
    private List<String> hotelCodes;
}