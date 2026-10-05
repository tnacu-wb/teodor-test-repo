package uk.co.whitbread.business.tether.model;


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
    Integer companyId;

    @NotNull
    Integer employeeId;
    
    @NotEmpty
    String tetheredGuid;

    @NotNull
    Scheme scheme;
}
