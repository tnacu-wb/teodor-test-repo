package uk.co.whitbread.hotel.info.model.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Option implements Serializable{

    private String value;
    private String labelKey;
}
