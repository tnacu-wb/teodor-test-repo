package uk.co.whitbread.shared.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@ExtendWith(MockitoExtension.class)
class RegistrationDataServiceTest {

  private static final String COMPANY_ACCOUNT_ID = "COMP_5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final Integer COMPANY_ID = 35176;
  private static final String EMPLOYEE_ACCOUNT_ID = "EMPL_9a1ba61e-7bb4-4862-8dd0-68c931c1b436";
  private static final Integer EMPLOYEE_ID = 6;
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String ACCESS_CONTEXT = "InBusiness";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";


  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;
  @Mock
  private CustomerDataHubClient cdhClient;

  @InjectMocks
  private RegistrationDataService registrationDataService;

  @BeforeEach
  public void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  void getDashboardDetailes_success() {

    GetDashboardDetailsQueryParams getDashboardDetailsQueryParams = GetDashboardDetailsQueryParams
        .builder().companyId(COMPANY_ACCOUNT_ID).employeeId(EMPLOYEE_ACCOUNT_ID).build();

    when(cdhClient.getListCDH(anyString(), any(HttpHeaders.class),
        eq(PibaTetheredGuidResponse.class)))
        .thenReturn(buildResponse());

    var pibaTetheredGuidResponseList = registrationDataService.
        getDashboardDetails(getDashboardDetailsQueryParams, ACCESSED_BY, ACCESS_CONTEXT);
    assertEquals(1, pibaTetheredGuidResponseList.size());
    assertEquals(COMPANY_ID, pibaTetheredGuidResponseList.get(0).getCompanyId());
    assertEquals(EMPLOYEE_ID, pibaTetheredGuidResponseList.get(0).getEmployeeId());
  }

  private List<PibaTetheredGuidResponse> buildResponse() {

    List<PibaTetheredGuidResponse> list = new ArrayList<>();
    list.add(PibaTetheredGuidResponse.builder()
        .tetheredGuid("417c6ea1-b7fc-4a55-a191-3d43083ea0f4")
        .scheme("GB")
        .companyId(COMPANY_ID)
        .employeeId(EMPLOYEE_ID)
        .build());

    return list;
  }


}
