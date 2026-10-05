import { gql } from 'graphql-request';

export const updateCompanyUserQuestionQuery = () => gql`
  mutation updateCompanyUserQuestion(
    $companyId: String!
    $questionId: String!
    $userQuestion: ManagementInformationQuestionCriteria!
  ) {
    updateCompanyUserQuestion(
      companyId: $companyId
      questionId: $questionId
      userQuestion: $userQuestion
    )
  }
`;
