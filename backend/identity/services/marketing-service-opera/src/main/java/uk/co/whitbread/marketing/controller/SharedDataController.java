package uk.co.whitbread.marketing.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.marketing.model.RegionsResponse;
import uk.co.whitbread.marketing.service.SharedDataService;

@Slf4j
@RequestMapping("/marketing/hotels")
@RestController
@Tag(name = "Marketing API Controller")
@Deprecated
public class SharedDataController {

    private SharedDataService sharedDataService;

    @Autowired
    public SharedDataController(SharedDataService sharedDataService) {
        this.sharedDataService = sharedDataService;
    }


    @Operation(summary = "Gets all regions", description = "Returns all regions hotels are active in.", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All good", content = @Content(schema = @Schema(implementation = RegionsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @RequestMapping(value = "/regions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public RegionsResponse getRegions() {
        log.info("Sending get regions request");
        return sharedDataService.getRegions();
    }
}
