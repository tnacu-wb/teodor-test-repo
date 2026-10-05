import { gql } from 'graphql-request';

export const GET_ROOM_OCCUPANCY_LIMITATIONS_QUERY = gql`
  query getRoomOccupancyLimitations($channel: Channel!, $brand: String) {
    roomOccupancyLimitations(channel: $channel, brand: $brand) {
      roomOccupancies {
        adultsNumber
        childrenNumber
        acceptedRoomTypes
      }
    }
  }
`;
