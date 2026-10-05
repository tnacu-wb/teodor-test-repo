package uk.co.whitbread.employee.bulk.utils;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import com.opencsv.CSVWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.employee.bulk.mapper.EmployeeMapper;
import uk.co.whitbread.employee.bulk.model.AccessLevel;
import uk.co.whitbread.employee.bulk.model.Address;
import uk.co.whitbread.employee.bulk.model.Employee;
import uk.co.whitbread.employee.bulk.model.EmployeeStatus;
import uk.co.whitbread.employee.bulk.model.HeaderProperties;
import uk.co.whitbread.employee.bulk.model.UserDefinedAnswer;
import uk.co.whitbread.employee.bulk.model.companyEmployees.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.company.CompanyManagementDetails;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;

@Component
@RequiredArgsConstructor
public class EmployeeFileWriter {

    private static final String RECORD_DATA_SEPARATOR = "\\s*~\\s*";
    private static final String DATA_SEPARATOR = "~";
    private static final String HEADER_FIELDS = "Title~First Name~Last Name~Status~Text Confirmation~Position~Central Card ID~Access Level~Address Line 1~Address Line 2~Address Line 3~Address Line 4~Address Line 5~Postcode~Country~Email Address~Phone~Mobile";

    private final EmployeeMapper employeeMapper;

    public String[] getCsvHeaderFields (String headerFields) {
        return headerFields.split(RECORD_DATA_SEPARATOR);
    }

    public HeaderProperties getCsvHeaderFieldsForCdh (GetCompanyResponse company) {
        String headerFields = HEADER_FIELDS;
        List<String> questionIds = new ArrayList<>();
        CompanyManagementDetails managementDetails = company.getCompanyManagementDetails();
        if (Objects.nonNull(managementDetails)) {
            if (Objects.nonNull(managementDetails.getCustomerReferenceManagement())) {
                headerFields =
                    headerFields + DATA_SEPARATOR + managementDetails.getCustomerReferenceManagement()
                        .getLabel();
            }
            if (Objects.nonNull(managementDetails.getPurchaseOrderManagement())) {
                headerFields =
                    headerFields + DATA_SEPARATOR + managementDetails.getPurchaseOrderManagement()
                        .getLabel();
            }
            List<GetQuestionResponse> questions = managementDetails.getQuestions();
            if (Objects.nonNull(questions)) {
                for (GetQuestionResponse question : questions) {
                    headerFields = headerFields + DATA_SEPARATOR + question.getLabel();
                    questionIds.add(question.getId());
                }
            }
        }
        return buildHeaderProperties(headerFields, questionIds);
    }

    private HeaderProperties buildHeaderProperties(String headerFields, List<String> questionIds) {
        return HeaderProperties.builder()
            .headerValues(headerFields.split(RECORD_DATA_SEPARATOR))
            .headerQuestionIds(questionIds)
            .build();
    }

    private void writeEmployeeRecord(final CSVWriter csvWriter, final Employee employee,
        final List<String> headerQuestionIds) {

        final Optional<Address> address = Optional.ofNullable(employee.getAddress());

        List<String> csvRowFieldList = new ArrayList<>();
        csvRowFieldList.add(employee.getTitle());
        csvRowFieldList.add(employee.getFirstName());
        csvRowFieldList.add(employee.getLastName());
        csvRowFieldList.add(Optional.ofNullable(employee.getEmployeeStatus()).map(EmployeeStatus::name).orElse(null));
        csvRowFieldList.add(employee.isTextConfirmation() ? "YES" : "NO");
        csvRowFieldList.add(employee.getPosition());
        csvRowFieldList.add(employee.getCentralCardId());
        csvRowFieldList.add(Optional.ofNullable(employee.getAccessLevel()).map(AccessLevel::name).orElse(null));
        csvRowFieldList.add(address.map(Address::getAddressLine1).orElse(null));
        csvRowFieldList.add(address.map(Address::getAddressLine2).orElse(null));
        csvRowFieldList.add(address.map(Address::getAddressLine3).orElse(null));
        csvRowFieldList.add(address.map(Address::getAddressLine4).orElse(null));
        csvRowFieldList.add(address.map(Address::getAddressLine5).orElse(null));
        csvRowFieldList.add(address.map(Address::getPostCode).orElse(null));
        csvRowFieldList.add(address.map(Address::getCountry).orElse(null));
        csvRowFieldList.add(employee.getEmailAddress());
        csvRowFieldList.add(employee.getPhoneNumber());
        csvRowFieldList.add(employee.getMobileNumber());
        if (employee.getEmployeeAnswers() != null) {
            csvRowFieldList.add(employee.getEmployeeAnswers().getCustomerReferenceAnswer());
            csvRowFieldList.add(employee.getEmployeeAnswers().getPurchaseOrderAnswer());
            List<UserDefinedAnswer> answers = employee.getEmployeeAnswers().getUserDefinedAnswers();
            if (Objects.nonNull(answers)) {
                if (Objects.isNull(headerQuestionIds)) {
                  answers
                      .forEach(userDefinedAnswer -> csvRowFieldList.add(userDefinedAnswer.getMiAnswer()));
                } else {
                    List<String> answerIds = getAnswerIds(answers);
                    headerQuestionIds.forEach(questionId -> {
                        if (answerIsPresent(answerIds, questionId)) {
                            csvRowFieldList.add(getAnswerByQuestionId(answers, questionId));
                        } else {
                            csvRowFieldList.add("");
                        }
                    });
                }
            }
        }

        csvWriter.writeNext(csvRowFieldList.toArray(new String[0]));
    }

    private static boolean answerIsPresent(List<String> answerIds, String questionId) {
        return answerIds.contains(questionId);
    }

    private static String getAnswerByQuestionId(List<UserDefinedAnswer> answers, String questionId) {
        Optional<UserDefinedAnswer> answerOpt = answers.stream()
            .filter(Objects::nonNull)
            .filter(q -> questionId.equals(q.getMiID()))
            .findFirst();

        if (answerOpt.isPresent()) {
            return answerOpt.get().getMiAnswer();
        } else {
            return "";
        }
    }

    private static List<String> getAnswerIds(List<UserDefinedAnswer> answers) {
        return answers.stream()
            .filter(Objects::nonNull)
            .filter(q -> Objects.nonNull(q.getMiID()))
            .map(q -> q.getMiID())
            .toList();
    }

    public void populateCsv(final HeaderProperties headerProperties, final CSVWriter csvWriter,
        final List<Employee> employees) throws IOException {

        csvWriter.writeNext(headerProperties.getHeaderValues());

        emptyIfNull(employees).forEach(employee -> writeEmployeeRecord(csvWriter, employee,
            headerProperties.getHeaderQuestionIds()));

        csvWriter.flush();
        csvWriter.close();
    }

    public List<Employee> convertToEmployeeList (List<GetEmployeeResponse> records) {
        return records.stream()
            .map(employeeMapper::toEmployee)
            .toList();
    }
}
