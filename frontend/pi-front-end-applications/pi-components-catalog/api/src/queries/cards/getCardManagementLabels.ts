import { gql } from 'graphql-request';

export const getCardManagementLabels = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      cardManagementEndpoint
    }
  }
`;
