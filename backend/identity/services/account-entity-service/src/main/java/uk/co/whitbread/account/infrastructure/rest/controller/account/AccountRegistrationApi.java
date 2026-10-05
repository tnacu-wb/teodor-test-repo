package uk.co.whitbread.account.infrastructure.rest.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.AccountRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.out.AccountRegistrationResponseDto;

public interface AccountRegistrationApi {
  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      operation = @Operation(operationId = "createAccount",
          summary =
              "Registers a new account with details received in the request",
          responses = {
              @ApiResponse(responseCode = "201", description = "Account created successfully"),
              @ApiResponse(responseCode = "400",
                  description = "Incorrect details supplied as registration request"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<AccountRegistrationResponseDto> createAccount(
      @Valid @RequestBody AccountRegistrationRequestDto request);
}
