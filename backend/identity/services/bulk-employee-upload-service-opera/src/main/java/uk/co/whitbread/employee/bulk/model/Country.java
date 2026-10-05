package uk.co.whitbread.employee.bulk.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    private String countryCode;
    private String countryCodeISO;
    private String countryLegend;
    private boolean passportRequired;
    private String dialingCode;
    private String flagImg;
}
