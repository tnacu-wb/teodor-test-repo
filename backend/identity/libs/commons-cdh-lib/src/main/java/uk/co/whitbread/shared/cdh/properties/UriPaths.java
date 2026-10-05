package uk.co.whitbread.shared.cdh.properties;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class UriPaths {

  public static final String ACCOUNT_SERVICES_ENDPOINT = "/AccountServices/V1";
  public static final String ACCOUNT_SERVICES_ENDPOINT_V2 = "/AccountServices/V2";
  public static final String ACCOUNT_SERVICES_ENDPOINT_V3 = "/AccountServices/V3";
  public static final String BOOKING_SERVICES_ENDPOINT = "/BookingServices/V1";
  public static final String BOOKING_SERVICES_ENDPOINT_V2 = "/BookingServices/V2";
  public static final String IB_PAY_SERVICES_ENDPOINT = "/IBPay/V1";
  public static final String REGISTRATION = "/Registration";
  public static final String IB_SERVICES_ENDPOINT = "/IB/V1";
  public static final String APPLICATION = "/Application";
  public static final String APPLICATIONS = "/Applications";
  public static final String START_APPLICATION_ENDPOINT = IB_PAY_SERVICES_ENDPOINT + APPLICATION + "/Start";
  public static final String UPDATE_APPLICATION_ENDPOINT = IB_PAY_SERVICES_ENDPOINT + APPLICATION ;
  public static final String FETCH_APPLICATIONS_ENDPOINT = IB_PAY_SERVICES_ENDPOINT + "/FetchApplicationsByUser/{companyId}/{participantId}" ;
  public static final String FETCH_APPLICATION_ENDPOINT = IB_PAY_SERVICES_ENDPOINT + APPLICATIONS;
  public static final String UPDATE_APPLICATION_STATUS_ENDPOINT = IB_PAY_SERVICES_ENDPOINT + APPLICATION + "/Status";
  public static final String UPDATE_APPLICATION_CARD_HOLDERS_ENDPOINT = IB_PAY_SERVICES_ENDPOINT + APPLICATION + "/CardHolders";
  public static final String REPORT = "/Report";
  public static final String CUSTOMERS = "/customers";
  public static final String EMPLOYEES = "/employees";
  public static final String COMPANIES = "/companies";
  public static final String COMPANY = COMPANIES + "/{companyId}";
  public static final String CUSTOMER_ACCOUNT = CUSTOMERS + "/{customerAccountId}";
  public static final String CUSTOMER_ACCOUNT_LIST = "/GetCustomerAccountsV2";
  public static final String CUSTOMER_ACCOUNT_LIST_V3 = "/GetCustomerAccounts";
  public static final String EMPLOYEE_ACCOUNT = EMPLOYEES + "/{employeeAccountId}";
  public static final String COMPANY_ACCOUNT = COMPANIES + "/{companyAccountId}";
  public static final String COMPANY_LEVEL_DETAILS = REPORT + "/CompanySpend/{companyAccountId}";
  public static final String ACCOUNT_LEVEL_DETAILS = REPORT + "/PIBAAccountSpend/{PIBAAccountId}";
  public static final String TRANSACTIONS_DETAILS = REPORT + "/transactions";
  public static final String ACTIVATION_KEY = "/activationKey";
  public static final String RESERVATION_SEARCH = "/ReservationSearch";
  public static final String CARDS = "/cards";
  public static final String CARD = CARDS + "/{cardId}";
  public static final String QUESTIONS = "/questions";
  public static final String QUESTION = QUESTIONS + "/{questionId}";
  public static final String ACTIVATE = "/activate";
  public static final String TETHERED_USER = "/TetheredUser";
  public static final String UPCOMING_BOOKINGS_ENDPOINT = IB_SERVICES_ENDPOINT + "/employees/{employeeAccountId}/companies/{companyAccountId}";
}
