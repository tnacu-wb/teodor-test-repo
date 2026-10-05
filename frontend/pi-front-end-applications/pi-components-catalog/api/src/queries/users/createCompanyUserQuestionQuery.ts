import { gql } from 'graphql-request';

export const createCompanyUserQuestionQuery = () => gql`
  mutation createCompanyUserQuestion(
    $companyId: String!
    $userQuestion: ManagementInformationQuestionCriteria!
  ) {
    createCompanyUserQuestion(companyId: $companyId, userQuestion: $userQuestion)
  }
`;
