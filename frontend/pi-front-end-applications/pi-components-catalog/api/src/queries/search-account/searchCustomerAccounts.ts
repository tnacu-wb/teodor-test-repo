import { gql } from 'graphql-request';

export const SEARCH_CUSTOMER_ACCOUNTS = gql`
  query SearchCustomerAccounts(
    $firstName: String
    $address: String
    $companyName: String
    $email: String
    $landline: String
    $lastName: String
    $mobile: String
    $postCode: String
  ) {
    customerAccounts(
      customerAccountSearchCriteria: {
        firstName: $firstName
        address: $address
        companyName: $companyName
        email: $email
        landline: $landline
        lastName: $lastName
        mobile: $mobile
        postCode: $postCode
      }
    ) {
      title
      companyName
      firstName
      companyPostalCode
      customerAccountId
      email
      homePostalCode
      landline
      lastName
      mobile
    }
  }
`;
