import { gql } from 'graphql-request';

export const getAuthLabelsQuery = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      authEndpoint
    }
  }
`;
