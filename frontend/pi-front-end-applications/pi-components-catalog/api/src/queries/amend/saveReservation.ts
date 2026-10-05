import { gql } from 'graphql-request';

export const SAVE_RESERVATION = gql`
  mutation updateReservationPackagesByReservation(
    $basketReferenceId: String!
    $hotelId: String!
    $arrivalDate: String!
    $departureDate: String!
    $roomsSelections: [RoomPackageSelectionByIdInput]
    $previousRoomsSelections: [RoomPackageSelectionByIdInput]
  ) {
    updateReservationPackagesByReservation(
      updateReservationPackagesRequest: {
        basketReferenceId: $basketReferenceId
        hotelId: $hotelId
        arrivalDate: $arrivalDate
        departureDate: $departureDate
        roomsSelections: $roomsSelections
        previousRoomsSelections: $previousRoomsSelections
      }
    )
  }
`;
