package uk.co.whitbread.shared.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.CustomerAccountRequest;
import uk.co.whitbread.shared.cdh.model.CustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.DeleteCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountResponse;
import uk.co.whitbread.shared.cdh.model.GetCustomerAccountsResponse;
import uk.co.whitbread.shared.cdh.model.SearchCustomerAccountRequest;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;


@ExtendWith(MockitoExtension.class)
class CustomerDataServiceTest {

  private static final String CUSTOMER_ACCOUNT_ID = "0e1afe56-3aed-4f0c-9717-fdc7a324f7ad";
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private CustomerDataHubClient cdhClient;

  @Mock
  private CdhApiProperties cdhApiProperties;

  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;

  @InjectMocks
  private CustomerDataService customerDataService;

  @BeforeEach
  void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  void getCustomerAccount_success() throws IOException {
    when(cdhClient.getCDH(any(), any(), any())).thenReturn(
        Optional.ofNullable(buildGetCustomerAccountResponse()));

    var customerAccount = customerDataService.getCustomerAccount(CUSTOMER_ACCOUNT_ID,
        ACCESSED_BY);

    assertTrue(customerAccount.isPresent());
    assertEquals(CUSTOMER_ACCOUNT_ID, customerAccount.get().getCustomerAccountId());
  }

  @Test
  void getCustomerAccountV3_success() throws IOException {
    when(cdhClient.getCDH(any(), any(), any())).thenReturn(
        Optional.ofNullable(buildGetCustomerAccountResponse()));

    var customerAccount = customerDataService.getCustomerAccountV3(CUSTOMER_ACCOUNT_ID,
        ACCESSED_BY);

    assertTrue(customerAccount.isPresent());
    assertEquals(CUSTOMER_ACCOUNT_ID, customerAccount.get().getCustomerAccountId());
  }

  @Test
  void getCustomerAccountV3_urlIsBuiltCorrectly() {
    var urlCaptor = ArgumentCaptor.forClass(String.class);
    customerDataService.getCustomerAccountV3(CUSTOMER_ACCOUNT_ID, ACCESSED_BY);

    verify(cdhClient).getCDH(urlCaptor.capture(), any(), any());

    var url = urlCaptor.getValue();
    var expectedUrl = HOST + "/AccountServices/V3/customers/" + CUSTOMER_ACCOUNT_ID;
    assertEquals(expectedUrl, url);
  }

  @Test
  void getCustomerAccountList_success() throws IOException {
    final GetCustomerAccountsResponse getCustomerAccountsResponse = GetCustomerAccountsResponse.builder()
        .results(List.of(buildGetCustomerAccountResponse())).build();
    when(cdhClient.getCDH(any(), any(), any())).thenReturn(Optional.of(getCustomerAccountsResponse));

    String email = "test@mail.com";
    Optional<GetCustomerAccountsResponse> customerAccounts = customerDataService.getCustomerAccountList(email);

    assertThat(customerAccounts.isEmpty(), is(false));
    final List<GetCustomerAccountResponse> accountsList = customerAccounts.get().getResults();
    assertThat(accountsList, notNullValue());
    assertThat(accountsList.isEmpty(), is(false));
    assertEquals(CUSTOMER_ACCOUNT_ID, accountsList.get(0).getCustomerAccountId());
  }

  @Test
  void getCustomerAccountListBySearchCustomerAccountRequest_success() throws IOException {
    final GetCustomerAccountsResponse getCustomerAccountsResponse = GetCustomerAccountsResponse.builder()
            .results(List.of(buildGetCustomerAccountResponse())).build();
    when(cdhClient.getCDH(any(), any(), any())).thenReturn(Optional.of(getCustomerAccountsResponse));

    String email = "test@mail.com";
    Optional<GetCustomerAccountsResponse> customerAccounts = customerDataService.getCustomerAccountList(
            SearchCustomerAccountRequest.builder().build(), email);

    assertThat(customerAccounts.isEmpty(), is(false));
    final List<GetCustomerAccountResponse> accountsList = customerAccounts.get().getResults();
    assertThat(accountsList, notNullValue());
    assertThat(accountsList.isEmpty(), is(false));
    assertEquals(CUSTOMER_ACCOUNT_ID, accountsList.get(0).getCustomerAccountId());
  }

  @Test
  void getCustomerAccountListV3_success() throws IOException {
    final GetCustomerAccountsResponse getCustomerAccountsResponse = GetCustomerAccountsResponse.builder()
        .results(List.of(buildGetCustomerAccountResponse())).build();
    when(cdhClient.postCDHOptional(any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(getCustomerAccountsResponse));

    Optional<GetCustomerAccountsResponse> customerAccounts = customerDataService.getCustomerAccountListV3(
        SearchCustomerAccountRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .email("john@test.com")
            .build(),
        ACCESSED_BY);

    assertThat(customerAccounts.isEmpty(), is(false));
    final List<GetCustomerAccountResponse> accountsList = customerAccounts.get().getResults();
    assertThat(accountsList, notNullValue());
    assertThat(accountsList.isEmpty(), is(false));
    assertEquals(CUSTOMER_ACCOUNT_ID, accountsList.get(0).getCustomerAccountId());
  }

  @Test
  void getCustomerAccountListV3_urlIsBuiltCorrectly() {
    var urlCaptor = ArgumentCaptor.forClass(String.class);
    customerDataService.getCustomerAccountListV3(
        SearchCustomerAccountRequest.builder().build(), ACCESSED_BY);

    verify(cdhClient).postCDHOptional(urlCaptor.capture(), any(), any(), any(), any());

    var url = urlCaptor.getValue();
    var expectedUrl = HOST + "/AccountServices/V3/GetCustomerAccounts";
    assertEquals(expectedUrl, url);
  }

  @Test
  void getCustomerAccountListByEmailV3_success() throws IOException {
    final GetCustomerAccountsResponse getCustomerAccountsResponse = GetCustomerAccountsResponse.builder()
        .results(List.of(buildGetCustomerAccountResponse())).build();
    when(cdhClient.postCDHOptional(any(), any(), any(), any(), any()))
        .thenReturn(Optional.of(getCustomerAccountsResponse));

    String email = "test@mail.com";
    Optional<GetCustomerAccountsResponse> customerAccounts =
        customerDataService.getCustomerAccountListV3(email);

    assertThat(customerAccounts.isEmpty(), is(false));
    final List<GetCustomerAccountResponse> accountsList = customerAccounts.get().getResults();
    assertThat(accountsList, notNullValue());
    assertThat(accountsList.isEmpty(), is(false));
    assertEquals(CUSTOMER_ACCOUNT_ID, accountsList.get(0).getCustomerAccountId());
  }

  @Test
  void getCustomerAccountListByEmailV3_urlIsBuiltCorrectly() {
    var urlCaptor = ArgumentCaptor.forClass(String.class);
    customerDataService.getCustomerAccountListV3("test@mail.com");

    verify(cdhClient).postCDHOptional(urlCaptor.capture(), any(), any(), any(), any());

    var url = urlCaptor.getValue();
    var expectedUrl = HOST + "/AccountServices/V3/customers";
    assertEquals(expectedUrl, url);
  }

  @Test
  void createCustomerAccount_success() throws IOException {
    var createCustomerAccountRequest = buildCreateCustomerAccountRequest();
    when(cdhClient.postCDH(any(), any(), any(), any(), any())).thenReturn(
        buildCreateCustomerAccountResponse());

    var customerAccount = customerDataService.createCustomerAccount(
        createCustomerAccountRequest);

    assertEquals(CUSTOMER_ACCOUNT_ID, customerAccount.getCustomerAccountId());
  }

  @Test
  void updateCustomerAccount_success() throws IOException {
    var updateCustomerAccountRequest = buildUpdateCustomerAccountRequest();
    when(cdhClient.putCDH(any(), any(), any(), any(), any())).thenReturn(
        buildUpdateCustomerAccountResponse());

    var customerAccount = customerDataService.updateCustomerAccount(CUSTOMER_ACCOUNT_ID,
        updateCustomerAccountRequest, ACCESSED_BY);

    assertEquals(CUSTOMER_ACCOUNT_ID, customerAccount.getCustomerAccountId());
  }

  @Test
  void deleteCustomerAccount_success() throws IOException {
    when(cdhClient.deleteCDH(any(), any(), any())).thenReturn(buildDeleteCustomerAccountResponse());

    var response = customerDataService.deleteCustomerAccount(CUSTOMER_ACCOUNT_ID, ACCESSED_BY);

    assertEquals(CUSTOMER_ACCOUNT_ID, response.getCustomerAccountId());
  }

  private GetCustomerAccountResponse buildGetCustomerAccountResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_customerAccount_response.json"),
        GetCustomerAccountResponse.class);
  }

  private CustomerAccountRequest buildCreateCustomerAccountRequest() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/create_customerAccount_request.json"),
        CustomerAccountRequest.class);
  }

  private CustomerAccountResponse buildCreateCustomerAccountResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/create_customerAccount_response.json"),
        CustomerAccountResponse.class);
  }

  private CustomerAccountRequest buildUpdateCustomerAccountRequest() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/update_customerAccount_request.json"),
        CustomerAccountRequest.class);
  }

  private CustomerAccountResponse buildUpdateCustomerAccountResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/update_customerAccount_response.json"),
        CustomerAccountResponse.class);
  }

  private DeleteCustomerAccountResponse buildDeleteCustomerAccountResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/delete_customerAccount_response.json"),
        DeleteCustomerAccountResponse.class);
  }
}
