package uk.co.whitbread.hotel.info.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.info.converters.CaseInsensitiveEnumPropertyEditor;
import uk.co.whitbread.hotel.info.errors.SmartBindingErrorProcessor;
import uk.co.whitbread.hotel.info.model.domain.*;
import uk.co.whitbread.hotel.info.service.HotelInfoService;
import uk.co.whitbread.hotel.info.service.HotelKeyService;

import java.util.List;
import java.util.stream.Stream;


@Slf4j
@Tag(name = "Hotel Info Controller")
@RestController
@RequiredArgsConstructor
@ApiResponses(value = {
        @ApiResponse(responseCode = "400", description = "Error Occurred ",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                        implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal Server Error",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                        implementation = ErrorResponse.class)))})
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HotelInfoController {

    private final HotelInfoService hotelInfoService;
    private final HotelKeyService hotelKeyService;

    @Operation(method = "getHotelInfo")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                    implementation = String.class)))
    @GetMapping(value = "/hotels/{hotel-code}")
    public String getHotelInfo(@PathVariable(value = "hotel-code") String hotelCode, @ModelAttribute HotelInfoRequest request) {
        log.debug("Called /hotels/{} with {}", hotelCode, request);

        return hotelInfoService.getHotelInfo(hotelCode, request.getCountry(), request.getLanguage(), request.getFormat());
    }

    @Operation(method = "getMultipleHotelInfo")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                    implementation = MultiHotelInfoResponse.class)))
    @GetMapping(value = "/hotels")
    public MultiHotelInfoResponse getHotelInfo(@Valid @ModelAttribute MultiHotelInfoRequest request) {
        log.debug("Called /hotels with {}", request);

        List<String> hotelCodes = request.getHotelCodes();
        boolean emptyHotelCodes = CollectionUtils.isEmpty(hotelCodes);

        List<String> hotels;

        if (emptyHotelCodes) {

            hotels = hotelInfoService.getAllHotels(request.getCountry(), request.getLanguage());
        } else {

            hotels = hotelInfoService.getHotelInfo(request.getHotelCodes(), request.getCountry(), request.getLanguage(), request.getFormat());
        }

        return MultiHotelInfoResponse
                .builder()
                .hotels(hotels)
                .total(hotels.size())
                .build();
    }

    @Operation(method = "getHotelKeyData")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                    implementation = HotelKeyData.class)))
    @GetMapping(value = "/hotels/{hotel-code}/key")
    public HotelKeyData getHotelKeyData(@PathVariable("hotel-code") String hotelCode, @ModelAttribute HotelKeyDataRequest request) {
        log.debug("Called /hotels/{}/key with {}", hotelCode, request);

        return hotelKeyService.getHotelKeyData(request.getLanguage(), hotelCode);
    }

    @Operation(method = "getMultiHotelKeyData")
    @ApiResponse(responseCode = "200", description = "Success",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(
                    implementation = MultiHotelKeyDataResponse.class)))
    @GetMapping(value = "/hotels/key")
    public MultiHotelKeyDataResponse getHotelKeyData(@Valid @ModelAttribute MultiHotelKeyDataRequest request) {
        log.debug("Called /hotels/key with {}", request);

        return MultiHotelKeyDataResponse
                .builder()
                .keyData(hotelKeyService.getHotelKeyData(request.getLanguage(), request.getHotelCodes()))
                .build();
    }

    @InitBinder
    public void setUpPropertyBinderExtras(WebDataBinder binder) {
        Stream.of(Country.class, Language.class, HotelInfoFormat.class)
                .forEach(clazz -> binder.registerCustomEditor(clazz, new CaseInsensitiveEnumPropertyEditor<>(clazz)));
        binder.setBindingErrorProcessor(new SmartBindingErrorProcessor());
    }
}
