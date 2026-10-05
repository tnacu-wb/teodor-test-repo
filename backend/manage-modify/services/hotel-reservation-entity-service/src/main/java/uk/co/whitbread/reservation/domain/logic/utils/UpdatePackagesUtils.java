package uk.co.whitbread.reservation.domain.logic.utils;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import uk.co.whitbread.reservation.domain.model.in.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.in.RoomsSelectionsByReservationId;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsPackagesResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomsSelectionsByReservation;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class UpdatePackagesUtils {

  public static Optional<UpdateReservationPackagesByIdRequest> excludedPackages(
      ReservationsPackagesResponse previousPackageSelections,
      BasketResponse tempBasket, String arrival, String departure, List<String> excludedPackages) {
    var previousRoomsSelections = new LinkedList<RoomsSelectionsByReservationId>();
    var roomsSelections = new LinkedList<RoomsSelectionsByReservationId>();

    tempBasket.getItems().forEach(
        basketItemResponse -> {
          var tempReservationId = getTempReservationId(tempBasket, basketItemResponse);
          if (tempReservationId.isPresent()) {
            var previousPackages = getOptionalPackageForReservation(previousPackageSelections,
                tempReservationId.get());
            if (previousPackages.isPresent()) {
              var pkgSelection = previousPackages.get().getPackagesSelection();
              if (Objects.nonNull(pkgSelection)) {
                var toBeExcluded = pkgSelection.stream()
                    .filter(ps -> excludedPackages.contains(ps.getId())).toList();
                if (!toBeExcluded.isEmpty()) {
                  var previousRoomSelectionsElement = getRoomSelectionsElement(
                      basketItemResponse.getSourceId(), previousPackages);
                  previousRoomsSelections.add(previousRoomSelectionsElement);
                  var updatedPackages = previousPackages.get().getPackagesSelection().stream()
                      .filter(ps -> !excludedPackages.contains(ps.getId())).toList();
                  var roomSelectionsElement = new RoomsSelectionsByReservationId(
                      basketItemResponse.getSourceId(),
                      getPackageSelectionsFromResponse(updatedPackages));
                  roomsSelections.add(roomSelectionsElement);
                }
              }
            }
          }
        });

    return !CollectionUtils.isEmpty(roomsSelections)
        ? buildRequest(tempBasket, arrival, departure, previousRoomsSelections, roomsSelections) :
        Optional.empty();
  }


  public static Optional<RoomsSelectionsByReservation> getOptionalPackageForReservation(
      ReservationsPackagesResponse previousPackageSelections, String resId) {
    return previousPackageSelections.getRoomsSelections().stream()
        .filter(previousRoomSelection ->
            resId.equals(previousRoomSelection.getReservationId()))
        .findFirst();
  }

  private static Optional<RoomsSelectionsByReservation> getUnbundledOptionalPackagesForReservation(
      ReservationsPackagesResponse packageSelections, String resId) {
    return packageSelections.getRoomsSelections().stream()
        .filter(previousRoomSelection -> resId.equals(previousRoomSelection.getReservationId()))
        .findFirst()
        .map(roomSelection -> {
          var packages = roomSelection.getPackagesSelection();
          List<uk.co.whitbread.reservation.domain.model.out.PackagesSelection> unbundledPackages =
              packages == null ? List.of() :
                  packages.stream()
                      .filter(pk -> pk.getRatePlanCode() == null)
                      .toList();

          return RoomsSelectionsByReservation.builder()
              .reservationId(roomSelection.getReservationId())
              .packagesSelection(unbundledPackages)
              .build();
        });
  }

  public static RoomsSelectionsByReservation getPackageForReservation(
      ReservationsPackagesResponse previousPackageSelections, String resId) {
    return getUnbundledOptionalPackagesForReservation(previousPackageSelections, resId)
        .orElse(RoomsSelectionsByReservation.builder()
            .reservationId(resId)
            .packagesSelection(new ArrayList<>())
            .build());
  }

  public static RoomsSelectionsByReservationId getRoomSelectionsElement(String resId,
      Optional<RoomsSelectionsByReservation> roomPackages) {
    return new RoomsSelectionsByReservationId(
        resId,
        roomPackages.map(
                roomsSelectionsByReservation -> getPackageSelectionsFromResponse(
                roomsSelectionsByReservation.getPackagesSelection()))
            .orElseGet(LinkedList::new)
    );
  }

  public static List<PackagesSelection> getPackageSelectionsFromResponse(
      List<uk.co.whitbread.reservation.domain.model.out.PackagesSelection> packagesSelection) {
    List<PackagesSelection> packageSelectionsList = new ArrayList<>();

    if (!CollectionUtils.isEmpty(packagesSelection)) {
      packagesSelection.forEach(packagesSelectionResp -> {
        var currentPackSelection = new PackagesSelection();
        currentPackSelection.setId(packagesSelectionResp.getId());
        currentPackSelection.setNoSelections(packagesSelectionResp.getNoOfSelections());
        currentPackSelection.setPackageGroup(packagesSelectionResp.getPackageGroup());
        packageSelectionsList.add(currentPackSelection);
      });
    }
    return packageSelectionsList;
  }

  @NotNull
  private static Optional<String> getTempReservationId(BasketResponse tempBasket,
      BasketItemResponse basketItemResponse) {
    if (tempBasket.getLinkAmendReservations() != null) {
      return tempBasket.getLinkAmendReservations()
          .entrySet()
          .stream()
          .filter(entry -> basketItemResponse.getSourceId().equals(entry.getValue()))
          .map(Map.Entry::getKey).findFirst();
    }
    return Optional.empty();
  }

  @NotNull
  private static Optional<UpdateReservationPackagesByIdRequest> buildRequest(
      BasketResponse tempBasket, String arrival, String departure,
      LinkedList<RoomsSelectionsByReservationId> previousRoomsSelections,
      LinkedList<RoomsSelectionsByReservationId> roomsSelections) {
    UpdateReservationPackagesByIdRequest updatePackagesByReservation = new UpdateReservationPackagesByIdRequest();
    updatePackagesByReservation.setPreviousRoomsSelections(previousRoomsSelections);
    updatePackagesByReservation.setRoomsSelections(roomsSelections);
    updatePackagesByReservation.setHotelId(tempBasket.getHotelId());
    updatePackagesByReservation.setBasketReference(tempBasket.getReference());
    updatePackagesByReservation.setArrival(arrival);
    updatePackagesByReservation.setDeparture(departure);
    return Optional.of(updatePackagesByReservation);
  }
}
