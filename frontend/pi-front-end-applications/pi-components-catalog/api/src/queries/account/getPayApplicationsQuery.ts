import { gql } from 'graphql-request';

export const getPayApplicationsQuery = () => gql`
  query getPayApplications {
    getPayApplications {
      applications {
        status
        accountName
        applicationGuid
        applicationId
        resumeUrl
        scheme
      }
    }
  }
`;
