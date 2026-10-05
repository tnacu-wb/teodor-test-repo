import { gql } from 'graphql-request';

export const directDebitMutation = () => gql`
  mutation DirectDebit($directDebitRequest: DirectDebitRequest!) {
    directDebit(directDebitRequest: $directDebitRequest) {
      hostedPageGuid
    }
  }
`;
