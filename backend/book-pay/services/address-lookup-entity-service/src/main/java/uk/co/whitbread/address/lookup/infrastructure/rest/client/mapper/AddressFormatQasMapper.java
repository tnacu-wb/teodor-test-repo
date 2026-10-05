package uk.co.whitbread.address.lookup.infrastructure.rest.client.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.address.lookup.domain.model.out.AddressFormatResponse;
import uk.co.whitbread.qas.addresslookup.api.Address;
import uk.co.whitbread.qas.addresslookup.api.AddressLineType;
import uk.co.whitbread.qas.addresslookup.api.QAAddressType;
import uk.co.whitbread.qas.addresslookup.api.QAGetAddress;

@Component
@Slf4j
public class AddressFormatQasMapper {

  private static final String ORGANISATION = "organisation";
  private static final String TOWN = "town";
  private static final String COUNTY = "county";
  private static final String POSTCODE = "postcode";
  private static final String DEFAULT_COUNTRY = "GB";
  private static final String SEPARATOR = ", ";

  public QAGetAddress toDto(String monikerId) {
    QAGetAddress getAddress = new QAGetAddress();
    getAddress.setMoniker(monikerId);

    return getAddress;
  }

  public Optional<AddressFormatResponse> toModel(Address address) {
    if (address != null) {
      List<AddressLineType> addressLinesTypes = Optional.ofNullable(address.getQAAddress())
          .map(QAAddressType::getAddressLine)
          .orElse(Collections.emptyList());

      final AddressFormatResponse formattedAddress = AddressFormatResponse.builder().build();
      setAddressLines(formattedAddress, addressLinesTypes);
      setAddressLabel(formattedAddress);

      return Optional.of(formattedAddress);
    } else {
      log.error("Unable to find any address.");
      return Optional.empty();
    }
  }

  private void setAddressLabel(AddressFormatResponse address) {

    final List<String> labels = new ArrayList<>();

    addToAddressLabel(address.getCompanyName(), labels);
    addToAddressLabel(address.getAddressLine1(), labels);
    addToAddressLabel(address.getAddressLine2(), labels);
    addToAddressLabel(address.getAddressLine3(), labels);
    addToAddressLabel(address.getAddressLine4(), labels);
    addToAddressLabel(address.getAddressLine5(), labels);
    addToAddressLabel(address.getPostalCode(), labels);

    address.setLabel(
        CollectionUtils.isEmpty(labels) ? null : StringUtils.join(labels, SEPARATOR));

  }

  private void addToAddressLabel(String addressLine, List<String> labels) {
    if (!StringUtils.isBlank(addressLine)) {
      labels.add(addressLine);
    }
  }

  private void setAddressLines(AddressFormatResponse address, List<AddressLineType> addressLines) {

    final MutableInt index = new MutableInt(1);

    addressLines.forEach(addressLineType -> {
      final String label = addressLineType.getLabel();
      final String line = addressLineType.getLine();
      if (StringUtils.isBlank(line)) {
        return;
      }
      if (StringUtils.isBlank(label)) {
        setAddressLinesWithoutLabels(address, index, line);
      } else {
        setAddressLinesWithLabels(address, label, line);
      }
      address.setCountry(DEFAULT_COUNTRY);
    });
  }

  private void setAddressLinesWithoutLabels(AddressFormatResponse address, MutableInt index,
      String line) {
    switch (index.intValue()) {
      case 1 -> address.setAddressLine1(line);
      case 2 -> address.setAddressLine2(line);
      case 3 -> address.setAddressLine3(line);
      default -> log.error("Unexpected address line: {} at index: {}", line, index.intValue());
    }
    index.increment();
  }

  private void setAddressLinesWithLabels(AddressFormatResponse address, String label, String line) {
    switch (label.toLowerCase()) {
      case ORGANISATION -> address.setCompanyName(line);
      case TOWN -> address.setAddressLine4(line);
      case COUNTY -> address.setAddressLine5(line);
      case POSTCODE -> address.setPostalCode(line);
      default -> log.error("Unexpected label: {} found for address line: {}", label, line);
    }
  }
}
