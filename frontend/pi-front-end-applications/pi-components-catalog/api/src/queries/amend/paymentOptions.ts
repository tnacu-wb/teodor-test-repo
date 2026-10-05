import { gql } from 'graphql-request';

export const AMEND_PAYMENT_OPTIONS = gql`
  query GetPaymentOptions(
    $bookingChannel: BookingChannelCriteria!
    $originalBookingRef: String!
    $tempBookingRef: String!
    $token: String!
    $country: String!
  ) {
    paymentOptions(
      paymentOptionsCriteria: {
        bookingChannel: $bookingChannel
        originalBookingRef: $originalBookingRef
        tempBookingRef: $tempBookingRef
        token: $token
        country: $country
      }
    ) {
      discount
      paymentOption {
        payNow
        payOnArrival
      }
      paymentType
      cardPresent {
        display
        value
      }
      eckoh {
        display
        enabled
      }
      cardHolderName {
        display
        name
        surname
      }
      billingAddress {
        display
      }
      emailPreference {
        display
        send
        emailAddress
      }
      allowances {
        display
        values {
          allowance
          budget
        }
      }
      purchaseOrder {
        display
        value
      }
      companyRef {
        display
        value
      }
      a2cDetails {
        display
        number
        name
        address
        postcode
      }
      preAuthCharges {
        display
        charges
      }
      hotelName
      hotelCode
      brand
      companyId
    }
  }
`;
