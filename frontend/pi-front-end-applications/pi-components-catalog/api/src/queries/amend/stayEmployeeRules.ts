import { gql } from 'graphql-request';

export const GET_EMPLOYEE_STAY_RULES_QUERY = gql`
  query getEmployeeStayRules($channel: Channel!) {
    globalConfig(channel: $channel) {
      maxRoomsLim {
        maxRooms
        maxRoomsAmend
      }
    }
  }
`;
