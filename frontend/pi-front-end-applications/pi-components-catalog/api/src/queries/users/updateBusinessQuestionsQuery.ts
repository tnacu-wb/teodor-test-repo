import { gql } from 'graphql-request';

export const updateBusinessQuestionsQuery = () => gql`
  mutation updateBusinessQuestions(
    $companyId: String!
    $questionId: String!
    $userQuestion: ManagementInformationQuestionCriteria!
  ) {
    updateBusinessQuestions(
      companyId: $companyId
      questionId: $questionId
      userQuestion: $userQuestion
    )
  }
`;
