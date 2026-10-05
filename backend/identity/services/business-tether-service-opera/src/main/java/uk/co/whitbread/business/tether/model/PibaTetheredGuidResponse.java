package uk.co.whitbread.business.tether.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PibaTetheredGuidResponse {
    
    private int companyId;
    
    private int employeeId;
    
    private List<String> tetheredGuid;

    private Scheme scheme;
}