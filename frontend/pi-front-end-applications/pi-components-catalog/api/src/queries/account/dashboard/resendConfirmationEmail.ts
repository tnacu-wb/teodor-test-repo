import { gql } from 'graphql-request';

export const RESEND_CONFIRMATION_EMAIL = gql`
  mutation resendConfirmationEmail($resendConfirmationRequest: ResendConfirmationRequest) {
    resendConfirmationEmail(resendConfirmationRequest: $resendConfirmationRequest)
  }
`;
