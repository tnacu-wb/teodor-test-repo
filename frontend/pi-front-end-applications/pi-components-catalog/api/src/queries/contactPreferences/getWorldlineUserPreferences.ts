import { gql } from 'graphql-request';

export const WORLDLINE_USER_PREFERENCES_QUERY = gql`
  query GetWorldlineUserPreferences($tetheredUserGuids: [String!]!) {
    getWorldlineUserPreferences(tetheredUserGuids: $tetheredUserGuids) {
      tetheredUserGuid
      settings {
        smsTypeId
        isSmsSelected
        smsTypeDescription
        smsType
      }
      details {
        showSmsStopsToCardholder
        sendCardsToCardholder
        accountName
        roleId
        accountNumber
        roleDescription
      }
    }
  }
`;
