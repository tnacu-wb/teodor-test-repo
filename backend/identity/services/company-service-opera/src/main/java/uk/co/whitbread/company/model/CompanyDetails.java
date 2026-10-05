package uk.co.whitbread.company.model;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetails {
    private String companyName;
    private String alternateCompanyName;
    private int numberOfEmployees;
    private String companySector;
    private String averageMonthlyBooking;
    private String numberOfEmployee;


    @Valid
    private Address companyAddress;
    @Valid
    private Employee mainEmployee;
}
