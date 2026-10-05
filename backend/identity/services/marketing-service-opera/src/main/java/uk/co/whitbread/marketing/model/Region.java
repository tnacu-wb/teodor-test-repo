package uk.co.whitbread.marketing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Region {

    private String id;
    private String description;
    private boolean active;

}
