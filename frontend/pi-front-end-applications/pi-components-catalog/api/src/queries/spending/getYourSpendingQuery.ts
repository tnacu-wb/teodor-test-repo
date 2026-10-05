import { gql } from 'graphql-request';

export const getYourSpendingQuery = () => gql`
  query EmployeeSpendQuery($employeeSpendCriteria: EmployeeSpendCriteria!) {
    getEmployeeSpend(employeeSpendCriteria: $employeeSpendCriteria) {
      employeeSpendDtoList {
        companyAccountId
        employeeAccountId
        year
        month
        noOfBookings
        bookingValue
        bookingCurrency
      }
    }
  }
`;
