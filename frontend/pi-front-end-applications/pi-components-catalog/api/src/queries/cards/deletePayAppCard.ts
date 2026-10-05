import { gql } from 'graphql-request';

export const deletePayAppCardMutation = () => gql`
  mutation DeleteApplicationCard($deleteApplicationCardRequest: DeleteApplicationCardRequest!) {
    deleteApplicationCard(deleteApplicationCardRequest: $deleteApplicationCardRequest)
  }
`;
