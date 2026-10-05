import { gql } from 'graphql-request';

export const GET_COOKIE_CONSENT_INFO = gql`
  query getCookieConsentInfo($country: String!, $language: String!, $brand: String!) {
    cookieConsent(brand: $brand, country: $country, language: $language) {
      cookiePolicies {
        brand
        introView {
          acceptAllButtonText
          description
          manageButtonText
          title
          necessaryOnlyButtonText
        }
        manageView {
          alwaysActiveText
          description
          saveSettingsButtonText
          title
          cookieGroup {
            cookieName
            description
            isAlwaysActive
            title
            toggleLabel
          }
        }
        config {
          cookieOptOutExpiryDays
          cookieOptInExpiryDays
        }
      }
    }
  }
`;
