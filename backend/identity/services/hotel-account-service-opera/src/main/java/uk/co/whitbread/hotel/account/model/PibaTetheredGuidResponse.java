package uk.co.whitbread.hotel.account.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PibaTetheredGuidResponse {

    private int companyId;

    private int employeeId;

    @NotNull
    private List<String> tetheredGuid;

}
