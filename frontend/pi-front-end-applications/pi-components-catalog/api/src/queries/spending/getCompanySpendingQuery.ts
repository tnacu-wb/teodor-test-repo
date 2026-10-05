import { gql } from 'graphql-request';

export const getCompanySpendingQuery = () => gql`
  query getCompanySpending($fromMonthYear: String!, $toMonthYear: String!) {
    getCompanySpending(
      searchCompanySpending: { fromMonthYear: $fromMonthYear, toMonthYear: $toMonthYear }
    ) {
      companySpendingDtoList {
        bookingValue
        companyAccountId
        month
        noOfBookings
        year
        bookingCurrency
      }
    }
  }
`;
