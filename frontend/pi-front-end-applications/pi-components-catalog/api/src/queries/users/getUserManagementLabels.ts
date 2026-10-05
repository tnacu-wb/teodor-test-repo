import { gql } from 'graphql-request';

export const getUserManagementLabels = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      userManagementEndpoint
    }
  }
`;
