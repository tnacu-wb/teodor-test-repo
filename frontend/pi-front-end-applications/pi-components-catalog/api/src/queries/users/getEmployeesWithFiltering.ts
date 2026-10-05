import { gql } from 'graphql-request';

export const getEmployeesWithFilteringOptionsQuery = () => gql`
  query getEmployeesWithFilteringOptionsV2(
    $companyId: String!
    $searchCriteria: String
    $bookingChannel: String
    $awaitingApproval: Boolean
    $size: Int!
    $page: Int
    $pageToken: String
    $shouldFilterEmployees: Boolean
  ) {
    getEmployeesWithFilteringOptionsV2(
      companyId: $companyId
      searchCriteria: $searchCriteria
      bookingChannel: $bookingChannel
      awaitingApproval: $awaitingApproval
      size: $size
      page: $page
      pageToken: $pageToken
      shouldFilterEmployees: $shouldFilterEmployees
    ) {
      success
      employees {
        id
        employeeId
        title
        firstName
        lastName
        emailAddress
        accessLevel
        employeeStatus
      }
      pageToken
    }
  }
`;
