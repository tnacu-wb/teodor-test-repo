import { gql } from 'graphql-request';

export const GET_SEARCH_RULES_QUERY = gql`
  query getSearchRules($channel: Channel!) {
    maxNightsLimitation(channel: $channel) {
      maxNights
    }
    globalConfig(channel: $channel) {
      maxRoomsLim {
        maxRooms
        maxRoomsAmend
      }
    }
    maxArrivalDateLimitation(channel: $channel) {
      maxArrivalDate
    }
    roomOccupancyLimitations(channel: $channel) {
      roomOccupancies {
        adultsNumber
        childrenNumber
        acceptedRoomTypes
      }
    }
  }
`;
