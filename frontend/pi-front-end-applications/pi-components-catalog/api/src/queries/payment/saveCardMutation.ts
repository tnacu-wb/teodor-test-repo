import { gql } from 'graphql-request';

export const SAVE_CARD_MUTATION = gql`
  mutation saveCard($initiateSaveCardRequest: InitiateSaveCardRequest!) {
    saveCard(initiateSaveCardRequest: $initiateSaveCardRequest) {
      paymentRedirect
      providerUrl
      template
      sessionId
    }
  }
`;
