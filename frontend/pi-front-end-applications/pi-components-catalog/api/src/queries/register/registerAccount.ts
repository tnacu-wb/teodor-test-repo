import { gql } from 'graphql-request';

export const REGISTER_USER_ACCOUNT = gql`
  mutation createAccount(
    $country: String
    $language: String
    $captcha: String
    $password: String!
    $companyName: String
    $cityName: String
    $emailAddress: String!
    $firstName: String!
    $lastName: String!
    $title: String
    $addressType: String
    $postalCode: String
    $mobile: String!
    $addressLine1: String
    $addressLine2: String
    $addressLine3: String
    $addressLine4: String
    $countryCode: String
    $basketReference: String
    $updatePreferencesRequest: UpdatePreferencesRequestV2
  ) {
    createAccount(
      createAccountRequest: {
        country: $country
        language: $language
        captcha: $captcha
        password: $password
        contactDetail: {
          title: $title
          firstName: $firstName
          lastName: $lastName
          emailAddress: $emailAddress
          mobile: $mobile
          address: {
            companyName: $companyName
            addressLine1: $addressLine1
            addressLine2: $addressLine2
            addressLine3: $addressLine3
            addressLine4: $addressLine4
            addressType: $addressType
            countryCode: $countryCode
            postalCode: $postalCode
            cityName: $cityName
          }
        }
        basketReference: $basketReference
        updatePreferencesRequest: $updatePreferencesRequest
      }
    ) {
      success
      customerId
    }
  }
`;
