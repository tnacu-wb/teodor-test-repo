import { gql } from 'graphql-request';

export const GET_ADDRESSES = gql`
  query addressSearch($searchTerm: String!, $countryCode: String) {
    partialAddress(partialAddressCriteria: { searchTerm: $searchTerm, countryCode: $countryCode }) {
      id
      addressText
    }
  }
`;
