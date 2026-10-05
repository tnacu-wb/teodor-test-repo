package uk.co.whitbread.hotel.info.model.domain;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class MultiHotelInfoRequest extends HotelInfoRequest {

    @Size(max = 100)
    private List<String> hotelCodes;
}