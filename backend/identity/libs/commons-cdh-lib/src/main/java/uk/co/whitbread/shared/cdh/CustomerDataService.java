package uk.co.whitbread.shared.cdh;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.model.*;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

import java.util.Optional;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.*;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDataService {

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;

  /**
   * Get customer account based on customerAccountId
   *
   * @param customerAccountId Id of the customer in CDH
   * @param accessedBy        information about who is making the request
   * @return customer profile details, if found in CDH
   */
  public Optional<GetCustomerAccountResponse> getCustomerAccount(String customerAccountId,
      String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + CUSTOMER_ACCOUNT);
    return cdhClient.getCDH(builder.buildAndExpand(customerAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetCustomerAccountResponse.class);
  }

  /**
   * Get customer account based on customerAccountId using V3 endpoint.
   *
   * @param customerAccountId Id of the customer in CDH
   * @param accessedBy        information about who is making the request
   * @return customer profile details, if found in CDH
   */
  public Optional<GetCustomerAccountResponse> getCustomerAccountV3(String customerAccountId,
      String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V3 + CUSTOMER_ACCOUNT);
    return cdhClient.getCDH(builder.buildAndExpand(customerAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetCustomerAccountResponse.class);
  }

  /**
   * Get customer accounts based on complex search criteria
   *
   * @param request    composite search criteria used as query params
   * @param accessedBy information about who is making the request
   * @return the response, containing a full or partial list of the matching customers
   */
  public Optional<GetCustomerAccountsResponse> getCustomerAccountList(SearchCustomerAccountRequest request,
      String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
            cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V2 + CUSTOMER_ACCOUNT_LIST)
        .queryParam("FirstName", request.getFirstName())
        .queryParam("LastName", request.getLastName())
        .queryParam("Email", request.getEmail())
        .queryParam("CompanyName", request.getCompanyName())
        .queryParam("AddressLine1", request.getAddressLine())
        .queryParam("PostCode", request.getPostCode())
        .queryParam("Mobile", request.getMobile())
        .queryParam("Telephone", request.getTelephone());
    HttpHeaders headers = buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    return cdhClient.getCDH(builder.toUriString(), headers, GetCustomerAccountsResponse.class);
  }

  /**
   * Get customer accounts based on complex search criteria using V3 endpoint (POST with request
   * body).
   *
   * @param request    composite search criteria used as request body
   * @param accessedBy information about who is making the request
   * @return the response, containing a full or partial list of the matching customers
   */
  public Optional<GetCustomerAccountsResponse> getCustomerAccountListV3(
      SearchCustomerAccountRequest request, String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V3 + CUSTOMER_ACCOUNT_LIST_V3);

    final SearchCustomerAccountV3Request requestBody = SearchCustomerAccountV3Request.builder()
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .email(request.getEmail())
        .companyName(request.getCompanyName())
        .addressLine(request.getAddressLine())
        .postCode(request.getPostCode())
        .mobile(request.getMobile())
        .telephone(request.getTelephone())
        .build();

    return cdhClient.postCDHOptional(builder.build().toUriString(), requestBody,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        SearchCustomerAccountV3Request.class, GetCustomerAccountsResponse.class);
  }

  /**
   * Get customer accounts based on email
   *
   * @param email of the PI customers in CDH
   * @return the response, containing a full or partial list of the matching customers, and a
   * continuation token for the next page
   */
  public Optional<GetCustomerAccountsResponse> getCustomerAccountList(String email) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
            cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + CUSTOMERS)
        .queryParam("Email", email);
    return cdhClient.getCDH(builder.build().toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), email),
        GetCustomerAccountsResponse.class);
  }

  /**
   * Get customer accounts based on email (V3 - POST with request body).
   *
   * @param email of the PI customers in CDH
   * @return the response, containing a full or partial list of the matching customers
   */
  public Optional<GetCustomerAccountsResponse> getCustomerAccountListV3(String email) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V3 + CUSTOMERS);

    final SearchCustomerAccountV3Request requestBody = SearchCustomerAccountV3Request.builder()
        .email(email)
        .build();

    return cdhClient.postCDHOptional(builder.build().toUriString(), requestBody,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), email),
        SearchCustomerAccountV3Request.class, GetCustomerAccountsResponse.class);
  }

  /**
   * Create a customer account
   *
   * @param customerAccountRequest Profile details of the customer
   * @return the response for the create operation
   */
  public CustomerAccountResponse createCustomerAccount(
      CustomerAccountRequest customerAccountRequest) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + CUSTOMERS);
    String accessedBy = customerAccountRequest.getContactDetail().getEmail();
    return cdhClient.postCDH(builder.build().toUriString(), customerAccountRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        CustomerAccountRequest.class, CustomerAccountResponse.class);
  }

  /**
   * Update an existent customer account
   *
   * @param customerAccountRequest Profile details of the customer
   * @param accessedBy             information about who is making the request
   * @return the response for the update operation
   */
  public CustomerAccountResponse updateCustomerAccount(String customerAccountId,
      CustomerAccountRequest customerAccountRequest, String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + CUSTOMER_ACCOUNT);
    return cdhClient.putCDH(builder.buildAndExpand(customerAccountId).toUriString(),
        customerAccountRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        CustomerAccountRequest.class, CustomerAccountResponse.class);
  }

  /**
   * Delete an existing user account.
   *
   * @param customerAccountId Id of the customer in CDH
   * @param accessedBy        information about who is making the request
   * @return the response for the delete operation
   */
  public DeleteCustomerAccountResponse deleteCustomerAccount(String customerAccountId,
      String accessedBy) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + CUSTOMER_ACCOUNT);
    return cdhClient.deleteCDH(builder.buildAndExpand(customerAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        DeleteCustomerAccountResponse.class);
  }
}
