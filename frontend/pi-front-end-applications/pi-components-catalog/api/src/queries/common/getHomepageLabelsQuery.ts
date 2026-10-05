import { gql } from 'graphql-request';

export const getHomepageLabelsQuery = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      homepageEndpoint
    }
  }
`;
