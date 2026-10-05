import { gql } from 'graphql-request';

export const GET_COUNTRIES = gql`
  query getCountries($country: String!, $language: String!, $site: String!) {
    countries(country: $country, language: $language, site: $site) {
      countries {
        countryCode
        countryCodeLegacy
        countryName
        dialingCode
        flagSrc
        passportRequired
        nationality
      }
    }
  }
`;
