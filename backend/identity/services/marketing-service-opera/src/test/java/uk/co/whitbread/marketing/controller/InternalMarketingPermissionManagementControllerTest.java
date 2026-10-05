package uk.co.whitbread.marketing.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.permissionmanagement.Customer;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequestV2;
import uk.co.whitbread.marketing.service.PermissionManagementService;
import uk.co.whitbread.marketing.utils.RequestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class InternalMarketingPermissionManagementControllerTest {

  @Mock
  private PermissionManagementService permissionManagementService;

  @Mock
  private RequestUtils requestUtils;

  @InjectMocks
  private InternalMarketingPermissionManagementController controller;

  @Test
  void updatePermissions_shouldReturnNoContent() {
    var request = createUpdatePreferencesRequest();
    doNothing().when(requestUtils).validateUpdatePreferences(any(), any(), any());
    doNothing().when(permissionManagementService).updatePreferences(any(), any(), any());

    ResponseEntity<Void> response = controller.updatePermissions(request);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(requestUtils).validateUpdatePreferences(request, "test@example.com", ContactType.email);
    verify(permissionManagementService).updatePreferences(request, "test@example.com", ContactType.email);
  }

  private UpdatePreferencesRequestV2 createUpdatePreferencesRequest() {
    return UpdatePreferencesRequestV2.builder()
        .brandCodes(new String[]{"PINN"})
        .optIn(true)
        .secondPartyOptIn(true)
        .thirdPartyVendorsOptIn(false)
        .doubleOptIn(true)
        .customer(
            Customer.builder().firstName("Liam").language("en").countryOfResidence("GB").nationality("GB").build())
        .contactValue("test@example.com")
        .build();
  }

}