import { gql } from 'graphql-request';

export const getAccountInfoQuery = () => gql`
  query getAccountInfo($scheme: Scheme!, $tetheredUserId: String!) {
    getAccountInfo(scheme: $scheme, tetheredUserId: $tetheredUserId) {
      billingFrequency
      daysToPay
      outStandingBalance {
        currencyCode
        value
      }
      statementValue {
        currencyCode
        value
      }
      status
    }
  }
`;
