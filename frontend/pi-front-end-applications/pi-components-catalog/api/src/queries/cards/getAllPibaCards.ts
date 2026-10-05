import { gql } from 'graphql-request';

export const getAllPibaCards = () => gql`
  query getAllPIBACards(
    $tetheredUserId: String!
    $countryCode: String!
    $pibaCardsCriteria: PIBACardsCriteria!
  ) {
    getAllPIBACards(
      tetheredUserId: $tetheredUserId
      countryCode: $countryCode
      pibaCardsCriteria: $pibaCardsCriteria
    ) {
      innBusinessPayCardList {
        myCard
        cardId
        cardHolderName
        cardRegistration
        cardNumber
        cardStatus
        cardRegistrationCount
        isActivated
      }
      from
      to
      totalCount
      lastPage
    }
  }
`;
