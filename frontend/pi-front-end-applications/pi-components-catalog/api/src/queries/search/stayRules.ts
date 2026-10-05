import { gql } from 'graphql-request';

export const GET_STAY_RULES_QUERY = gql`
  query getStayRules($channel: Channel!) {
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
  }
`;
