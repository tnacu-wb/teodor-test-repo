import { gql } from 'graphql-request';

export const getInnBusinessCommonIcons = () => gql`
  query getPageData($country: String!, $language: String!) {
    getPageData(country: $country, language: $language) {
      commonIconsEndpoint
    }
  }
`;
