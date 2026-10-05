import { gql } from 'graphql-request';

export const SEARCH_A2C_COMPANIES = gql`
  query searchCompanies(
    $companyName: String!
    $arNumber: String!
    $hotelId: String!
    $limit: Int!
  ) {
    searchCompanies(
      searchCompaniesCriteria: {
        companyName: $companyName
        arNumber: $arNumber
        hotelId: $hotelId
        limit: $limit
      }
    ) {
      totalResults
      hasMore
      companies {
        name
        address {
          addressLine1
          addressLine2
          addressLine3
          addressLine4
          country
          postalCode
        }
        telephoneNumber
        corpId
        companyId
        arNumber
        profileType
        language
        active
        restricted
        restrictedReason
      }
    }
  }
`;
