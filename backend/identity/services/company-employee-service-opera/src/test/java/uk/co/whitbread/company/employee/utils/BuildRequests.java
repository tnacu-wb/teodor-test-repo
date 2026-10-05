package uk.co.whitbread.company.employee.utils;

import java.util.Arrays;
import java.util.List;
import uk.co.whitbread.company.employee.model.AccessLevel;
import uk.co.whitbread.company.employee.model.Address;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.Employee;
import uk.co.whitbread.company.employee.model.EmployeeAnswers;
import uk.co.whitbread.company.employee.model.EmployeeStatus;
import uk.co.whitbread.company.employee.model.EmployeeSummary;
import uk.co.whitbread.company.employee.model.GetEmployeeResponse;
import uk.co.whitbread.company.employee.model.GetEmployeesResponse;
import uk.co.whitbread.company.employee.model.InviteRequest;
import uk.co.whitbread.company.employee.model.PasswordChange;
import uk.co.whitbread.company.employee.model.RoomType;
import uk.co.whitbread.company.employee.model.UpdateRegistrationRequest;
import uk.co.whitbread.company.employee.model.UserDefinedAnswer;

public class BuildRequests {

    private static final String GH_NUMBER = "1234";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smith";
    private static final String EMAIL_ADDRESS = "johnsmith@gmail.com";
    private static final Boolean TEXT_CONFIRMATION = true;
    private static final AccessLevel employeeAccessLevel = AccessLevel.SUPER;
    private static final EmployeeStatus employeeStatus = EmployeeStatus.ACTIVE;
    private static final String EMPLOYEE_PHONE_NUMBER = "07986645334";
    private static final String EMPLOYEE_MOBILE_NUMBER = "07986645335";
    private static final String CENTRAL_CARD_ID = "1";

    private static final Integer ADULTS = 1;
    private static final Integer CHILDREN = 2;
    private static final RoomType ROOM_TYPE = RoomType.FAM;
    private static final Boolean PREMIER_BREAKFAST = false;
    private static final Boolean CONTINENTAL_BREAKFAST = true;
    private static final Boolean MEAL_DEAL = false;
    private static final Boolean PRESELECT_WIFI = true;
    private static final Boolean ELECTRONIC_INVOICE = false;

    private static final String ADDRESS_LINE_1 = "120 Holborn";
    private static final String POSTCODE = "EC1N 2TD";
    private static final String COUNTRY = "GB";

    private static final String CUSTOMER_REFERENCE_ANSWER = "customerReferenceAnswer";
    private static final String PURCHASE_ORDER_ANSWER = "purchaseOrderAnswer";
    private static final UserDefinedAnswer USER_DEFINED_ANSWER = new UserDefinedAnswer("1", "userDefinedAnswer");

    private static final String CURRENT_PASSWORD = "currentPassword";
    private static final String NEW_PASSWORD = "newPassword";

    public static InviteRequest buildInviteRequest(String centralCardId, String emailAddress) {
        InviteRequest inviteRequest = new InviteRequest();
        inviteRequest.setCentralCardId(centralCardId);
        inviteRequest.setEmailAddress(emailAddress);
        return inviteRequest;
    }

    private static EmployeeAnswers buildEmployeeAnswers() {
        EmployeeAnswers employeeAnswers = new EmployeeAnswers();
        employeeAnswers.setCustomerReferenceAnswer(CUSTOMER_REFERENCE_ANSWER);
        employeeAnswers.setPurchaseOrderAnswer(PURCHASE_ORDER_ANSWER);
        employeeAnswers.setUserDefinedAnswers(Arrays.asList(USER_DEFINED_ANSWER));
        return employeeAnswers;
    }

    public static Employee buildEmployee (){
        Employee employee = new Employee();

        employee.setGhNumber(GH_NUMBER);
        employee.setTitle("Mr");
        employee.setFirstName(FIRST_NAME);
        employee.setLastName(LAST_NAME);
        employee.setEmailAddress(EMAIL_ADDRESS);
        employee.setTextConfirmation(TEXT_CONFIRMATION);
        employee.setAccessLevel(employeeAccessLevel);
        employee.setEmployeeStatus(employeeStatus);
        employee.setPhoneNumber(EMPLOYEE_PHONE_NUMBER);
        employee.setMobileNumber(EMPLOYEE_MOBILE_NUMBER);
        employee.setAddress(buildAddress());
        employee.setCentralCardId(CENTRAL_CARD_ID);
        employee.setEmployeeAnswers(buildEmployeeAnswers());
        return employee;
    }

    public static EmployeeSummary buildEmployeeSummary(){
        EmployeeSummary employeeSummary = new EmployeeSummary();

        employeeSummary.setGuestHistoryNumber(GH_NUMBER);
        employeeSummary.setTitle("Mr");
        employeeSummary.setFirstName(FIRST_NAME);
        employeeSummary.setLastName(LAST_NAME);
        employeeSummary.setEmailAddress(EMAIL_ADDRESS);
        employeeSummary.setTextConfirmation(TEXT_CONFIRMATION);
        employeeSummary.setAccessLevel(employeeAccessLevel);
        employeeSummary.setEmployeeStatus(employeeStatus);
        return employeeSummary;
    }

    public static BookingPreference buildBookingPreference() {

        return BookingPreference.builder()
                .adults(ADULTS)
                .children(CHILDREN)
                .roomType(ROOM_TYPE)
                .cotRequired(false)
                .premierBreakfast(PREMIER_BREAKFAST)
                .continentalBreakfast(CONTINENTAL_BREAKFAST)
                .mealDeal(MEAL_DEAL)
                .preselectWifi(PRESELECT_WIFI)
                .electronicInvoice(ELECTRONIC_INVOICE)
                .build();
    }

    public static UpdateRegistrationRequest buildUpdateRegistrationRequest() {
        return new UpdateRegistrationRequest();
    }

    public static PasswordChange buildUpdateChangePassword() {
        return PasswordChange.builder()
                .currentPassword(CURRENT_PASSWORD)
                .newPassword(NEW_PASSWORD)
                .build();
    }

    public static GetEmployeeResponse buildGetEmployeeResponse() {
        GetEmployeeResponse getEmployeeResponse = new GetEmployeeResponse();
        getEmployeeResponse.setEmployee(new Employee());
        getEmployeeResponse.setSuccess(true);
        return getEmployeeResponse;
    }

    public static GetEmployeesResponse buildGetEmployeesResponse() {
        List<EmployeeSummary> employees = List.of(buildEmployeeSummary());
        return GetEmployeesResponse.builder()
            .employees(employees)
            .build();
    }

    private static Address buildAddress() {

        return Address.builder()
                .addressLine1(ADDRESS_LINE_1)
                .postCode(POSTCODE)
                .country(COUNTRY)
                .build();
    }
}
