package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionSearchCriteria;


public class DistributionHotelAvailabilitiesUtilTest {

  @Test
  public void buildDistributionPayloadTest() {

    final DistributionSearchCriteria criteria = buildDistributionSearchCriteria();

    final String[][] roomTypes = new String[][]{{"DBLDBL", "ZIPDBL"}, {"ZIPSB", "SBSBDB"}};
    final Set<String> rateCodes = Set.of("FLEXRATE", "FLEXMEAL", "FLEXBEDB");

    final DistributionPayload distributionPayload =
        DistributionHotelAvailabilitiesUtil.buildDistributionPayload(criteria, roomTypes, rateCodes);

    assertThat(distributionPayload.getRoomTypes()).isEqualTo(roomTypes);
    assertThat(distributionPayload.getHotelCodes()).isEqualTo(criteria.getHotelCodes());
    assertThat(distributionPayload.getArrival()).isEqualTo(criteria.getArrival());
    assertThat(distributionPayload.getDeparture()).isEqualTo(criteria.getDeparture());
    assertThat(distributionPayload.getCot()).isEqualTo(criteria.getCot());
    assertThat(distributionPayload.getLanguage()).isEqualTo(criteria.getLanguage());
    assertThat(distributionPayload.getCountry()).isEqualTo(criteria.getCountry());
    assertThat(distributionPayload.getAdults()).isEqualTo(criteria.getAdults());
    assertThat(distributionPayload.getChildren()).isEqualTo(criteria.getChildren());
    assertThat(distributionPayload.getRooms()).isEqualTo(criteria.getRooms());
    assertThat(distributionPayload.getRoomQty()).isEqualTo(criteria.getRoomQty());

  }

  private DistributionSearchCriteria buildDistributionSearchCriteria() {

    return DistributionSearchCriteria
        .builder()
        .hotelCodes(Arrays.asList("PLYPTI", "PLYLOC", "PLYMAR", "LISBAR", "PAIWHI"))
        .arrival(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .departure(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(new boolean[]{false, false, false, false})
        .rooms(1)
        .language("EN")
        .country("GB")
        .build();
  }

}
