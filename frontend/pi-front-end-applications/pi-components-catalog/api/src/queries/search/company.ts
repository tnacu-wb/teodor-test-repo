import { gql } from 'graphql-request';

export const SEARCH_COMPANIES = gql`
  query searchCompanies(
    $searchTerm: String!
    $negotiatedRateCompanies: Boolean!
    $limit: Int!
    $offset: Int!
  ) {
    searchCompanies(
      searchCompaniesCriteria: {
        companyName: $searchTerm
        negotiatedRateCompanies: $negotiatedRateCompanies
        limit: $limit
        offset: $offset
      }
    ) {
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
        profileType
        language
        active
        negotiatedRateEnabled
      }
    }
  }
`;

export const SEARCH_COMPANY_BY_ID_OR_CORP_ID = gql`
  query searchCompanyByIdOrCorpId($id: String!) {
    companyProfileById(id: $id) {
      active
      arNumber
      companyId
      corpId
      language
      name
      negotiatedRateEnabled
      profileType
      restricted
      restrictedReason
      telephoneNumber
      address {
        addressLine1
        addressLine2
        addressLine3
        addressLine4
        country
        postalCode
      }
    }
  }
`;

export const SEARCH_COMPANY_BY_ID = gql`
  query searchCompanyById($corpId: String!) {
    companyProfile(companyId: $corpId) {
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
      profileType
      language
      active
      negotiatedRateEnabled
    }
  }
`;
