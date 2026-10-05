package uk.co.whitbread.company.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.shared.commons.validation.CompanyName;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanySummary {
    @NotEmpty
    @CompanyName
    private String companyName;
    @NotEmpty
    @CompanyName
    private String alternateCompanyName;
    @NotNull
    private Address companyAddress;
    @NotNull
    private MainContact mainContact;

    private String companySector;
    private String averageMonthlyBooking;
    private String numberOfEmployee;
}
