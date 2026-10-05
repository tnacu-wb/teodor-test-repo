package uk.co.whitbread.content.infrastructure.rest.client.snowdrop;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.co.whitbread.content.domain.model.ErrorCode.SNOWDROP_HOTEL_SEARCH_EXCEPTION;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.content.domain.model.dlp.in.DlpInformationRequest;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.SnowdropResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.CoordinatesDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.DlpInformationDto;
import uk.co.whitbread.content.infrastructure.rest.client.aem.model.dlp.out.HotelDto;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.AemClient;
import uk.co.whitbread.content.infrastructure.rest.client.content.mapper.LabelsRequestMapper;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.adapter.SnowdropClient;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.in.HotelLocationRequestDto;
import uk.co.whitbread.content.infrastructure.rest.client.snowdrop.model.out.HotelLocationResponseDto;

class SnowdropHotelsRetrieverTest {

    @Mock
    private SnowdropClient snowdropClient;

    @Mock
    private AemClient aemClient;

    @Mock
    private LabelsRequestMapper labelsRequestMapper;

    @InjectMocks
    private SnowdropHotelsRetriever snowdropHotelsRetriever;

    private static final String LONEUS_CODE = "LONEUS";

    private final Exception exception = new Exception();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void mapHotelsFromSnowdrop_ShouldReturnMappedHotels_WhenResponseIsValid() {
        DlpInformationRequest request = mock(DlpInformationRequest.class);
        DlpInformationDto dto = mock(DlpInformationDto.class);
        HotelLocationResponseDto responseDto = new HotelLocationResponseDto();
        responseDto.setCode(LONEUS_CODE);

        when(snowdropClient.getHotelsLocationByLatLong(any(HotelLocationRequestDto.class)))
                .thenReturn(List.of(responseDto));
        when(dto.getMap()).thenReturn(CoordinatesDto.builder().latitude(51.5074)
                .longitude(-0.1278).radius("30").hideHotelDistance(false).build());

        List<HotelDto> result = snowdropHotelsRetriever.mapHotelsFromSnowdrop(request, dto);

        assertEquals(1, result.size());
        assertEquals(LONEUS_CODE, result.get(0).getCode());
    }

    @Test
    void mapHotelsFromSnowdrop_ShouldReturnEmptyList_WhenResponseIsNull() {
        DlpInformationRequest request = mock(DlpInformationRequest.class);
        DlpInformationDto dto = mock(DlpInformationDto.class);

        when(snowdropClient.getHotelsLocationByLatLong(any(HotelLocationRequestDto.class)))
                .thenReturn(null);

        List<HotelDto> result = snowdropHotelsRetriever.mapHotelsFromSnowdrop(request, dto);

        assertEquals(0, result.size());
    }

    @Test
    void retrieveHotelsFromSnowdrop_ShouldReturnHotels_WhenMapDataIsValid() {
        DlpInformationRequest request = mock(DlpInformationRequest.class);
        DlpInformationDto dto = mock(DlpInformationDto.class);
        when(request.getLanguage()).thenReturn("en");
        when(dto.getMap()).thenReturn(CoordinatesDto.builder().latitude(51.5074)
                        .longitude(-0.1278).radius("30").hideHotelDistance(false).build());

        when(aemClient.getLabels(any())).thenReturn(Map.of(
                SnowdropHotelsRetriever.DLP_RADIUS_UNITS, "mi",
                SnowdropHotelsRetriever.DLP_RADIUS, "30"
        ));

        HotelLocationResponseDto responseDto = new HotelLocationResponseDto();
        responseDto.setCode(LONEUS_CODE);

        when(snowdropClient.getHotelsLocationByLatLong(any(HotelLocationRequestDto.class)))
                .thenReturn(List.of(responseDto));

        List<HotelLocationResponseDto> result = snowdropHotelsRetriever.retrieveHotelsFromSnowdrop(request, dto);

        assertEquals(1, result.size());
        assertEquals(LONEUS_CODE, result.get(0).getCode());
    }

    @Test
    void retrieveHotelsFromSnowdrop_ShouldThrowException_WhenSnowdropClientFails() {
        String expectedMessage = "Resource not found in SnowDrop";
        DlpInformationRequest request = mock(DlpInformationRequest.class);
        DlpInformationDto dto = mock(DlpInformationDto.class);
        when(dto.getMap()).thenReturn(CoordinatesDto.builder().latitude(51.5074)
                .longitude(-0.1278).build());

        when(snowdropClient.getHotelsLocationByLatLong(any(HotelLocationRequestDto.class)))
                .thenThrow(new SnowdropResponseException(SNOWDROP_HOTEL_SEARCH_EXCEPTION,
                       expectedMessage, exception));
        //Act
        SnowdropResponseException actual =
                assertThrows(SnowdropResponseException.class, () ->
                        snowdropHotelsRetriever.retrieveHotelsFromSnowdrop(request, dto));
        //Assert
        assertEquals(expectedMessage, actual.getMessage());
        assertThat(actual, notNullValue());
    }

    @Test
    void retrieveHotelsFromSnowdrop_ShouldReturnEmptyList_WhenMapDataIsNull() {
        DlpInformationRequest request = mock(DlpInformationRequest.class);
        DlpInformationDto dto = mock(DlpInformationDto.class);
        when(dto.getMap()).thenReturn(null);

        List<HotelLocationResponseDto> result = snowdropHotelsRetriever.retrieveHotelsFromSnowdrop(request, dto);

        assertEquals(0, result.size());
    }

    @Test
    void retrieveHotelsFromSnowdrop_ShouldReturnEmptyList_WhenLatitudeOrLongitudeIsNull() {
        DlpInformationRequest request = mock(DlpInformationRequest.class);
        DlpInformationDto dto = mock(DlpInformationDto.class);
        when(dto.getMap()).thenReturn(CoordinatesDto.builder().latitude(null)
                .longitude(-0.1278).radius(null).hideHotelDistance(false).build());

        List<HotelLocationResponseDto> result = snowdropHotelsRetriever.retrieveHotelsFromSnowdrop(request, dto);

        assertEquals(0, result.size());
    }
}