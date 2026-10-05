import { gql } from 'graphql-request';

export const getEmployeesQuery = () => gql`
  query getEmployeesV2(
    $companyId: String!
    $searchCriteria: String
    $bookingChannel: String
    $awaitingApproval: Boolean
    $size: Int!
    $page: Int
    $pageToken: String
  ) {
    getEmployeesV2(
      companyId: $companyId
      searchCriteria: $searchCriteria
      bookingChannel: $bookingChannel
      awaitingApproval: $awaitingApproval
      size: $size
      page: $page
      pageToken: $pageToken
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
