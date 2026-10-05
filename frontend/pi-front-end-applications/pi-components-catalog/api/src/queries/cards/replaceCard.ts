import { gql } from 'graphql-request';

export const replacePIBACardMutation = () => gql`
  mutation ReplaceCard(
    $tetheredUserGuid: String!
    $cardId: String!
    $scheme: Scheme!
    $replaceCardRequest: ReplaceCardRequest!
  ) {
    replaceCard(
      tetheredUserGuid: $tetheredUserGuid
      cardId: $cardId
      scheme: $scheme
      replaceCardRequest: $replaceCardRequest
    )
  }
`;
