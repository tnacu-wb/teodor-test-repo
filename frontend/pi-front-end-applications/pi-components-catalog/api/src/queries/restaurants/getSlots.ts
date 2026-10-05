import { gql } from 'graphql-request';

export const GET_SLOTS = gql`
  query getSlots(
    $adult: Int!
    $children: Int!
    $from: String!
    $until: String!
    $siteId: String!
    $time: String!
  ) {
    slots(
      from: $from
      siteId: $siteId
      until: $until
      adult: $adult
      children: $children
      time: $time
    ) {
      dates {
        breakFastAvailable
        lunchAvailable
        dinnerAvailable
        date
        sessionDto {
          dinner {
            time
            available
            totalCapacity
            remainingCapacity
            canEnquire
            closed
          }
          lunch {
            time
            available
            totalCapacity
            remainingCapacity
            canEnquire
            closed
          }
          breakFast {
            time
            available
            totalCapacity
            remainingCapacity
            canEnquire
            closed
          }
        }
      }
    }
  }
`;
