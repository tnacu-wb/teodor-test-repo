import { gql } from 'graphql-request';

export const getRegistrationQuestionsQuery = () => gql`
  query getEmployeeRegistrationQuestionsAndAnswers($companyId: String!, $employeeId: String!) {
    getEmployeeRegistrationQuestionsAndAnswers(companyId: $companyId, employeeId: $employeeId) {
      purchaseOrderManagement {
        questionId
        label
        mandatory
        managementHeader
        active
        location
        managementInformationAnswer {
          answerType
          answers
        }
        type
        positionId
        answer
      }
      customerReferenceManagement {
        questionId
        label
        mandatory
        managementHeader
        active
        location
        managementInformationAnswer {
          answerType
          answers
        }
        type
        positionId
        answer
      }
      userDefinedQuestions {
        questionId
        label
        mandatory
        managementHeader
        active
        location
        managementInformationAnswer {
          answerType
          answers
        }
        type
        positionId
        userDefinedAnswer
      }
    }
  }
`;
