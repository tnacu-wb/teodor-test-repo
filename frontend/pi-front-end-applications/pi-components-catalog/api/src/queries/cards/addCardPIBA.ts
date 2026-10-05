import { gql } from 'graphql-request';

export const addCardPIBA = () => gql`
  mutation addInnBPIBACard(
    $tetheredUserGuid: String!
    $countryCode: String!
    $addInnBPIBACardCriteria: AddInnBPIBACardCriteria!
  ) {
    addInnBPIBACard(
      tetheredUserGuid: $tetheredUserGuid
      countryCode: $countryCode
      addInnBPIBACardCriteria: $addInnBPIBACardCriteria
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
