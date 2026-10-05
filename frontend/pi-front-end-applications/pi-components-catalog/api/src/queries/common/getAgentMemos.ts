import { gql } from 'graphql-request';

export const GET_AGENT_MEMOS = gql`
  query getMemos($basketReference: String!) {
    getMemos(basketReference: $basketReference) {
      memos {
        ids {
          reservationId
          memoIds
        }
        description
        createdOn
        createdBy
        modifiedOn
        modifiedBy
        memoType
      }
    }
  }
`;
