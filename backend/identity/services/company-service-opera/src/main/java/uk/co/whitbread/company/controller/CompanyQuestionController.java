package uk.co.whitbread.company.controller;

import static uk.co.whitbread.company.utils.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.company.model.AnsweredQuestions;
import uk.co.whitbread.company.model.BusinessQuestions;
import uk.co.whitbread.company.model.ManagementInformationQuestion;
import uk.co.whitbread.company.service.cdh.CdhCompanyQuestionService;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/companies/{companyId}")
@Tag(name = "Business and user created questions")
public class CompanyQuestionController {

  private static final String DUMMY_EMAIL = "dummy@email.com";

  private  static  final String EMPTY_TOKEN = "Authorization token is empty!";
  private  static  final String INVALID_TOKEN = "Authorization token is invalid! " +
          "companyAccountId and/or employeeAccountId are missing";

  private final CdhCompanyQuestionService cdhCompanyQuestionService;
  private final TokenService authTokenService;

  @Operation(summary = "Create a user question", description = "Create a user question for a specific company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", headers =
      @Header(name = "Location", schema = @Schema(implementation = URI.class), description = "Unique identifier for the question created.")),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "403", description = "Error Occurred: You hit the maximum number of questions",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.PATH, name = "companyId", description = "Company Id",
      required = true, content = @Content(schema = @Schema(type = "string", defaultValue = "8")))
  @Parameter(in = ParameterIn.DEFAULT, name = "payload", description = "Payload",
      required = true, content = @Content(schema = @Schema(type = "object")))
  @PostMapping(value = "admin/employee-questions",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Void> createCompanyUserQuestion(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId,
    @Parameter(required = true, name = "payload", description = "The question JSON payload") @Valid @RequestBody ManagementInformationQuestion userQuestion) {
    String sanitizedCompanyId = companyId.replaceAll("\\W", "");
    log.info(
        "Called POST /companies/{}/admin/employee-questions to create a new user question",
        sanitizedCompanyId);

      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      String userEmail = cdhEmployeeDetails.getUserEmail();
      return ResponseEntity.created(
              ServletUriComponentsBuilder
                  .fromCurrentRequest()
                  .path("/{id}")
                  .buildAndExpand(cdhCompanyQuestionService.postQuestion(companyId,
                      userQuestion, userEmail))
                  .toUri())
          .build();
  }

  @Operation(summary = "Retrieve all user questions", description = "Get all user created questions for a specific company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All good"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.PATH, name = "companyId", description = "Company Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "8")))
  @GetMapping(value = "/employee-questions", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<List<ManagementInformationQuestion>> getCompanyUserQuestions(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId) {

    if (StringUtils.isNotBlank(authorization)) {
      log.info(
          "Called GET /companies/{}/employee-questions with token to retrieve all user questions",
          sanitizeInputString(companyId));
      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      if (StringUtils.isBlank(cdhEmployeeDetails.getCompanyAccountId())
          || StringUtils.isBlank(cdhEmployeeDetails.getEmployeeAccountId())) {
        throw new TokenVerificationException(INVALID_TOKEN);
      }
      String userEmail = cdhEmployeeDetails.getUserEmail();
      return ResponseEntity.ok(
          cdhCompanyQuestionService.getUserCreatedQuestions(companyId, userEmail));
    }
    throw new TokenVerificationException(EMPTY_TOKEN);
  }

  @Operation(summary = "Delete a user question", description = "Remove a user question for a specific company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "No content. Operation succeeded."),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "404", description = "Error Occurred: Question not found", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.HEADER, name = "session-id", description = "Required for authorization",
      content = @Content(schema = @Schema(type = "string", defaultValue = "BCQR98267")))
  @Parameter(in = ParameterIn.PATH, name = "companyId", description = "Company Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "8")))
  @Parameter(in = ParameterIn.PATH, name = "question-id", description = "Question Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "1")))
  @DeleteMapping(value = "admin/employee-questions/{question-id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> removeUserQuestion(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @PathVariable("companyId") String companyId,
      @PathVariable("question-id") String questionId) {
    companyId = companyId.replace('\n', '_').replace('\r', '_');
    questionId = questionId.replace('\n', '_').replace('\r', '_');
    log.info(
        "Called Delete /companies/{}/admin/employee-questions/{} to retrieve all user questions",
        companyId, questionId);

      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      String userEmail = cdhEmployeeDetails.getUserEmail();
      cdhCompanyQuestionService.deleteQuestion(companyId, questionId, userEmail);
      return ResponseEntity.noContent().build();

  }

  @Operation(summary = "Delete a user question", description = "Remove a user question for a specific company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "No content. Operation succeeded."),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "404", description = "Error Occurred: Question not found", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Required for authorization",
      content = @Content(schema = @Schema(type = "string")))
  @Parameter(in = ParameterIn.PATH, name = "companyId", description = "Company Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "8")))
  @Parameter(in = ParameterIn.PATH, name = "question-id", description = "Question Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "1")))
  @PostMapping(value = "admin/employee-questions/{question-id}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> deleteUserQuestion(
      @Parameter @RequestHeader(value = "Authorization") String authorization,
      @PathVariable("companyId") String companyId,
      @PathVariable("question-id") String questionId) {
    companyId = companyId.replace('\n', '_').replace('\r', '_');
    questionId = questionId.replace('\n', '_').replace('\r', '_');
    log.info(
        "Called Delete /companies/{}/admin/employee-questions/{} to delete the user question",
        companyId, questionId);

    CdhEmployeeDetails cdhEmployeeDetails =
        authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
    String userEmail = cdhEmployeeDetails.getUserEmail();
    cdhCompanyQuestionService.deleteQuestion(companyId, questionId, userEmail);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Update a specific user question", description = "Edit a specific user question for a specific company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "All good. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Required for authorization",
      content = @Content(schema = @Schema(type = "string")))
  @Parameter(in = ParameterIn.PATH, name = "companyId", description = "Company Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "8")))
  @Parameter(in = ParameterIn.PATH, name = "question-id", description = "Question Id",
      content = @Content(schema = @Schema(type = "string", defaultValue = "1")))
  @Parameter(in = ParameterIn.DEFAULT, name = "payload", description = "Payload",
      content = @Content(schema = @Schema(type = "object")))
  @PutMapping(value = "admin/employee-questions/{question-id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updateCompanyUserQuestion(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @Parameter(required = true) @PathVariable("companyId") String companyId,
      @Parameter(required = true) @PathVariable("question-id") String questionId,
      @Parameter(required = true, name = "payload", description = "The question JSON payload") @Valid @RequestBody ManagementInformationQuestion userQuestion) {
    String sanitizedCompanyId = companyId.replace('\n', '_').replace('\r', '_');
    String sanitizedQuestionId = questionId.replace('\n', '_').replace('\r', '_');
    log.info(
        "Called PUT /companies/{}/admin/employee-questions/{} to update the user created question",
        sanitizedCompanyId, sanitizedQuestionId);

      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      String userEmail = cdhEmployeeDetails.getUserEmail();
      cdhCompanyQuestionService.updateQuestion(companyId, questionId, userQuestion, userEmail);
      return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Retrieve business questions", description = "Get the purchase order and customer reference questions")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All good"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(value = "/business-questions", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<BusinessQuestions> retrieveBusinessQuestions(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @Parameter(required = true) @PathVariable("companyId") String companyId) {

    if (StringUtils.isNotBlank(authorization)) {
      log.info(
          "Called GET /companies/{}/business-questions with token to retrieve the business questions",
          sanitizeInputString(companyId));
      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      if (StringUtils.isBlank(cdhEmployeeDetails.getCompanyAccountId())
          || StringUtils.isBlank(cdhEmployeeDetails.getEmployeeAccountId())) {

        throw new TokenVerificationException(INVALID_TOKEN);
      }
      String userEmail = cdhEmployeeDetails.getUserEmail();
      return ResponseEntity.ok(
          cdhCompanyQuestionService.getBusinessQuestions(companyId, userEmail));
    }
    throw new TokenVerificationException(EMPTY_TOKEN);
  }

  @Operation(summary = "updateCompanyBusinessQuestion", description = "Update a specific business created question")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "All good. No content"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PutMapping(value = "admin/business-questions/{question-id}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> updateCompanyBusinessQuestion(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @Parameter(required = true) @PathVariable("companyId") String companyId,
      @Parameter(required = true) @PathVariable("question-id") String questionId,
      @Parameter(required = true, name = "payload", description = "The question JSON payload") @Valid @RequestBody ManagementInformationQuestion userQuestion) {
    String sanitizedCompanyId = companyId.replaceAll("\\W", "");
    String sanitizedQuestionId = questionId.replaceAll("\\W", "");
    log.info(
        "Called PUT /companies/{}/admin/business-questions/{} to update the business question",
        sanitizedCompanyId, sanitizedQuestionId);

      CdhEmployeeDetails cdhEmployeeDetails = authTokenService
          .retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      String userEmail = cdhEmployeeDetails.getUserEmail();
      cdhCompanyQuestionService
          .updateBusinessQuestions(companyId, questionId, userQuestion, userEmail);
      return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Retrieve registration questions and answers")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All good"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.HEADER, name = "employee-id", description = "Required when call is made from the activation link",
          content = @Content(schema = @Schema(type = "string", defaultValue = "28")))
  @GetMapping(value = "/registration-questions", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<AnsweredQuestions> retrieveAnsweredQuestions(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @Parameter(required = true) @PathVariable("companyId") String companyId,
      @RequestHeader(name = "employee-id", required = false) String employeeId) {

    if (StringUtils.isNotBlank(authorization)) {
      log.info(
          "Called GET /companies/{}/registration-questions with token to retrieve questions and answers",
          sanitizeInputString(companyId));
      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      if (StringUtils.isBlank(cdhEmployeeDetails.getCompanyAccountId())
          || StringUtils.isBlank(cdhEmployeeDetails.getEmployeeAccountId())) {

        throw new TokenVerificationException(INVALID_TOKEN);
      }
      return ResponseEntity.ok(
          cdhCompanyQuestionService.getAnsweredQuestions(companyId, cdhEmployeeDetails));
    }
    //the call is made from the activation link
    log.info(
            "Called GET /companies/{}/registration-questions from the activation link",
            sanitizeInputString(companyId));
    return ResponseEntity.ok(cdhCompanyQuestionService.getAnsweredQuestions(companyId,
            CdhEmployeeDetails.builder()
                    .employeeAccountId(employeeId)
                    .companyAccountId(companyId)
                    .userEmail(DUMMY_EMAIL)
                    .build()));
  }

  @Operation(summary =
      "Only travel managers are authorised. Travel managers can retrieve registration questions and answers from employees, where employee-id "
          +
          "in the header refers to travel manager's employee id and employeeId in the path is the employee's he wants to retrieve for."
          +
          "If employee-id in the header and in the path are the same it will return travel manager's registration questions.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "All good"),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized"),
      @ApiResponse(responseCode = "403", description = "Bad Request"),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @Parameter(in = ParameterIn.HEADER, name = "company-id", description = "Required for authorization",
      content = @Content(schema = @Schema(type = "string", defaultValue = "22")))
  @Parameter(in = ParameterIn.HEADER, name = "employee-id", description = "Required for authorization",
      content = @Content(schema = @Schema(type = "string", defaultValue = "28")))
  @GetMapping(value = "/employees/{employeeId}/registration-questions",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<AnsweredQuestions> retrieveAnsweredQuestionsForAnotherEmployeeId(
      @Parameter @RequestHeader(value = "Authorization", required = false) String authorization,
      @Parameter(required = true) @PathVariable("companyId") String companyId,
      @Parameter(required = true) @PathVariable("employeeId") String employeeId) {

    if (StringUtils.isNotBlank(authorization)) {
      log.info(
          "Called GET /companies/{}/employees/{}/registration-questions with token to retrieve questions and answers",
          sanitizeInputString(companyId), sanitizeInputString(employeeId));
      CdhEmployeeDetails cdhEmployeeDetails =
          authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
      if (StringUtils.isBlank(cdhEmployeeDetails.getCompanyAccountId())
          || StringUtils.isBlank(cdhEmployeeDetails.getEmployeeAccountId())) {

        throw new TokenVerificationException(INVALID_TOKEN);
      }
      return ResponseEntity.ok(
          cdhCompanyQuestionService.getEmployeeAnswersForCompanyQuestions(companyId,
              employeeId, cdhEmployeeDetails.getUserEmail()));
    }
    throw new TokenVerificationException(EMPTY_TOKEN);
  }

}
