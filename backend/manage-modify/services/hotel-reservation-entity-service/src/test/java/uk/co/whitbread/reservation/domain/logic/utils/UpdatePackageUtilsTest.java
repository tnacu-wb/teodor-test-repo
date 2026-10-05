package uk.co.whitbread.reservation.domain.logic.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;

class UpdatePackageUtilsTest {

  public static final String PACKAGE_GROUP = "MDP";

  @ParameterizedTest
  @CsvSource(value = {"b, true", "d, false"})
  void excludedPackagesTest(String packageId, boolean isRemoved) {
    //arrange
    var selectionA = buildPackage("a");
    var selectionToExclude = buildPackage("b");
    var selectionC = buildPackage("c");
    var expectedPrev = buildRoomsSelections(List.of(selectionA, selectionToExclude), "resId1");
    var expectedPrev1 = buildRoomsSelections(List.of(selectionC, selectionToExclude), "resId2");
    var expectedSel = buildRoomsSelections(List.of(selectionA), "resId1");
    var expectedSel1 = buildRoomsSelections(List.of(selectionC), "resId2");
    var expected = UpdateReservationPackagesByIdRequest.builder()
        .arrival("arrival")
        .departure("departure")
        .roomsSelections(List.of(expectedSel, expectedSel1))
        .previousRoomsSelections(List.of(expectedPrev, expectedPrev1))
        .build();

    var packageSelectionA = buildPackagesSelection("a");
    var packageSelectionC = buildPackagesSelection("c");
    var toExclude = buildPackagesSelection("b");
    List<PackagesSelection> previous = List.of(packageSelectionA, toExclude);
    List<PackagesSelection> previous1 = List.of(packageSelectionC, toExclude);
    ReservationsPackagesResponse packages = ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(
            RoomsSelectionsByReservation.builder()
                .reservationId("refOrig1")
                .packagesSelection(previous)
                .build(),
            RoomsSelectionsByReservation.builder()
                .reservationId("refOrig2")
                .packagesSelection(previous1)
                .build()))
        .build();

    Map<String, String> map = new HashMap();
    map.put("refOrig1", "resId1");
    map.put("refOrig2", "resId2");
    BasketResponse basketResponse = BasketResponse.builder()
        .linkAmendReservations(map)
        .items(List.of(BasketItemResponse.builder()
                .sourceId("resId1")
                .build(),
            BasketItemResponse.builder()
                .sourceId("resId2")
                .build())).build();
    //act
    var actual = UpdatePackagesUtils.excludedPackages(packages, basketResponse,
        "arrival", "departure", List.of(packageId));
    //assert
    assertEquals(isRemoved, actual.isPresent());
    if (isRemoved) {
      assertEquals(expected, actual.get());
    } else {
      assertTrue(actual.isEmpty());
    }
  }

  @ParameterizedTest
  @CsvSource(value = {"refOrig1, x, true", "refOrig, x1, true", "refOrig, x, false"})
  void excludedPackages_wrongTempBasketLinkReservationTest(String refOrigin, String reservationId,
      boolean hasPrevious) {
    //arrange
    List<PackagesSelection> previous = List.of();
    if (hasPrevious) {
      var packageSelection = buildPackagesSelection("a");
      var toExclude = buildPackagesSelection("b");
      previous = List.of(packageSelection, toExclude);
    }
    ReservationsPackagesResponse packages = ReservationsPackagesResponse.builder()
        .roomsSelections(List.of(
            RoomsSelectionsByReservation.builder()
                .reservationId("refOrig")
                .packagesSelection(previous)
                .build()))
        .build();

    BasketResponse basketResponse = BasketResponse.builder()
        .linkAmendReservations(Maps.of(refOrigin, reservationId))
        .items(List.of(BasketItemResponse.builder()
            .sourceId("x")
            .build())).build();
    //act
    var actual = UpdatePackagesUtils.excludedPackages(packages, basketResponse,
        "arrival", "departure", List.of("c"));

    //assert
    assertTrue(actual.isEmpty());
  }

  private PackagesSelection buildPackagesSelection(String id) {
    return PackagesSelection.builder()
        .id(id)
        .noOfSelections(1)
        .packageGroup(PACKAGE_GROUP)
        .build();
  }

  private uk.co.whitbread.reservation.domain.model.in.PackagesSelection buildPackage(String id) {
    var selection = new uk.co.whitbread.reservation.domain.model.in.PackagesSelection();
    selection.setId(id);
    selection.setNoSelections(1);
    selection.setPackageGroup(PACKAGE_GROUP);
    return selection;
  }

  private RoomsSelectionsByReservationId buildRoomsSelections(
      List<uk.co.whitbread.reservation.domain.model.in.PackagesSelection> list, String resId) {
    return RoomsSelectionsByReservationId.builder()
        .reservationId(resId)
        .packagesSelection(list)
        .build();
  }
}
