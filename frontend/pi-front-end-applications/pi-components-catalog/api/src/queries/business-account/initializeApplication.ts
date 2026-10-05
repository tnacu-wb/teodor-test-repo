import { gql } from 'graphql-request';

export const initializeApplicationQuery = () => gql`
  mutation initializeApplication(
    $email: String!
    $scheme: Scheme
    $campaignCode: String
    $incentiveCode: String
  ) {
    initializeApplication(
      initializeApplicationRequest: {
        email: $email
        scheme: $scheme
        campaignCode: $campaignCode
        incentiveCode: $incentiveCode
      }
    ) {
      applicationGUID
      applicationId
    }
  }
`;
