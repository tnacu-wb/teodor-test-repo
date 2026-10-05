import { gql } from 'graphql-request';

export const CCUI_INITIATE_PAYMENT_PROCESS = gql`
  mutation paymentProcess(
    $basketReference: String!
    $paymentOption: String!
    $subPaymentType: String
    $sendMail: Boolean
    $cardPresent: Boolean
    $companyNumber: String
    $companyId: String
    $charges: String
    $typeOfCaller: String
    $addressCompanyName: String
    $businessItems: BusinessItems
    $requestId: String!
    $type: String!
    $subType: String!
    $firstName: String!
    $lastName: String!
    $title: String!
    $email: String!
    $telephone: String
    $differentBillingAddress: Boolean
    $addressLine1: String!
    $addressLine2: String
    $addressLine3: String
    $cityName: String!
    $postalCode: String!
    $companyName: String
    $addressType: String
    $country: String!
    $bookingType: String!
    $journey: String!
    $channel: String!
    $language: String!
    $identifier: String!
    $bookingBusinessSiteType: String!
    $name: String!
    $location: String!
    $card: CardCcui!
    $bookerIsNotGuest: Boolean
  ) {
    initiateCcuiPayment(
      basketReference: $basketReference
      initiateCcuiPaymentCriteria: {
        paymentOption: $paymentOption
        subPaymentType: $subPaymentType
        ccuiExtraItems: {
          sendMail: $sendMail
          cardPresent: $cardPresent
          accountCompanyItems: {
            companyNumber: $companyNumber
            companyId: $companyId
            charges: $charges
          }
          nonguaranteedItems: { typeOfCaller: $typeOfCaller }
          addressCompanyName: $addressCompanyName
          businessItems: $businessItems
        }
        paymentRequest: {
          requestId: $requestId
          payment: {
            type: $type
            subType: $subType
            card: $card
            billing: {
              firstName: $firstName
              lastName: $lastName
              title: $title
              email: $email
              telephone: $telephone
              differentBillingAddress: $differentBillingAddress
              bookerIsNotGuest: $bookerIsNotGuest
              address: {
                addressLine1: $addressLine1
                addressLine2: $addressLine2
                addressLine3: $addressLine3
                cityName: $cityName
                postalCode: $postalCode
                country: $country
                companyName: $companyName
                addressType: $addressType
              }
            }
          }
          booking: {
            type: $bookingType
            journey: $journey
            channel: $channel
            language: $language
            businessSite: {
              identifier: $identifier
              type: $bookingBusinessSiteType
              name: $name
              location: $location
            }
          }
        }
      }
    ) {
      status
    }
  }
`;
