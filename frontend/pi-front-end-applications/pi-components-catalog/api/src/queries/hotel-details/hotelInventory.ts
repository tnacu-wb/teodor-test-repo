import { gql } from 'graphql-request';

export const GET_HOTEL_INVENTORY_QUERY = gql`
  query getHotelInventory($hotelId: String!, $dateRangeEnd: String!, $dateRangeStart: String!) {
    hotelInventory(
      hotelId: $hotelId
      dateRangeEnd: $dateRangeEnd
      dateRangeStart: $dateRangeStart
    ) {
      roomTypeInventories {
        availableCount
        code
      }
    }
  }
`;
