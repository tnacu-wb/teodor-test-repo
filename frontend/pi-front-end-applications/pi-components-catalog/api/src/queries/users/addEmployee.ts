import { gql } from 'graphql-request';

export const addEmployee = () => gql`
  mutation addEmployee($companyId: String!, $languageCode: String, $employee: EmployeeCriteria!) {
    addEmployee(companyId: $companyId, languageCode: $languageCode, employee: $employee)
  }
`;
