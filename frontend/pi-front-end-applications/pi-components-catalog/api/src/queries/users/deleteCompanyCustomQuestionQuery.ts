import { gql } from 'graphql-request';

export const deleteCompanyCustomQuestionQuery = () => gql`
  mutation deleteCustomQuestion($companyId: String!, $questionId: String!) {
    deleteCustomQuestion(companyId: $companyId, questionId: $questionId)
  }
`;
