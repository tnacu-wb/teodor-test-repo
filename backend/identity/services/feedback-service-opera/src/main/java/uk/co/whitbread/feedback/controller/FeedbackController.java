package uk.co.whitbread.feedback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.feedback.model.Feedback;
import uk.co.whitbread.feedback.model.FeedbackResponse;
import uk.co.whitbread.feedback.service.FeedbackService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Autowired
    FeedbackService feedbackService;

    @Operation(summary = "Add Feedback", description = "Adds customer feedback")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                content = @Content(schema = @Schema(implementation = FeedbackResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred ",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
  @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public FeedbackResponse addFeedback(@Valid @RequestBody Feedback payload) {
        log.info("Called /feedback (POST -  Add Feedback)");
        return feedbackService.addFeedback(payload);
    }
}
