import { gql } from 'graphql-request';

export const GET_PAYMENT_METHODS_QUERY = gql`
  query getPaymentMethods(
    $basketReference: String!
    $language: String!
    $country: String!
    $userType: UserType
    $clientChannel: String
  ) {
    paymentMethods(
      paymentMethodsCriteria: {
        basketReference: $basketReference
        language: $language
        country: $country
        userType: $userType
        clientChannel: $clientChannel
      }
    ) {
      name
      type
      subType
      order
      enabled
      cnpPreSelected
      cnpOptionAvailable
      clientId
      clientToken
      logoSrc

      acceptedCardTypes {
        type
        name
        logoSrc
      }

      card {
        token
        # cardMasked
        cardName
        cardNumber
        expiryMonth
        expiryYear
        type
        logoSrc
        cardHolderName
        cardType
        cnpRequired
      }

      paymentOptions {
        type
        order
        enabled
      }
      reasons
    }
  }
`;

export const GET_PAYMENT_METHODS_QUERY_BB = gql`
  query getPaymentMethods(
    $basketReference: String!
    $language: String!
    $country: String!
    $userType: UserType
    $clientChannel: String
  ) {
    paymentMethods(
      paymentMethodsCriteria: {
        basketReference: $basketReference
        language: $language
        country: $country
        userType: $userType
        clientChannel: $clientChannel
      }
    ) {
      name
      type
      subType
      order
      enabled
      cnpPreSelected
      cnpOptionAvailable
      logoSrc
      clientId
      clientToken

      acceptedCardTypes {
        type
        name
        logoSrc
      }

      card {
        token
        # cardMasked
        cardNumber
        expiryMonth
        expiryYear
        type
        logoSrc
        cardHolderName
        cardType
        cnpRequired
      }

      paymentOptions {
        type
        order
        enabled
      }
      reasons
      bookingAllowances {
        allowAlcohol
        allowCarParking
        allowAdditionalCosts
        maxDinnerBudgets {
          ukWide {
            amount
            currency
          }
          greaterLondon {
            amount
            currency
          }
          ireland {
            amount
            currency
          }
        }
      }
    }
  }
`;

export const GET_PAYMENT_TYPE_OPTS_QUERY_CCUI = `
  query getPaymentMethodsCCUI($basketReference: String!, $language: String!, $country: String!, $changePaymentBIC: Boolean) {
    paymentCcuiMethods(
      paymentCcuiMethodsCriteria: {
        basketReference: $basketReference
        language: $language
        country: $country
        changePaymentBIC: $changePaymentBIC
      }
    ) {
      acceptedCardTypes {
        logoSrc
        name
        type
      }
      cnpOptionAvailable
      cnpPreSelected
      enabled
      name
      subType
      order
      paymentOptions {
        enabled
        order
        type
      }
      reasons
      type
    }
  }
`;
