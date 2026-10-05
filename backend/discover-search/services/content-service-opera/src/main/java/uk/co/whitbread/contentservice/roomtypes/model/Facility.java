package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Facility implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private String facilityTitle;
    private String facilityDescription;
}
