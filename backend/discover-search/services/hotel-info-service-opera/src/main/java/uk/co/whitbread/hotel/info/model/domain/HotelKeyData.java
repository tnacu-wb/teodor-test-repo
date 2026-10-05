package uk.co.whitbread.hotel.info.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * Models the key data for a hotel, meaning its code, brand and name.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelKeyData implements Serializable {

    private String code;
    private BartHotelBrandCode brand;
    private String name;
}