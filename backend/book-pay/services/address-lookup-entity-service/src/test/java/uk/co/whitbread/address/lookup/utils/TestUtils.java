package uk.co.whitbread.address.lookup.utils;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Arrays;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.address.lookup.domain.model.in.AddressSearchRequest;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.in.AddressSearchRequestDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressFormatResponseDto;
import uk.co.whitbread.address.lookup.infrastructure.rest.controller.address.model.out.AddressSearchResponseDto;
import uk.co.whitbread.qas.addresslookup.api.Address;
import uk.co.whitbread.qas.addresslookup.api.AddressLineType;
import uk.co.whitbread.qas.addresslookup.api.EngineEnumType;
import uk.co.whitbread.qas.addresslookup.api.EngineType;
import uk.co.whitbread.qas.addresslookup.api.PicklistEntryType;
import uk.co.whitbread.qas.addresslookup.api.PromptSetType;
import uk.co.whitbread.qas.addresslookup.api.QAAddressType;
import uk.co.whitbread.qas.addresslookup.api.QAGetAddress;
import uk.co.whitbread.qas.addresslookup.api.QAPicklistType;
import uk.co.whitbread.qas.addresslookup.api.QASearch;
import uk.co.whitbread.qas.addresslookup.api.QASearchResult;

public class TestUtils {

  private static final String MOCK_POSTCODE = "ABC";
  private static final String MOCK_COUNTRY = "GBX";
  private static final String MOCK_MONIKER = "moniker123";

  public static void checkErrorThrown(Executable executable, String[] errors) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    Arrays.stream(errors)
        .forEach(error -> Assertions.assertTrue(thrownException.getMessage().contains(error)));
  }

  public static AddressFormatResponse mockAddressFormatResponse() {
    return AddressFormatResponse.builder()
        .addressLine1("120 Holborn")
        .addressLine4("LONDON")
        .postalCode("EC1N 2TD")
        .country("GB")
        .build();
  }

  public static AddressFormatResponseDto mockAddressFormatResponseDto() {
    return AddressFormatResponseDto.builder()
        .addressLine1("120 Holborn")
        .addressLine4("LONDON")
        .postalCode("EC1N 2TD")
        .country("GB")
        .build();
  }

  public static List<AddressSearchResponse> mockAddressSearchResponse() {
    return List.of(AddressSearchResponse.builder()
        .monikerId(
            "GBX|0f670cfa-f262-4cad-b168-f91f80633833|7.730MOGBXDwfmBwAAAAABAwEAAAABouxHkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8")
        .address("Money Pensions Service, 120 Holborn, LONDON, EC1N 2TD")
        .build());
  }

  public static List<AddressSearchResponseDto> mockAddressSearchResponseDto() {
    return List.of(AddressSearchResponseDto.builder()
        .id(
            "GBX|0f670cfa-f262-4cad-b168-f91f80633833|7.730MOGBXDwfmBwAAAAABAwEAAAABouxHkgAhEAIQACAAAAAAAAAAAP..ZAAAAAD.....AAAAAAAAAAAAAAAAAAAARUMxTiAyVEQAAAAAAA--$8")
        .addressText("Money Pensions Service, 120 Holborn, LONDON, EC1N 2TD")
        .build());
  }

  public static AddressSearchRequestDto mockAddressSearchRequestDto() {
    return AddressSearchRequestDto
        .builder().searchTerm("EC1N 2TD").build();
  }

  public static AddressSearchRequest mockAddressSearchRequest() {
    return AddressSearchRequest.builder().searchTerm("LU1 1BN").build();
  }

  public static QASearch mockQASearchRequest() {
    QASearch request = new QASearch();
    request.setSearch(MOCK_POSTCODE);
    request.setCountry(MOCK_COUNTRY);

    final EngineType engineType = new EngineType();
    engineType.setPromptSet(PromptSetType.DEFAULT);
    engineType.setValue(EngineEnumType.SINGLELINE);
    engineType.setFlatten(true);
    request.setEngine(engineType);
    return request;
  }

  public static QASearchResult mockQASearchResponse() {
    final QASearchResult searchResult = new QASearchResult();
    final QAPicklistType picklistType = new QAPicklistType();
    final PicklistEntryType picklistEntryType = new PicklistEntryType();
    picklistEntryType.setFullAddress(true);
    picklistEntryType.setMoniker(MOCK_MONIKER);

    picklistType.getPicklistEntry().add(picklistEntryType);

    final PicklistEntryType picklistEntryType2 = new PicklistEntryType();
    picklistEntryType2.setFullAddress(true);
    picklistEntryType2.setMoniker(MOCK_MONIKER + 123);
    picklistType.getPicklistEntry().add(picklistEntryType2);

    searchResult.setQAPicklist(picklistType);
    return searchResult;
  }

  public static QAGetAddress mockGetAddressRequestWithMonikerId(String monikerId) {
    QAGetAddress address = new QAGetAddress();
    address.setMoniker(monikerId);

    return address;
  }

  public static Address mockGetAddressResponse() {
    Address address = new uk.co.whitbread.qas.addresslookup.api.Address();

    final QAAddressType qaAddressType = new QAAddressType();

    final AddressLineType addressLineTypeCompany = new AddressLineType();
    addressLineTypeCompany.setLabel("Organisation");
    addressLineTypeCompany.setLine("TEST_COMPANY");
    qaAddressType.getAddressLine().add(addressLineTypeCompany);

    final AddressLineType addressLineTypeLine1 = new AddressLineType();
    addressLineTypeLine1.setLabel("");
    addressLineTypeLine1.setLine("TEST_LINE1");
    qaAddressType.getAddressLine().add(addressLineTypeLine1);

    final AddressLineType addressLineTypeLine2 = new AddressLineType();
    addressLineTypeLine2.setLabel("");
    addressLineTypeLine2.setLine("TEST_LINE2");
    qaAddressType.getAddressLine().add(addressLineTypeLine2);

    final AddressLineType addressLineTypeLine3 = new AddressLineType();
    addressLineTypeLine3.setLabel("");
    addressLineTypeLine3.setLine("TEST_LINE3");
    qaAddressType.getAddressLine().add(addressLineTypeLine3);

    final AddressLineType addressLineTypeTown = new AddressLineType();
    addressLineTypeTown.setLabel("Town");
    addressLineTypeTown.setLine("TEST_TOWN");
    qaAddressType.getAddressLine().add(addressLineTypeTown);

    final AddressLineType addressLineTypeCounty = new AddressLineType();
    addressLineTypeCounty.setLabel("County");
    addressLineTypeCounty.setLine("TEST_COUNTY");
    qaAddressType.getAddressLine().add(addressLineTypeCounty);

    final AddressLineType addressLineTypePostcode = new AddressLineType();
    addressLineTypePostcode.setLabel("Postcode");
    addressLineTypePostcode.setLine("TEST_POSTCODE");
    qaAddressType.getAddressLine().add(addressLineTypePostcode);

    final AddressLineType addressLineTypeInvalidLabel = new AddressLineType();
    addressLineTypeInvalidLabel.setLabel("invalid_label");
    addressLineTypeInvalidLabel.setLine("invalid_label");
    qaAddressType.getAddressLine().add(addressLineTypeInvalidLabel);

    address.setQAAddress(qaAddressType);

    return address;
  }

}
