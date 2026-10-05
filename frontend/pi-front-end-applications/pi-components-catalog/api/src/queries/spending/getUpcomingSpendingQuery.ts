import { gql } from 'graphql-request';

export const getUpcomingSpendingQuery = () => gql`
  query GetAccountUpcomingSpending($accountId: String!, $tetheredUserGuid: String) {
    getAccountUpcomingSpending(accountId: $accountId, tetheredUserGuid: $tetheredUserGuid) {
      expectedSpendTodayDate
      expectedSpendToday
      expectedNextBillingStartDate
      expectedNextBillingEndDate
      expectedNextBilling
      expectedNextPeriodStartDate
      expectedNextPeriodEndDate
      expectedNextPeriod
      currency
      accountStatus
    }
  }
`;
