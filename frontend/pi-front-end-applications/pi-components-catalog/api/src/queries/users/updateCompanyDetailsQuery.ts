import { gql } from 'graphql-request';

export const updateCompanyDetailsQuery = () => gql`
  mutation updateCompanyDetails($companyId: String!, $companySummary: CompanySummary!) {
    updateCompanyDetails(companyId: $companyId, companySummary: $companySummary)
  }
`;
