package uk.co.whitbread.hotel.info.model.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
public class Section implements Serializable {

    private String name;
    private String labelKey;

    private List<Field> fields;
}
