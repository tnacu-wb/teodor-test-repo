import { gql } from 'graphql-request';

export const resetMemorableWordQuery = () => gql`
  mutation resetMemorableWord(
    $tetheredUserGuid: String!
    $memorableWord: String!
    $scheme: String
  ) {
    resetMemorableWord(
      resetMemorableWordRequest: {
        tetheredUserGuid: $tetheredUserGuid
        memorableWord: $memorableWord
        scheme: $scheme
      }
    ) {
      resultCode
    }
  }
`;
