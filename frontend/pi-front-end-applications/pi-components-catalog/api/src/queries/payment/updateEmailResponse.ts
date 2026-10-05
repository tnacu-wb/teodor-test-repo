import { gql } from 'graphql-request';

export const UPDATE_EMAIL = gql`
  mutation updateEmailMutation(
    $basketReference: String!
    $updateEmailCriteria: UpdateEmailCriteria!
  ) {
    updateEmail(basketReference: $basketReference, updateEmailCriteria: $updateEmailCriteria)
  }
`;
