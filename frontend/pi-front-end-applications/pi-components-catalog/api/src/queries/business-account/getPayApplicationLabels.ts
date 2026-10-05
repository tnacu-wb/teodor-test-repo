import { gql } from 'graphql-request';

export const getPayApplicationLabelsQuery = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      payApplicationEndpoint
    }
  }
`;
