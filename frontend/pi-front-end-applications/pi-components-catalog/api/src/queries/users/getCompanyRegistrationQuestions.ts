import { gql } from 'graphql-request';

export const getCompanyRegistrationQuestionsQuery = () => gql`
  query getCompanyRegistrationQuestionsAndAnswers($companyId: String!) {
    getCompanyRegistrationQuestionsAndAnswers(companyId: $companyId) {
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
