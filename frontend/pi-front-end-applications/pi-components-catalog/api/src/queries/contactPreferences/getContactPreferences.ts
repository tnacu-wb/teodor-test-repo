import { gql } from 'graphql-request';

export const GET_CONTACT_PREFERENCES = gql`
  query getContactPreferences($request: PreferencesGetRequest!) {
    getContactPreferences(request: $request) {
      permissions {
        brandCode
        brand
        optIn
        secondOptInReq
        secondOptIn
        secondPartyOptIn
        thirdPartyVendorsOptIn
        suppressMarketingCheckbox
      }
      loyaltyAccounts {
        loyaltyBrand
        loyaltySystemId
      }
      valid
      deleted
      contactChannelId
      contactChannelValue
    }
  }
`;
