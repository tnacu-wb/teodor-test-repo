package uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.address.lookup.domain.model.out.AddressSearchResponse;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;
import uk.co.whitbread.qas.addresslookup.api.EngineEnumType;
import uk.co.whitbread.qas.addresslookup.api.EngineType;
import uk.co.whitbread.qas.addresslookup.api.PicklistEntryType;
import uk.co.whitbread.qas.addresslookup.api.PromptSetType;
import uk.co.whitbread.qas.addresslookup.api.QAPicklistType;
import uk.co.whitbread.qas.addresslookup.api.QASearch;
import uk.co.whitbread.qas.addresslookup.api.QASearchResult;

@Component
@RequiredArgsConstructor
public class AddressSearchQasMapper {

  private final QasProperties qasProperties;

  public QASearch toDto(String postCode) {
    final QASearch searchRequest = new QASearch();
    searchRequest.setSearch(postCode);
    searchRequest.setCountry(qasProperties.getCountryBusinessDataSetId());
    searchRequest.setLayout(qasProperties.getLayoutLeisure());

    final EngineType engineType = new EngineType();
    engineType.setPromptSet(PromptSetType.DEFAULT);
    engineType.setValue(EngineEnumType.SINGLELINE);
    engineType.setFlatten(true);

    searchRequest.setEngine(engineType);

    return searchRequest;
  }

  public List<AddressSearchResponse> toModel(QASearchResult searchResult) {
    List<AddressSearchResponse> partialAddresses = new ArrayList<>();

    final List<PicklistEntryType> pickListEntryTypes =
        Optional.ofNullable(searchResult)
            .map(QASearchResult::getQAPicklist)
            .map(QAPicklistType::getPicklistEntry)
            .orElse(Collections.emptyList());

    pickListEntryTypes.stream()
        .filter(PicklistEntryType::isFullAddress)
        .forEach(picklistEntryType -> {
          AddressSearchResponse partialAddress = AddressSearchResponse.builder()
              .monikerId(picklistEntryType.getMoniker())
              .address(picklistEntryType.getPartialAddress())
              .build();
          partialAddresses.add(partialAddress);
        });
    return partialAddresses;
  }
}
