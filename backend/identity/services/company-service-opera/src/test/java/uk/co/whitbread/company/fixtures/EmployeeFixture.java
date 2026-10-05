package uk.co.whitbread.company.fixtures;


import java.util.UUID;
import uk.co.whitbread.company.model.EmployeeStatus;
import uk.co.whitbread.company.model.MainContact;
import uk.co.whitbread.shared.cdh.AccessLevel;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;

public class EmployeeFixture {

    public static GetEmployeeResponse buildGetEmployeeResponse(String companyAccountId) {
        return GetEmployeeResponse.builder()
            .employeeAccountId(UUID.randomUUID().toString())
            .companyAccountId(companyAccountId)
            .accessLevel(AccessLevel.SUPER.name())
            .employeeStatus(EmployeeStatus.ACTIVE.getValue())
            .title("Mr")
            .firstName("John")
            .lastName("Smith")
            .emailAddress("john.smith@mail.com")
            .phoneNumber("12345678")
            .mobileNumber("87654321")
            .position("CEO")
            .build();
    }

    public static MainContact buildMainContact() {
        return MainContact.builder()
            .id(UUID.randomUUID().toString())
            .title("Ms")
            .firstName("Jane")
            .lastName("Doe")
            .emailAddress("jane.doe@mail.com")
            .phoneNumber("123456789")
            .mobileNumber("987654321")
            .position("Developer")
            .build();
    }
}
