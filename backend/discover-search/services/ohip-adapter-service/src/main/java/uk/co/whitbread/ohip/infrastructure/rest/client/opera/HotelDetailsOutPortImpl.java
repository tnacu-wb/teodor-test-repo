package uk.co.whitbread.ohip.infrastructure.rest.client.opera;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.opera.out.HotelStatus;
import uk.co.whitbread.ohip.domain.model.opera.out.OperaHotelDetails;
import uk.co.whitbread.ohip.domain.model.opera.out.OperaHotelDetailsList;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelDetailsOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.mapper.HotelStatusMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.opera.properties.OhipHotelDetailsClient;

@RequiredArgsConstructor
@Slf4j
public class HotelDetailsOutPortImpl implements HotelDetailsOutPort {

  private final OhipHotelDetailsClient ohipHotelDetailsClient;
  private final HotelStatusMapper hotelStatusMapper;

  @Override
  public List<HotelStatus> getHotelsMigrationStatus(Set<String> hotelIds) {
    log.debug("Entered getHotelsMigrationStatus for hotelIds= {}",
        sanitizeInput(hotelIds));
    List<HotelStatus> hotelStatusList = new ArrayList<>();
    boolean hotelOnSale = true;

    for (String hotelId : hotelIds) {
      try {
        final OperaHotelDetailsList operaHotelDetailsList = ohipHotelDetailsClient.getHotelDetails(
            hotelId);

        final List<OperaHotelDetails> hotelDetails = operaHotelDetailsList.getHotelDetails();

        final OperaHotelDetails hotelDetailsFromOperaForOnSaleFlag = hotelDetails.stream()
            .filter(hotelDetail -> hotelDetail.getCategory()
                .equals(OhipConstants.MIGRATION_CATEGORY_ONSALE))
            .findFirst()
            .orElse(buildOperaHotelDetails(hotelId, OhipConstants.MIGRATION_CATEGORY_ONSALE,
                OhipConstants.BOOLEAN_FALSE, 2, false));

        hotelOnSale = hotelDetailsFromOperaForOnSaleFlag.getCode()
            .equalsIgnoreCase("TRUE");

        final OperaHotelDetails hotelDetailsFromOperaForPmsSource = hotelDetails.stream()
            .filter(hotelDetail -> hotelDetail.getCategory().equals("PMS"))
            .findFirst()
            .orElse(buildOperaHotelDetails(hotelId, OhipConstants.MIGRATION_CATEGORY,
                OhipConstants.MIGRATION_STATUS_FALSE, 1, false));

        hotelDetailsFromOperaForPmsSource.setOnSale(hotelOnSale);

        hotelStatusList.add(hotelStatusMapper.toDomainModel(hotelDetailsFromOperaForPmsSource));
      } catch (Exception e) {
        log.error("Hotel with hotelId: {} comes from BART", sanitizeInput(hotelIds));

        hotelStatusList.add(hotelStatusMapper.toDomainModel(
            buildOperaHotelDetails(hotelId, OhipConstants.MIGRATION_CATEGORY,
                OhipConstants.MIGRATION_STATUS_FALSE, 1, true)));
      }
    }
    for (HotelStatus hotelStatus : hotelStatusList) {
      if (hotelStatus.getPmsSource().equals(OhipConstants.MIGRATION_PMS_SOURCE_OPERA)) {
        hotelStatus.setPmsSource(OhipConstants.MIGRATION_PMS_SOURCE_OPERA);
      } else {
        hotelStatus.setPmsSource(OhipConstants.MIGRATION_PMS_SOURCE_BART);
      }
    }

    return hotelStatusList;
  }

  private OperaHotelDetails buildOperaHotelDetails(final String hotelCode, String category,
      final String code, int sequence, boolean onSale) {
    return OperaHotelDetails.builder()
        .hotelId(hotelCode)
        .category(category)
        .description("Migration status")
        .sequence(sequence)
        .code(code)
        .onSale(onSale)
        .build();
  }

  private String sanitizeInput(Set<String> input) {
    StringBuilder sb = new StringBuilder();
    for (String id : input) {
      if (!sb.isEmpty()) {
        sb.append(", ");
      }
      sb.append(id.replaceAll("[\r\n]", "").replaceAll("[^\\w\\s-]", ""));
    }
    return sb.toString();
  }
}
