import { gql } from 'graphql-request';

export const updateEmployee = () => gql`
  mutation updateEmployee(
    $companyId: String!
    $employeeId: String!
    $languageCode: String
    $updateEmployeeCriteria: EmployeeCriteria!
    $activationKey: String
  ) {
    updateEmployee(
      companyId: $companyId
      employeeId: $employeeId
      languageCode: $languageCode
      updateEmployeeCriteria: $updateEmployeeCriteria
      activationKey: $activationKey
    )
  }
`;
