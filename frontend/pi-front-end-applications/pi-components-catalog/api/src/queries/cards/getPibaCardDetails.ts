import { gql } from 'graphql-request';

export const getPibaCardDetailsQuery = () => gql`
  query getPIBACardDetails($tetheredUserId: String!, $countryCode: String!, $cardId: String!) {
    getPIBACardDetails(
      tetheredUserId: $tetheredUserId
      countryCode: $countryCode
      cardId: $cardId
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
