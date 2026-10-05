import { gql } from 'graphql-request';

export const companyDetailsLookupQuery = () => gql`
  query companyDetailsLookup($companyRegistrationNumber: String!, $scheme: Scheme) {
    companyDetailsLookup(companyRegistrationNumber: $companyRegistrationNumber, scheme: $scheme) {
      data {
        address {
          addressLine1
          addressLine2
          addressLine3
          addressLine4
          postcode
          countryCode
        }
        companyName
        creditAgencyReference
      }
    }
  }
`;
