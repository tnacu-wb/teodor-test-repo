import { gql } from 'graphql-request';

export const REGISTER_USER_MARKETING = gql`
  mutation updateMarketingPreferences(
    $brandCodes: [String!]
    $optIn: Boolean!
    $doubleOptIn: Boolean!
    $language: String
    $title: String
    $customerId: String
    $countryOfResidence: String
    $firstName: String
    $lastName: String
    $channel: String!
    $journey: String
    $locale: String
  ) {
    updateMarketingPreferences(
      updateMarketingPreferencesRequest: {
        brandCodes: $brandCodes
        optIn: $optIn
        doubleOptIn: $doubleOptIn
        customer: {
          title: $title
          firstName: $firstName
          lastName: $lastName
          customerId: $customerId
          countryOfResidence: $countryOfResidence
          language: $language
        }
        sourceDetails: { channel: $channel, journey: $journey, locale: $locale }
      }
    )
  }
`;
