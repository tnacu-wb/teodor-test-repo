package uk.co.whitbread.piba.registration.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PibaTetheredGuidRequest {

    @NotNull
    String companyId;

    @NotNull
    String employeeId;
    
    @NotEmpty
    String tetheredGuid;

    @NotEmpty
    Scheme scheme;

}
