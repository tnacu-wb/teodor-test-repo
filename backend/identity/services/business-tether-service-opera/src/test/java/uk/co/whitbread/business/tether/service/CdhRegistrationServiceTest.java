package uk.co.whitbread.business.tether.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.business.tether.model.Scheme;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.cdh.EmployeeDataService;
import uk.co.whitbread.shared.cdh.model.TetheredGuid;
import uk.co.whitbread.shared.cdh.model.TetheredUserRequest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CdhRegistrationServiceTest {

  @InjectMocks
  private CdhRegistrationService cdhRegistrationService;

  @Mock
  private EmployeeDataService employeeDataService;

  @Test
  void testRegisterTetheredGuids_shouldRegisterOk() {
    // Given
    String guid = "test-guid";
    EmployeeDetails employeeDetails = new EmployeeDetails("company-id", "employee-id");
    Scheme scheme = Scheme.GB;
    String accessedBy = "test-user";
    doNothing().when(employeeDataService).registerTetheredUser(any(), any(), any());

    // When
    cdhRegistrationService.registerTetheredGuids(guid, employeeDetails, scheme, accessedBy);

    // Then
    verify(employeeDataService, times(1)).registerTetheredUser(
        TetheredUserRequest.builder()
            .companyId(employeeDetails.getCompanyId())
            .scheme(scheme.toString())
            .tetheredGuids(List.of(new TetheredGuid(employeeDetails.getEmployeeId(), guid)))
            .build(),
        accessedBy, "InnBusiness");
  }
}
