package uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data;

import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.content.domain.model.index.header.data.in.LocalizationRequest;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Api;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Config;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Content;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Facilities;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Filter;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Label;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Result;
import uk.co.whitbread.content.domain.model.searchresults.data.out.Results;
import uk.co.whitbread.content.domain.model.searchresults.data.out.SearchResultsData;
import uk.co.whitbread.content.domain.ports.primary.ContentInPort;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.mapper.LocalizationRequestDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.header.data.model.out.BrandDto;
import uk.co.whitbread.content.infrastructure.rest.controller.model.in.LocalizationDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.mapper.SearchResultsDataDtoMapper;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.ApiDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.ConfigDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.ContentDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.FacilitiesDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.FilterDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.LabelDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.ResultDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.ResultsDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.SearchResultsDataDto;
import uk.co.whitbread.content.infrastructure.rest.controller.searchresults.data.model.out.SrpGlobalDto;


@ExtendWith(MockitoExtension.class)
class SearchResultsDataControllerTest {

  @InjectMocks
  SearchResultsDataController searchResultsDataControllerUnderTest;

  @Mock
  private ContentInPort contentInPort;

  @Mock
  private LocalizationRequestDtoMapper localizationRequestDtoMapper;

  @Mock
  private SearchResultsDataDtoMapper searchResultsDataDtoMapper;


  @Test
  void getSearchResultsData__ShouldReturnOK() {
    //Arrange
    LocalizationRequest localizationRequest = LocalizationRequest.builder().country("gb")
        .language("en").build();
    LocalizationDto localizationDto = LocalizationDto.builder().country("gb").language("en")
        .build();

    Mockito.when(localizationRequestDtoMapper.toDomainModel(localizationDto))
        .thenReturn(localizationRequest);
    Mockito.when(contentInPort.getSearchResultsData(localizationRequest))
        .thenReturn(getSearchResultsData());
    Mockito.when(searchResultsDataDtoMapper.toDtoModel(getSearchResultsData()))
        .thenReturn(getSearchResultsDataDto());

    //act
    final var request = localizationRequestDtoMapper.toDomainModel(localizationDto);
    final var searchResultsData = contentInPort.getSearchResultsData(request);
    final var domainContentRequest = searchResultsDataDtoMapper.toDtoModel(searchResultsData);
    final ResponseEntity<SearchResultsDataDto> response =
        searchResultsDataControllerUnderTest.getSearchResultsData(localizationDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFullyBooked(),
        response.getBody().getContent().getResults().getResult().getFullyBooked());


    assertEquals(response.toString(),
        domainContentRequest.getContent().getResults().getResult().getAvailabilityWarning(),
        response.getBody().getContent().getResults().getResult().getAvailabilityWarning());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getOpeningSoon(),
        response.getBody().getContent().getResults().getResult().getOpeningSoon());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getOpeningOn(),
        response.getBody().getContent().getResults().getResult().getOpeningOn());

    assertEquals(response.toString(),
        domainContentRequest.getContent().getResults().getResult().getDistanceUnitPlural(),
        response.getBody().getContent().getResults().getResult().getDistanceUnitPlural());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFromLocation(),
        response.getBody().getContent().getResults().getResult().getFromLocation());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getPriceFrom(),
        response.getBody().getContent().getResults().getResult().getPriceFrom());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getViewDetails(),
        response.getBody().getContent().getResults().getResult().getViewDetails());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFacilities()
            .getPremierPlusRoom(),
        response.getBody().getContent().getResults().getResult().getFacilities()
            .getPremierPlusRoom());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFacilities()
            .getStandardExtraRoom(),
        response.getBody().getContent().getResults().getResult().getFacilities()
            .getStandardExtraRoom());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFacilities()
            .getBusinessRoom(),
        response.getBody().getContent().getResults().getResult().getFacilities()
            .getBusinessRoom());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFacilities()
            .getParking(),
        response.getBody().getContent().getResults().getResult().getFacilities()
            .getParking());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFacilities()
            .getNoParking(),
        response.getBody().getContent().getResults().getResult().getFacilities()
            .getNoParking());

    assertEquals(response.toString(), domainContentRequest.getContent().getResults().getResult().getFacilities()
            .getFreeParking(),
        response.getBody().getContent().getResults().getResult().getFacilities()
            .getFreeParking());

    assertEquals(response.toString(), domainContentRequest.getContent().getFilter().getLabel().getFreeParking(),
        response.getBody().getContent().getFilter().getLabel().getFreeParking());

    assertEquals(response.toString(), domainContentRequest.getContent().getFilter().getLabel().getParking(),
        response.getBody().getContent().getFilter().getLabel().getParking());

    assertEquals(response.toString(), domainContentRequest.getContent().getTotalHotels(),
        response.getBody().getContent().getTotalHotels());

    assertEquals(response.toString(), domainContentRequest.getContent().getGlobal().getBrand().getHubBadge(),
        response.getBody().getContent().getGlobal().getBrand().getHubBadge());
  }

  private SearchResultsData getSearchResultsData() {
    return new SearchResultsData(getContent(), getConfig());
  }

  private Content getContent() {
    return Content.builder().results(getResults())
        .filter(getFilter())
        .totalHotels("Hotels found")
        .build();
  }

  private Filter getFilter() {
    return Filter.builder()
        .label(getLabel())
        .build();
  }

  private Label getLabel() {

    return Label.builder()
        .freeParking("Free parking")
        .parking("Parking")
        .build();
  }

  private Results getResults() {
    return Results.builder().result(getResult()).build();
  }

  private Result getResult() {
    return Result.builder()
        .fullyBooked("Sold out")
        .availabilityWarning("Last few rooms")
        .openingSoon("Open soon")
        .openingOn("Opening on")
        .distanceUnitPlural("miles")
        .fromLocation("from tour search")
        .priceFrom("From")
        .viewDetails("View details")
        .facilities(getFacilities())
        .build();
  }

  private Config getConfig() {
    return Config.builder().api(getApi()).build();
  }

  private Api getApi() {
    return Api.builder().initialPageSize("40")
        .radius("50")
        .lazyLoadPageSize("10")
        .build();
  }

  private Facilities getFacilities() {
    return Facilities.builder()
        .premierPlusRoom("Premier Plus")
        .standardExtraRoom("Premier Plus rooms")
        .businessRoom("Standard Extra rooms")
        .parking("Parking")
        .noParking("No parking available")
        .freeParking("Free parking")
        .build();
  }

  private SearchResultsDataDto getSearchResultsDataDto() {
    return new SearchResultsDataDto(getContentDto(), getConfigDto());
  }

  private ContentDto getContentDto() {
    return ContentDto.builder().results(getResultsDto())
        .filter(getFilterDto())
        .global(getSrpGlobalDto())
        .totalHotels("Hotels found")
        .build();
  }

  private SrpGlobalDto getSrpGlobalDto() {
    return SrpGlobalDto.builder().brand(getBrandDto()).build();
  }

  private BrandDto getBrandDto() {
    return BrandDto.builder()
        .hubBadge("test")
        .build();
  }

  private FilterDto getFilterDto() {
    return FilterDto.builder()
        .label(getLabelDto())
        .build();
  }

  private LabelDto getLabelDto() {
    return LabelDto.builder()
        .parking("Parking")
        .freeParking("Free parking")
        .build();
  }

  private ResultsDto getResultsDto() {
    return ResultsDto.builder()
        .result(getResultDto())
        .build();
  }

  private ResultDto getResultDto() {
    return ResultDto.builder()
        .fullyBooked("Sold out")
        .availabilityWarning("Last few rooms")
        .openingSoon("Open soon")
        .openingOn("Opening on")
        .distanceUnitPlural("miles")
        .fromLocation("From")
        .viewDetails("View details")
        .facilities(getFacilitiesDto())
        .build();
  }

  private ConfigDto getConfigDto() {
    return ConfigDto.builder().api(getApiDto()).build();
  }

  private ApiDto getApiDto() {
    return ApiDto.builder().initialPageSize("40")
        .radius("50")
        .lazyLoadPageSize("10")
        .build();
  }

  private FacilitiesDto getFacilitiesDto() {
    return FacilitiesDto.builder()
        .premierPlusRoom("Premier Plus")
        .standardExtraRoom("Premier Plus rooms")
        .businessRoom("Standard Extra rooms")
        .parking("Parking")
        .noParking("No parking available")
        .freeParking("Free parking")
        .build();
  }
}