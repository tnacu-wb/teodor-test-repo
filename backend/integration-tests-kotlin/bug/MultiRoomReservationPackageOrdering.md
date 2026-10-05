# Multi-room reservation packages can be applied to the wrong room

## Status

Known bug. It is reproduced by the integration test suite and has not been fixed in the services.

## Summary

For a basket containing multiple reservations, `PUT /v1/reservations/ancillaries` associates each `roomsSelections` entry with an Opera reservation by list position. The reservation IDs stored in the basket can already have lost their original room order, so a package selected for one room can be sent to another reservation.

For example, a request intending to add early check-in (`HSCKIN`) to the first room and late check-out (`HSCOU2`) to the second room can send `HSCKIN` to the second reservation.

The integration test detects this because its Opera PUT stubs validate the expected reservation/package pair. The mismatched request is rejected and the public endpoint returns `500`. Against a permissive downstream system, the more serious outcome is possible: the call may succeed while updating the wrong guest's room.

## Where the bug is in OHIP

The ordering is first lost during multi-room reservation creation in [`HotelReservationOutPortImpl.createReservation`](../../discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/client/reservation/HotelReservationOutPortImpl.java#L454):

1. Reservations are created concurrently with Reactor `flatMap`.
2. Created reservation IDs are collected with `collect(toSet())`.
3. [`ReservationResponseOhipMapper.toReservationResponseLightModel`](../../discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/client/reservation/mapper/ReservationResponseOhipMapper.java#L1051) streams that `Set` into the response reservation list.

A `Set` does not represent the positional relationship between the original room requests and their created reservation IDs. Concurrency also means completion order cannot be used as request order.

The invalid positional assumption is then used during package updates in [`HotelReservationOutPortImpl.addPackagesToReservations`](../../discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/client/reservation/HotelReservationOutPortImpl.java#L1613):

```java
Flux.fromIterable(reservationsId)
    .flatMap(res -> ohipReservationClient.sendChangeReservationRequest(
        reservationPackagesRequest.getHotelId(),
        res,
        mapToUpdateReservationPackagesOhip(
            reservationPackagesRequest,
            reservationsId.indexOf(res),
            packagesResponseOhipDto)))
```

[`mapToUpdateReservationPackagesOhip`](../../discover-search/services/ohip-adapter-service/src/main/java/uk/co/whitbread/ohip/infrastructure/rest/client/reservation/HotelReservationOutPortImpl.java#L3181) uses that index to read `roomsSelections.get(roomIndex)`. Therefore, correctness depends on the basket reservation IDs having exactly the same order as the caller's room selections.

The hotel-reservation entity service does not repair this association. [`UpdatePackagesRequestOhipMapper`](../../manage-modify/services/hotel-reservation-entity-service/src/main/java/uk/co/whitbread/reservation/infrastructure/rest/client/ohip/mapper/UpdatePackagesRequestOhipMapper.java#L26) reads basket item `sourceId` values into a list and forwards them to OHIP, while the public request's `roomsSelections` remain positional.

## Scenario that found the bug

The bug is documented by the scenario:

> `PUT /v1/reservations/ancillaries exposes the multi-room package ordering bug`

It is in [`UpdateReservationPackagesSpec`](../src/integrationTest/kotlin/uk/co/whitbread/integrationtests/journeys/hotelreservation/UpdateReservationPackagesSpec.kt#L93).

The scenario:

1. Creates a two-room basket backed by Opera reservation IDs `6003102` and `6003103`.
2. Selects `HSCKIN` for room index 0.
3. Selects `HSCOU2` for room index 1.
4. Installs strict Opera mappings expecting `HSCKIN` for `6003102` and `HSCOU2` for `6003103`.
5. Observes OHIP sending `HSCKIN` to `6003103` and the public endpoint returning `500`.

The scenario intentionally expects `500` until reservation identity is preserved throughout creation, basket storage, and ancillary updates. After the bug is fixed, it should expect `200` and the returned basket reference.

## Reproduction with curl

The following requests reproduce the service-level flow when the local Docker test stack is running on its default port. The dates must be valid future stay dates, and the configured Opera dependency must be able to create both reservations.

First create a two-room basket:

```bash
curl --fail-with-body \
  --request POST \
  --url http://localhost:9103/v1/reservations \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{
    "reservations": [
      {
        "hotelId": "HEAPTI",
        "arrival": "2026-07-30",
        "departure": "2026-08-01",
        "adultsNumber": 1,
        "childrenNumber": 0,
        "cotRequired": false,
        "roomRates": {
          "ratePlanCode": "SEMIFLEX",
          "pmsRoomType": "VPPDBL",
          "specialRequests": ["SING"],
          "startDate": "2026-07-30",
          "endDate": "2026-08-01"
        },
        "reservationPackages": []
      },
      {
        "hotelId": "HEAPTI",
        "arrival": "2026-07-30",
        "departure": "2026-08-01",
        "adultsNumber": 1,
        "childrenNumber": 0,
        "cotRequired": false,
        "roomRates": {
          "ratePlanCode": "SEMIFLEX",
          "pmsRoomType": "VPPDBL",
          "specialRequests": ["SING"],
          "startDate": "2026-07-30",
          "endDate": "2026-08-01"
        },
        "reservationPackages": []
      }
    ],
    "bookingChannel": {
      "channel": "PI",
      "subchannel": "WEB",
      "language": "EN"
    },
    "bookingFlowId": "reproduce-package-ordering-bug",
    "getReservationsByIds": false,
    "isOta": false
  }'
```

Copy the `basketReference` from the `201` response, then submit different packages for the two room positions:

```bash
curl --fail-with-body \
  --request PUT \
  --url http://localhost:9103/v1/reservations/ancillaries \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{
    "basketReferenceId": "<basket-reference-from-create-response>",
    "hotelId": "HEAPTI",
    "arrivalDate": "2026-07-30",
    "departureDate": "2026-08-01",
    "roomsSelections": [
      {
        "packagesSelection": [
          {"id": "HSCKIN", "noOfSelections": 1}
        ]
      },
      {
        "packagesSelection": [
          {"id": "HSCOU2", "noOfSelections": 1}
        ]
      }
    ],
    "previousRoomsSelections": null
  }'
```

Expected behavior: the first created reservation receives `HSCKIN`, and the second receives `HSCOU2`.

Observed integration-test behavior: OHIP attempts to send `HSCKIN` to reservation `6003103` instead of `6003102`; strict Opera validation rejects the request and the endpoint returns `500`.

Because `HashSet` iteration and concurrent completion make this data-dependent, the incorrect order may not occur for every pair of real reservation IDs. The integration scenario uses IDs that reproduce it deterministically.

## Fix direction

Preserve an explicit room-to-reservation association rather than relying on collection order. At minimum, reservation creation must return IDs in original request order. A stronger design would carry reservation identity alongside each package selection so the ancillary update does not have to join independent lists by index.
