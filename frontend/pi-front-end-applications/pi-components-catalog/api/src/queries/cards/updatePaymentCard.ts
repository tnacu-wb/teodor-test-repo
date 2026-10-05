import { gql } from 'graphql-request';

export const updatePaymentCard = () => gql`
  mutation updatePaymentCard(
    $companyId: String!
    $cardId: String!
    $cardLabel: String!
    $cardType: String!
    $cardNumber: String!
    $expiryDate: String!
    $cardHolderName: String!
    $cardToken: String!
    $billingAddress: PaymentCardAddress!
    $cnpRequired: Boolean!
    $cnpBusinessAccountUsername: String
    $cnpBusinessAccountPassword: String
  ) {
    updatePaymentCard(
      companyId: $companyId
      cardId: $cardId
      updatePaymentCardRequest: {
        cardId: $cardId
        cardHolderName: $cardHolderName
        cardToken: $cardToken
        cardLabel: $cardLabel
        cardNumber: $cardNumber
        cardType: $cardType
        cnpBusinessAccountPassword: $cnpBusinessAccountPassword
        cnpRequired: $cnpRequired
        expiryDate: $expiryDate
        cnpBusinessAccountUsername: $cnpBusinessAccountUsername
        billingAddress: $billingAddress
      }
    )
  }
`;
