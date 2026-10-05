import { gql } from 'graphql-request';

export const businessTetherQuery = gql`
  mutation businessTether(
    $linkCode: String!
    $linkId: String!
    $memorableWord: String!
    $saveInCdh: Boolean!
  ) {
    businessTether(
      tetherLinkRequest: {
        linkCode: $linkCode
        linkId: $linkId
        memorableWord: $memorableWord
        saveInCdh: $saveInCdh
      }
    ) {
      guid
    }
  }
`;
