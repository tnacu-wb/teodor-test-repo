package uk.co.whitbread.marketing.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelAccountClientError {

    private int status;
    private String code;
    private String[] details;
}
