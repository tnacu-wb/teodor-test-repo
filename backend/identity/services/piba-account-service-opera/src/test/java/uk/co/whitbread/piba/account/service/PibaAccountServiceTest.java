package uk.co.whitbread.piba.account.service;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.converter.WorldlineTransformer;
import uk.co.whitbread.piba.account.model.CustomerAccountsResponse;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.CdhProperties;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@ExtendWith(MockitoExtension.class)
class PibaAccountServiceTest {

  @Mock
  private WorldlineTransformer worldlineAccountTransformer;

  @Mock
  private Executor worldLineExecutor;

  @Mock
  private CdhRegistrationService cdhRegistrationService;

  @Mock
  private WebServiceTemplate worldlineWebServiceTemplate;

  @Mock
  private TokenService authTokenService;

  @Mock
  private CdhProperties cdhProperties;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private WorldLineProperties worldLineProperties;

  @Mock
  private WorldLineAccountResponseValidator worldLineResponseValidator;

  @Mock
  private WorldLineService worldLineService;

  @InjectMocks
  private PibaAccountService pibaAccountService;

  @Test
  void getAccountsReturnsCustomerAccountsWithoutWLDetailsWhenIgnoreWLDetailsIsTrue() {
    List<PibaTetheredGuidResponse> tetheredGuidResponses = List.of(
        PibaTetheredGuidResponse.builder().scheme(Scheme.GB).tetheredGuid(List.of("guid1", "guid2")).build(),
        PibaTetheredGuidResponse.builder().scheme(Scheme.GB).tetheredGuid(List.of("guid3")).build()
    );
    when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(),
        anyString())).thenReturn(tetheredGuidResponses);
    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
        CdhEmployeeDetails.builder().employeeAccountId("employeeId")
            .companyAccountId("companyId").userEmail("a@b.c").build());
    when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
        new EmployeeDetails("companyId", "employeeId"));

    CustomerAccountsResponse response = pibaAccountService.getAccounts("authToken", true, true);

    assertThat(response.getAccounts().size(), is(3));
    assertEquals("guid1", response.getAccounts().get(0).getTetheredGuid());
    assertEquals(Scheme.GB, response.getAccounts().get(0).getScheme());
    assertEquals("guid2", response.getAccounts().get(1).getTetheredGuid());
    assertEquals(Scheme.GB, response.getAccounts().get(1).getScheme());
    assertEquals("guid3", response.getAccounts().get(2).getTetheredGuid());
    assertEquals(Scheme.GB, response.getAccounts().get(2).getScheme());
  }

  @Test
  void getAccountsReturnsCustomerAccountsWithWorldlineDetailsWhenIgnoreWorldlineDetailsIsFalse() {
    List<PibaTetheredGuidResponse> tetheredGuidResponses = List.of(
        PibaTetheredGuidResponse.builder().scheme(Scheme.GB).tetheredGuid(List.of("guid1", "guid2")).build(),
        PibaTetheredGuidResponse.builder().scheme(Scheme.GB).tetheredGuid(List.of("guid3")).build()
    );
    when(cdhRegistrationService.getTetheredGuids(anyString(), anyString(),
        anyString())).thenReturn(tetheredGuidResponses);
    when(authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
        CdhEmployeeDetails.builder().employeeAccountId("employeeId")
            .companyAccountId("companyId").userEmail("a@b.c").build());
    when(authTokenService.retrieveEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
        new EmployeeDetails("companyId", "employeeId"));
    when(worldLineService.getUserDetails(any(), any())).thenReturn(
        random(TetheredUserDetailsResponse.class));
    doAnswer(invocation -> {
      Runnable task = invocation.getArgument(0);
      task.run();
      return null;
    }).when(worldLineExecutor).execute(any());

    CustomerAccountsResponse response = pibaAccountService.getAccounts("authToken", true, false);

    assertThat(response.getAccounts().size(), is(3));
    assertEquals("guid1", response.getAccounts().get(0).getTetheredGuid());
    assertEquals(Scheme.GB, response.getAccounts().get(0).getScheme());
    assertEquals("guid2", response.getAccounts().get(1).getTetheredGuid());
    assertEquals(Scheme.GB, response.getAccounts().get(1).getScheme());
    assertEquals("guid3", response.getAccounts().get(2).getTetheredGuid());
    assertEquals(Scheme.GB, response.getAccounts().get(2).getScheme());
  }
}

