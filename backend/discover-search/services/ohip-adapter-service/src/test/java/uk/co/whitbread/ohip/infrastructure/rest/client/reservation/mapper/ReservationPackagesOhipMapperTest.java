package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackagesInfoPackageCodesList;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelectionScheduled;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelections;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesResponseOhipMapperTest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReservationPackagesOhipMapperImpl.class)
public class ReservationPackagesOhipMapperTest {

  private static final ObjectMapper mapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);

  @Autowired
  private ReservationPackagesOhipMapperImpl reservationPackagesOhipMapper;

  @ParameterizedTest
  @CsvSource({
      "BKFSTTEST, 2022-04-02, 2022-04-04, 2",
      "HSCKIN, 2022-04-02, 2022-04-02, 1",
      "HSCOU2, 2022-04-04, 2022-04-04, 1"})
  void injectReservationPackagesDetails_ECI_LCO(String packageName, LocalDate expectedArrival, LocalDate expectedDeparture, int expectedScheduleListSize) throws IOException {
    //Arrange
    ReservationPackagesRequest reservationPackagesRequest = mockReservationPackagesRequest();
    reservationPackagesRequest.setArrival(expectedArrival.toString());
    reservationPackagesRequest.setDeparture(expectedDeparture.toString());
    reservationPackagesRequest.getRoomsSelections().get(0).getPackagesSelection().get(0).setId(packageName);
    PackagesResponseOhipDto packages = createPackagesResponseOhip();

    //Act
    var reservationPackagesResponse = reservationPackagesOhipMapper.injectReservationPackagesDetails(reservationPackagesRequest, 0, packages);

    //Assert
    assertNotNull(reservationPackagesResponse);
    assertEquals(expectedScheduleListSize, reservationPackagesResponse.get(0).getReservationPackages().get(0).getScheduleList().size());
    assertEquals(expectedArrival, reservationPackagesResponse.get(0).getReservationPackages().get(0).getStartDate());
    assertEquals(expectedDeparture, reservationPackagesResponse.get(0).getReservationPackages().get(0).getEndDate());

    if ("HSCOU2".equals(packageName.toString())) {
      expectedArrival = expectedArrival.minusDays(1);
    }
    assertEquals(expectedArrival, reservationPackagesResponse.get(0).getReservationPackages().get(0).getScheduleList().get(0).getConsumptionDate());
  }

  @Test
  void injectReservationPackagesDetails_ScheduleDates() throws IOException {
    //Arrange
    ReservationPackagesRequest reservationPackagesRequest = mockReservationPackagesRequest();
    reservationPackagesRequest.getRoomsSelections().get(0)
        .setPackagesSelection(List.of(createPackagesScheduledSelection()));
    PackagesResponseOhipDto packages = createPackagesResponseOhip();

    //Act
    var reservationPackagesResponse = reservationPackagesOhipMapper.injectReservationPackagesDetails(reservationPackagesRequest, 0, packages);

    //Assert
    var scheduleList = reservationPackagesResponse
        .get(0)
        .getReservationPackages().get(0)
        .getScheduleList().get(0);
    assertNotNull(reservationPackagesResponse);
    assertEquals(LocalDate.parse("2022-04-03"),scheduleList.getConsumptionDate());
    assertEquals(LocalDate.parse("2022-04-03"),scheduleList.getReservationDate());

  }

  private PackagesResponseOhipDto createPackagesResponseOhip() throws IOException {
    return PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(
            PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource("__files/ohip_packageCodesList_object.json"),
            PackagesInfoPackageCodesList.class))
        .build();
  }

  private ReservationPackagesRequest mockReservationPackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(Collections.singletonList("0"));
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-04");

    reservationPackagesRequest.setRoomsSelections(
        Collections.singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }

  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(Collections.singletonList(createPackagesSelection()));

    return roomsSelections;
  }

  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("HSCKIN");

    return packagesSelection;
  }

  private PackagesSelection createPackagesScheduledSelection() {
    PackagesSelectionScheduled packagesSelection = new PackagesSelectionScheduled();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("HSCKIN");
    packagesSelection.setScheduledDates(List.of("2022-04-03"));

    return packagesSelection;
  }
}
