package uk.co.whitbread.company.model;


import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company {

    @Valid
    private CompanyDetails companyDetails;
    private PaymentDetails paymentDetails;
    private BookingAllowances bookingAllowances;
    private BookingAlerts bookingAlerts;
    private CompanyManagementQuestions companyManagementDetails;
    private String companyType;

    // the following were added for Amadeus, currently not mapped
    private List<CellCode> companyCellCodes;
    private List<RatePlan> restrictedRatePlans;
    private List<HotelCode> restrictedHotelCodes;
    private CompanyStatus companyStatus;

}
