package uk.co.whitbread.hotel.info.model.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


@Data
@NoArgsConstructor
public class Field implements Serializable{

    private String type;
    private String name;
    private String labelKey;

    private boolean required;

    private List<Option> options;
}
