import { gql } from 'graphql-request';

export const getInnBusinessLayoutLabels = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      layoutEndpoint
    }
  }
`;
