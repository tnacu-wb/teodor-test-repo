import { gql } from 'graphql-request';

export const GET_ANONYMOUS_NEWSLETTER_PREFERENCES = gql`
  query getAnonymousNewsletterPreferences(
    $brandCode: String!
    $email: String!
    $countryOfResidence: String
    $language: String
  ) {
    anonymousNewsletterPreferences(
      brandCode: $brandCode
      email: $email
      countryOfResidence: $countryOfResidence
      language: $language
    ) {
      optIn
      secondOptIn
      secondOptInReq
      secondPartyOptIn
      thirdPartyVendorsOptIn
      suppressMarketingCheckbox
    }
  }
`;
