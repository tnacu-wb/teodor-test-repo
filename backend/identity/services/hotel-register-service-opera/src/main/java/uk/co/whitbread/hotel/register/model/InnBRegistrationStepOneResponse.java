package uk.co.whitbread.hotel.register.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InnBRegistrationStepOneResponse {

    private boolean existingCompany;
    private boolean existingEmployee;
    private String existingCompanyType;
}