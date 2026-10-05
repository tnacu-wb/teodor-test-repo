import { gql } from 'graphql-request';

export const addPayAppCardMutation = () => gql`
  mutation AddApplicationCard($addApplicationCardCriteria: AddApplicationCardRequest!) {
    addApplicationCard(addApplicationCardCriteria: $addApplicationCardCriteria) {
      cardGuid
    }
  }
`;
