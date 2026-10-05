import { gql } from 'graphql-request';

export const resetPasswordQuery = () => gql`
  mutation ResetPassword($payload: ResetPasswordRequest!) {
    resetPassword(payload: $payload) {
      passwordChanged
    }
  }
`;
