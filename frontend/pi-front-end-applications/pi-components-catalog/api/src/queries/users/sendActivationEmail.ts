import { gql } from 'graphql-request';

export const SEND_ACTIVATION_EMAIL = gql`
  mutation sendActivationEmail(
    $companyId: String!
    $languageCode: String
    $sendActivationEmailCriteria: SendActivationEmailCriteria!
  ) {
    sendActivationEmail(
      companyId: $companyId
      languageCode: $languageCode
      sendActivationEmailCriteria: $sendActivationEmailCriteria
    )
  }
`;
