package uk.co.whitbread.employee.bulk.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.ByteArrayOutputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.employee.bulk.model.BookingChannel;
import uk.co.whitbread.employee.bulk.model.EmployeeListRequest;
import uk.co.whitbread.employee.bulk.service.CdhBulkEmployeeService;

@Slf4j
@RequestMapping("/companies")
@RestController
@Tag(name = "Bulk Upload Employees operations")
@AllArgsConstructor
public class BulkEmployeeController {
    private static final String EMPLOYEES_CSV_CONTENT_DISPOSITION = "attachment; filename=Company_Employees.csv";
    private final CdhBulkEmployeeService cdhBulkEmployeeService;

    @Operation(summary = "getEmployeesList", description = "Get employees list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good.",
                    content = @Content(schema = @Schema(implementation = byte[].class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})

    @Parameter(in = ParameterIn.PATH, name = "company-id", description = "CompanyId",
        required = true, content = @Content(schema = @Schema(type = "string", defaultValue = "46")))
    @Parameter(in = ParameterIn.QUERY, name = "fullListMarker", description = "fullListMarker",
        content = @Content(schema = @Schema(type = "boolean", defaultValue = "false")))
    @Parameter(in = ParameterIn.QUERY, name = "recordsPerPage", description = "Maximum number of results to return per page. Should return this many unless on last page.",
        content = @Content(schema = @Schema(type = "integer", defaultValue = "10")))
    @Parameter(in = ParameterIn.QUERY, name = "pageRequired", description = "Required page number.",
        content = @Content(schema = @Schema(type = "integer", defaultValue = "1")))
    @Parameter(in = ParameterIn.HEADER, name = "bookingChannel", description = "The Booking-Channel to use.",
        content = @Content(schema = @Schema(type = "string", defaultValue = "CBT", allowableValues = {"CBT", "MOBILE", "WEB", "WEB_DE"})))
    @Parameter(in = ParameterIn.HEADER, name = "session-id", description = "SessionId",
        content = @Content(schema = @Schema(type = "string", defaultValue = "BCQR98267")))
    @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Authorization",
        content = @Content(schema = @Schema(type = "string", defaultValue = "Authorization-JWT")))
    @GetMapping(value = "/{company-id}/employees/bulk",
        produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<byte[]> getEmployeesList(
            @Parameter @RequestHeader(value = "session-id", required = false) String sessionId,
            @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter(required = true) @PathVariable("company-id") String companyId,
            @Parameter(hidden = true) @Validated @ModelAttribute EmployeeListRequest employeeListRequest) {

        log.info(StringEscapeUtils.escapeJava("Called GET companies/" + companyId + "/employees/bulk with session-id: " + sessionId));

        ByteArrayOutputStream employeeListOutputStream =  cdhBulkEmployeeService
            .bulkGetEmployees(authorization, companyId);
        return prepareResponse(employeeListOutputStream);

    }

    @Operation(summary = "bulkAddEmployees", description = "Bulk add employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created."),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Error Occurred: requested entity not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/{company-id}/employees/bulk",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> bulkAddEmployees(
            @Parameter @RequestHeader(value = "session-id", required = false) String sessionId,
            @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
            @Parameter @RequestHeader(name = "country", required = false, defaultValue = "gb") String countryCode,
            @Parameter @RequestHeader(name = "language", required = false, defaultValue = "en") String languageCode,
            @Parameter(required = true) @PathVariable("company-id") String companyId,
            @Parameter @RequestHeader(name = "bookingChannel", required = false, defaultValue = "CBT") BookingChannel bookingChannel,
            @Parameter(required = true, name = "upFile", description = "The Employees Excel file payload") @RequestPart("upFile") MultipartFile upFile,
            @Parameter(name = "innBusiness", description = "Indicates if the request comes from InnBusiness", required = false)
            @RequestParam(name = "innBusiness", required = false, defaultValue = "false") boolean innBusiness) {

        log.info(StringEscapeUtils.escapeJava("Called POST companies/" + companyId + "/employees/bulk with session-id: " + sessionId));

        cdhBulkEmployeeService
            .bulkAddEmployees(authorization, companyId, upFile, languageCode, innBusiness);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private static ResponseEntity<byte[]> prepareResponse(
        ByteArrayOutputStream employeeListOutputStream) {
      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.CONTENT_DISPOSITION, EMPLOYEES_CSV_CONTENT_DISPOSITION);
      return ResponseEntity.ok().headers(headers).body(employeeListOutputStream.toByteArray());
    }
}
