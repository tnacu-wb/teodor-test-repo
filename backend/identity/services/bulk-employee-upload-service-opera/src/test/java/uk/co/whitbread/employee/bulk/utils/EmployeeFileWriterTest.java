package uk.co.whitbread.employee.bulk.utils;

import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.employee.bulk.mapper.EmployeeMapper;
import uk.co.whitbread.employee.bulk.model.AccessLevel;
import uk.co.whitbread.employee.bulk.model.Address;
import uk.co.whitbread.employee.bulk.model.Employee;
import uk.co.whitbread.employee.bulk.model.EmployeeAnswers;
import uk.co.whitbread.employee.bulk.model.EmployeeStatus;
import uk.co.whitbread.employee.bulk.model.HeaderProperties;
import uk.co.whitbread.employee.bulk.model.UserDefinedAnswer;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.company.CompanyManagementDetails;
import uk.co.whitbread.shared.cdh.model.company.CompanyQuestion;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;

@ExtendWith(MockitoExtension.class)
class EmployeeFileWriterTest {
    private static final String CUSTOMER_REFERENCE_QUESTION = "Customer Reference Question";
    private static final String PURCHASE_REFERENCE_QUESTION = "Purchase Reference Question";
    private static final String QUESTION_2 = "Question 2";
    private static final String QUESTION_3 = "Question 3";
    private static final String[] EXPECTED_FILE_HEADERS =
            {"Title", "First Name", "Last Name", "Status", "Text Confirmation", "Position", "Central Card ID",
                    "Access Level", "Address Line 1", "Address Line 2", "Address Line 3", "Address Line 4", "Address Line 5",
                    "Postcode", "Country", "Email Address", "Phone", "Mobile"};

    private static final String[] EXPECTED_FILE_HEADERS_WITH_QUESTIONS =
        {"Title", "First Name", "Last Name", "Status", "Text Confirmation", "Position", "Central Card ID",
            "Access Level", "Address Line 1", "Address Line 2", "Address Line 3", "Address Line 4", "Address Line 5",
            "Postcode", "Country", "Email Address", "Phone", "Mobile",
            CUSTOMER_REFERENCE_QUESTION, PURCHASE_REFERENCE_QUESTION, QUESTION_2, QUESTION_3};
    private static final String GET_EMPLOYEE_LIST_HEADER = "Title~First Name~Last Name~Status~Text Confirmation~Position~Central Card ID~Access Level~Address Line 1~Address Line 2~Address Line 3~Address Line 4~Address Line 5~Postcode~Country~Email Address~Phone~Mobile";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CSVWriter csvWriter;
    @Mock
    private EmployeeMapper employeeMapper;

    @Captor
    private ArgumentCaptor<String[]> argumentCaptorForFileHeader;

    @InjectMocks
    private EmployeeFileWriter employeeFileWriter;

    @Test
    void testPopulateHeaderArray() {
        String[] csvHeaderFields = employeeFileWriter.getCsvHeaderFields(GET_EMPLOYEE_LIST_HEADER);
        assertArrayEquals(EXPECTED_FILE_HEADERS, csvHeaderFields);
    }

    @Test
    void testPopulateHeaderArrayForCdh() {
        // GIVEN
        GetCompanyResponse company = GetCompanyResponse.builder().build();

        // WHEN
        HeaderProperties csvHeaderProperties = employeeFileWriter.getCsvHeaderFieldsForCdh(company);

        // THEN
        assertNotNull(csvHeaderProperties);
        assertArrayEquals(EXPECTED_FILE_HEADERS, csvHeaderProperties.getHeaderValues());
        assertEquals(EMPTY_LIST, csvHeaderProperties.getHeaderQuestionIds());
    }

    @Test
    void testPopulateHeaderArrayForCdhWithQuestions() {
        // GIVEN
        CompanyManagementDetails managementDetails = CompanyManagementDetails.builder()
            .customerReferenceManagement(buildQuestion(0, CUSTOMER_REFERENCE_QUESTION))
            .purchaseOrderManagement(buildQuestion(1, PURCHASE_REFERENCE_QUESTION))
            .questions(List.of(buildGetQuestionResponse("ABC-123", 2, QUESTION_2),
                buildGetQuestionResponse("DEF-456", 3, QUESTION_3))).build();
        GetCompanyResponse company = GetCompanyResponse.builder()
            .companyManagementDetails(managementDetails).build();

        // WHEN
        HeaderProperties csvHeaderProperties = employeeFileWriter.getCsvHeaderFieldsForCdh(company);

        // THEN
        assertNotNull(csvHeaderProperties);
        assertArrayEquals(EXPECTED_FILE_HEADERS_WITH_QUESTIONS, csvHeaderProperties.getHeaderValues());
        assertEquals(List.of("ABC-123","DEF-456"), csvHeaderProperties.getHeaderQuestionIds());
    }

    @Test
    void testConvertToCdHEmployeeList() throws IOException {
        // GIVEN
        GetEmployeeResponse getEmployee = buildGetEmployeeResponse();
        when(employeeMapper.toEmployee(any())).thenReturn(new Employee());

        // WHEN
        List<Employee> employeeList = employeeFileWriter.convertToEmployeeList(List.of(getEmployee));

        // THEN
        assertEquals(1, employeeList.size());
        verify(employeeMapper).toEmployee(any());
    }

    @Test
    void testPopulateCsv_forNullList() throws IOException {

        // WHEN
        employeeFileWriter.populateCsv(HeaderProperties.builder().headerValues(EXPECTED_FILE_HEADERS).build(), csvWriter, null);

        // THEN
        verify(csvWriter).writeNext(argumentCaptorForFileHeader.capture());
        List<String[]> fileRecords = argumentCaptorForFileHeader.getAllValues();
        assertEquals(1, fileRecords.size());
        assertArrayEquals(EXPECTED_FILE_HEADERS, fileRecords.getFirst());
    }

    @Test
    void testPopulateCsv_forListOfEmployees() throws IOException {

        // GIVEN
        Employee employee1 = buildEmployee1();
        Employee employee2 = buildEmployee2();
        Employee employee3 = buildEmployee3();

        // WHEN
        employeeFileWriter.populateCsv(HeaderProperties.builder().headerValues(EXPECTED_FILE_HEADERS).build(), csvWriter, asList(employee1, employee2, employee3));

        // THEN
        verify(csvWriter, times(4)).writeNext(argumentCaptorForFileHeader.capture());
        List<String[]> fileRecords = argumentCaptorForFileHeader.getAllValues();
        assertEquals(4, fileRecords.size());

        assertArrayEquals(EXPECTED_FILE_HEADERS, fileRecords.get(0));

        verifyEmployeeRecord(employee1, fileRecords.get(1), false);
        verifyEmployeeRecord(employee2, fileRecords.get(2), false);
        verifyEmployeeRecord(employee3, fileRecords.get(3), true);
    }

    @Test
    void testPopulateCsv_forListOfEmployeesWithQuestions() throws IOException {

        // GIVEN
        Employee employee1 = buildEmployee1();
        Employee employee2 = buildEmployee2();
        Employee employee3 = buildEmployee3();

        // WHEN
        employeeFileWriter.populateCsv(HeaderProperties.builder().headerValues(EXPECTED_FILE_HEADERS).headerQuestionIds(List.of("ABC_123-456-00bbcc","XYZ_987-456-00mmpp")).build(), csvWriter, asList(employee1, employee2, employee3));

        // THEN
        verify(csvWriter, times(4)).writeNext(argumentCaptorForFileHeader.capture());
        List<String[]> fileRecords = argumentCaptorForFileHeader.getAllValues();
        assertEquals(4, fileRecords.size());

        assertArrayEquals(EXPECTED_FILE_HEADERS, fileRecords.get(0));

        verifyEmployeeRecord(employee1, fileRecords.get(1), false);
        verifyEmployeeRecord(employee2, fileRecords.get(2), false);
        verifyEmployeeRecord(employee3, fileRecords.get(3), true);
    }

    private void verifyEmployeeRecord(final Employee employee, final String[] recordFields, boolean includesUserDefinedFields) {

        Optional<Address> address = Optional.ofNullable(employee.getAddress());

        assertNull(recordFields[0]);
        assertEquals(employee.getFirstName(), recordFields[1]);
        assertEquals(employee.getLastName(), recordFields[2]);
        assertEquals(Optional.ofNullable(employee.getEmployeeStatus()).map(EmployeeStatus::name).orElse(null), recordFields[3]);
        assertEquals(employee.isTextConfirmation() ? "YES": "NO", recordFields[4]);
        assertNull(recordFields[5]);
        assertNull(recordFields[6]);
        assertEquals(Optional.ofNullable(employee.getAccessLevel()).map(AccessLevel::name).orElse(null), recordFields[7]);
        assertEquals(address.map(Address::getAddressLine1).orElse(null), recordFields[8]);
        assertEquals(address.map(Address::getAddressLine2).orElse(null), recordFields[9]);
        assertEquals(address.map(Address::getAddressLine3).orElse(null), recordFields[10]);
        assertEquals(address.map(Address::getAddressLine4).orElse(null), recordFields[11]);
        assertEquals(address.map(Address::getAddressLine5).orElse(null), recordFields[12]);
        assertEquals(address.map(Address::getPostCode).orElse(null), recordFields[13]);
        assertEquals(address.map(Address::getCountry).orElse(null), recordFields[14]);
        assertEquals(employee.getEmailAddress(), recordFields[15]);
        assertEquals(employee.getPhoneNumber(), recordFields[16]);
        assertEquals(employee.getMobileNumber(), recordFields[17]);

        if (includesUserDefinedFields) {
            assertEquals(employee.getEmployeeAnswers().getCustomerReferenceAnswer(), recordFields[18]);
            assertEquals(employee.getEmployeeAnswers().getPurchaseOrderAnswer(), recordFields[19]);
            assertEquals(employee.getEmployeeAnswers().getUserDefinedAnswers().getFirst().getMiAnswer(), recordFields[20]);
        }
    }

    private Employee buildEmployee1() {
        Employee employee = new Employee();
        employee.setFirstName("FirstName1");
        employee.setLastName("LastName1");
        return employee;
    }

    private Employee buildEmployee2() {
        Address address = new Address();
        address.setAddressLine1("line1");
        address.setAddressLine2("line2");
        address.setAddressLine3("line3");
        address.setAddressLine4("line4");
        address.setAddressLine1("line5");
        address.setPostCode("postcode");
        address.setCountry("GB");

        Employee employee = new Employee();
        employee.setFirstName("FirstName2");
        employee.setLastName("LastName2");
        employee.setEmailAddress("email@whitbread.com");
        employee.setAccessLevel(AccessLevel.BOOKER);
        employee.setAddress(address);
        employee.setEmployeeStatus(EmployeeStatus.ACTIVE);
        employee.setPhoneNumber("7777777777");
        return employee;
    }

    private Employee buildEmployee3() {
        Employee employee = new Employee();
        employee.setFirstName("FirstName1");
        employee.setLastName("LastName1");
        EmployeeAnswers employeeAnswers = new EmployeeAnswers();
        employeeAnswers.setCustomerReferenceAnswer("My Ref");
        employeeAnswers.setPurchaseOrderAnswer("Tester");
        List<UserDefinedAnswer> userDefinedAnswerList = new ArrayList<>();
        UserDefinedAnswer userDefinedAnswer = new UserDefinedAnswer();
        userDefinedAnswer.setMiAnswer("Test Manager");
        userDefinedAnswer.setMiID("ABC_123-456-00bbcc");
        userDefinedAnswerList.add(userDefinedAnswer);
        employeeAnswers.setUserDefinedAnswers(userDefinedAnswerList);
        employee.setEmployeeAnswers(employeeAnswers);
        return employee;
    }

    private CompanyQuestion buildQuestion(int position, String label) {
        return CompanyQuestion.builder().position(position).label(label).build();
    }

    private GetQuestionResponse buildGetQuestionResponse(String id, int position, String label){
        return GetQuestionResponse.builder().id(id).position(position).label(label).build();
    }


    private GetEmployeeResponse buildGetEmployeeResponse() throws IOException {
        return objectMapper.readValue(
            new File("src/test/resources/__files/get_cdh_employee_response.json"),
            GetEmployeeResponse.class);
    }
}
