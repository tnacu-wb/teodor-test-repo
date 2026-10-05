import { gql } from 'graphql-request';

export const updatePIBACardQuery = () => gql`
  mutation updateInnBPIBACard(
    $tetheredUserGuid: String!
    $cardId: String!
    $countryCode: String!
    $updateInnBPIBACardRequest: UpdateInnBPIBACardRequest!
  ) {
    updateInnBPIBACard(
      tetheredUserGuid: $tetheredUserGuid
      cardId: $cardId
      countryCode: $countryCode
      updateInnBPIBACardRequest: $updateInnBPIBACardRequest
    ) {
      cardHolderName
      cardLimit
      cardId
      cardNumber
      myCard
      activated
      status
      primaryUserId
      userId
      expiryDate
      title
      firstName
      lastName
      email
      cardAction
      registeredUsers {
        apiUserGuid
        emailAddress
        displayName
        hasAddress
      }
      cardRestriction {
        startDate
        endDate
        restrictCardUsage
      }
      amountSpend {
        amount
        currencyCode
      }
    }
  }
`;
