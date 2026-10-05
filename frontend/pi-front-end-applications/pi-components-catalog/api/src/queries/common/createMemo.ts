import { gql } from 'graphql-request';

export const CREATE_MEMO = gql`
  mutation createMemo(
    $basketReference: String!
    $description: String!
    $channel: Channel!
    $subchannel: String!
    $language: String
  ) {
    createMemo(
      createMemoCriteria: {
        basketReference: $basketReference
        description: $description
        bookingChannel: { channel: $channel, subchannel: $subchannel, language: $language }
      }
    ) {
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
