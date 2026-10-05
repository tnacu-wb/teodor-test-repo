package uk.co.whitbread.contentservice.roomtypes.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class RateClassification implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private String rateClassification;
    private String rateOrder;
    private String rateName;
    private String rateDescription;
    private String rateLongDescription;
    private String rateNotes;
}
