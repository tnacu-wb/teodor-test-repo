import { gql } from 'graphql-request';

export const getPaymentCards = () => gql`
  query getPaymentCards($companyId: String!) {
    getPaymentCards(companyId: $companyId) {
      cardId
      cardLabel
      cardNumber
      cardHolderName
      expiryDate
      cardType
    }
  }
`;
