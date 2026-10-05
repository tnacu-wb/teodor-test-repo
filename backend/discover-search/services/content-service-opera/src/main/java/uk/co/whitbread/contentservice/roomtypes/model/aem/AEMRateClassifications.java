package uk.co.whitbread.contentservice.roomtypes.model.aem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class AEMRateClassifications implements Serializable {
    private static final long serialVersionUID = 3973228892139419579L;
    private RateClassification rateClassification;
    private RateOrder rateOrder;
    private RateName rateName;
    private RateDescription rateDescription;
    private RateLongDescription rateLongDescription;
    private RateNotes rateNotes;
}
