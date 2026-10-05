import { gql } from 'graphql-request';

export const getAccountSpendingQuery = () => gql`
  query AccountSpendingDtoList($accountSpendingCriteria: AccountSpendingCriteria!) {
    getAccountSpending(accountSpendingCriteria: $accountSpendingCriteria) {
      accountSpendingDtoList {
        bookingValue
        month
        noOfBookings
        year
        pibaAccountId
      }
    }
  }
`;
