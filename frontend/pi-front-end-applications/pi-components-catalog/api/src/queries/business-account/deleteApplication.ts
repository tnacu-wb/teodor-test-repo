import { gql } from 'graphql-request';

export const deleteApplicationQuery = () => gql`
  mutation deleteApplication($applicationId: String!, $applicationGuid: String!, $scheme: Scheme) {
    deleteApplication(
      deleteApplicationRequest: {
        applicationId: $applicationId
        applicationGuid: $applicationGuid
        scheme: $scheme
      }
    ) {
      status
      message
    }
  }
`;
