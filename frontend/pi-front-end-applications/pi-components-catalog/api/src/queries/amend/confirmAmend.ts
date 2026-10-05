import { gql } from 'graphql-request';

export const CONFIRM_AMEND = gql`
  mutation ConfirmAmendLogic(
    $tempBookingRef: String!
    $originalBookingRef: String!
    $token: String!
    $channel: Channel!
    $subchannel: String!
    $language: String
    $paymentOptionSelected: String!
    $paymentOption: String
    $environment: String!
    $emailAddress: String
    $paymentRequest: PaymentCcuiRequest
    $ccuiExtraItems: CcuiExtraItems
    $preCheckIn: [String] = null
  ) {
    confirmAmendLogic(
      confirmAmendLogicCriteria: {
        originalBookingRef: $originalBookingRef
        tempBookingRef: $tempBookingRef
        token: $token
        bookingChannel: { channel: $channel, subchannel: $subchannel, language: $language }
        paymentOptionSelected: $paymentOptionSelected
        paymentOption: $paymentOption
        environment: $environment
        emailAddress: $emailAddress
        paymentRequest: $paymentRequest
        ccuiExtraItems: $ccuiExtraItems
        preCheckIn: $preCheckIn
      }
    ) {
      payment {
        status
        paymentRequiredDetails {
          paymentRedirect
          sessionId
          template
        }
      }
    }
  }
`;
