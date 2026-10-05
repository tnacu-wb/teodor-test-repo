import { gql } from 'graphql-request';

export const resendActivationEmailQuery = () => gql`
  mutation ResendActivationEmail($sendActivationRequest: SendActivationRequest!) {
    resendActivationEmail(sendActivationRequest: $sendActivationRequest)
  }
`;
