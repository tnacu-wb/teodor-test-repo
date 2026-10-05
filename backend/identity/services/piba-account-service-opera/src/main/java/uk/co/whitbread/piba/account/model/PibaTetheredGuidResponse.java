package uk.co.whitbread.piba.account.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.piba.account.model.enums.Scheme;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PibaTetheredGuidResponse {
    
    private String companyId;
    private String employeeId;
    private List<String> tetheredGuid;
    private Scheme scheme;
    
}