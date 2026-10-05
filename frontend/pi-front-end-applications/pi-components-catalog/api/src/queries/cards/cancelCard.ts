import { gql } from 'graphql-request';

export const cancelAndReplacePIBACardQuery = () => gql`
  mutation CancelAndReplaceInnBPIBACard(
    $tetheredUserId: String!
    $cardId: String!
    $cancelAndReplaceInnBCardRequest: CancelAndReplaceInnBCardRequest!
  ) {
    cancelAndReplaceInnBPIBACard(
      tetheredUserId: $tetheredUserId
      cardId: $cardId
      cancelAndReplaceInnBCardRequest: $cancelAndReplaceInnBCardRequest
    ) {
      cancelledCardDetails {
        cardHolderName
        cardId
        email
        expiryDate
        firstName
        activated
        myCard
        lastName
        pan
        primarySchemeCustomerId
        status
        title
      }
      newCardDetails {
        cardHolderName
        cardId
        email
        expiryDate
        firstName
        activated
        myCard
        lastName
        pan
        primarySchemeCustomerId
        status
        title
      }
    }
  }
`;
