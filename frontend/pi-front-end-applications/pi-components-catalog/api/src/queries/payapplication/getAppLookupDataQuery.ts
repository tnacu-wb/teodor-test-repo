import { gql } from 'graphql-request';

export const getAppLookupDataQueryEN = gql`
  query GetAppLookupData($scheme: Scheme!) {
    getAppLookupData(scheme: $scheme) {
      title
      tradingStyle
      estimatedMonthlySpend
      hotelBrandPolicies
      hotelBookingRoles
      isoCountryCodes
      timeTrading
      industrySector
      registrationQuestions
      numberOfEmployees
      cancellationReason
    }
  }
`;

export const getAppLookupDataQueryDE = gql`
  query GetAppLookupData($scheme: Scheme!) {
    getAppLookupData(scheme: $scheme) {
      title
      tradingStyle
      estimatedMonthlySpend
      timeTrading
      industrySector
      numberOfEmployees
      cancellationReason
    }
  }
`;
