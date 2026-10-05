import { gql } from 'graphql-request';

export const GET_FORMATTED_ADDRESS = gql`
  query formattedAddress($identifier: String!) {
    formattedAddress(identifier: $identifier) {
      companyName
      addressLine1
      addressLine2
      addressLine3
      addressLine4
      companyName
      label
      postalCode
      country
    }
  }
`;
