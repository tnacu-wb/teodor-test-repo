import { gql } from 'graphql-request';

export const activatePibaCardMutation = () => gql`
  mutation InnBCardActivateMutation(
    $tetheredUserId: String!
    $cardId: String!
    $countryCode: String!
  ) {
    activateInnBPIBACard(
      tetheredUserId: $tetheredUserId
      cardId: $cardId
      countryCode: $countryCode
    )
  }
`;
