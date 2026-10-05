package uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.ports.primary.DashboardInPort;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.mapper.DashboardElementMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.mapper.RetrieveDashboardRequestMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.in.RetrieveDashboardRequestDto;
import uk.co.whitbread.dashboard.infrastructure.rest.controller.dashboard.model.out.DashboardElementDto;

@RequestMapping
@RestController
@Slf4j
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardInPort dashboardInPort;
  private final RetrieveDashboardRequestMapper retrieveDashboardRequestMapper;
  private final DashboardElementMapper dashboardElementMapper;


  @Operation(method = "Generate Dashboard", description = "Generates "
      + "a dashboard to be used by the mobile apps. This version supports "
      + "logged users and it returns frequent bookings.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              array = @ArraySchema(schema = @Schema(implementation = DashboardElement.class)))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred ",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Internal Server Error",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(value = "/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
  public List<DashboardElementDto> retrieveDashboard(
      @RequestHeader(name = "Authorization", required = false) final String authorization,
      @RequestHeader(name = "session-id", required = false) final String sessionId,
      @RequestHeader(name = "Origin", required = false) final String origin,
      @RequestParam(name = "hasRecentSearches", required = false, defaultValue = "true") final boolean recentSearches,
      @RequestParam(name = "customer-id", required = false) final String customerId,
      @Valid final RetrieveDashboardRequestDto request) {
    log.info("Called /dashboard for booking {}", request.getConfirmationNumber());

    final RetrieveDashboardRequest retrieveDashboardRequest = retrieveDashboardRequestMapper.toModel(
        request);
    final List<DashboardElement> dashboardElements = dashboardInPort.retrieveDashboard(
        retrieveDashboardRequest,
        authorization, sessionId, recentSearches, origin, customerId);
    return dashboardElements.stream().map(dashboardElementMapper::toDto).toList();
  }
}
