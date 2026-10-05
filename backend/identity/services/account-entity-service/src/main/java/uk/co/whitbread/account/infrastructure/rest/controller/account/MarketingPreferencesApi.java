package uk.co.whitbread.account.infrastructure.rest.controller.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.UpdatePreferencesRequestDto;

public interface MarketingPreferencesApi {
  @RouterOperations({@RouterOperation(method = RequestMethod.PUT,
      operation = @Operation(operationId = "updateMarketingPreferences",
          summary =
              "Updates marketing newsletter preferences",
          responses = {
              @ApiResponse(responseCode = "204", description = "Marketing preferences updated successfully"),
              @ApiResponse(responseCode = "400",
                  description = "Incorrect details supplied to update request"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<Void> updateMarketingPreferences(
      @Valid @RequestBody UpdatePreferencesRequestDto updatePreferencesRequestDto);
}
