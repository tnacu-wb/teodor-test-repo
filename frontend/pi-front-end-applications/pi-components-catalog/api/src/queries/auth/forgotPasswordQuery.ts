import { gql } from 'graphql-request';

export const forgotPasswordQuery = () => gql`
  mutation forgotPassword(
    $language: String
    $innBusiness: Boolean
    $forgottenPasswordRequest: ForgottenPasswordRequest!
  ) {
    forgotPassword(
      language: $language
      innBusiness: $innBusiness
      forgottenPasswordRequest: $forgottenPasswordRequest
    ) {
      success
    }
  }
`;
