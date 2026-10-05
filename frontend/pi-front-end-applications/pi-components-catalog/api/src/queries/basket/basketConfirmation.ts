import { gql } from 'graphql-request';

export const BASKET_CONFIRMATION_MUTATION = gql`
  mutation basketConfirmationMutation($basketReference: String!) {
    basketConfirmation(basketReference: $basketReference) {
      reference
    }
  }
`;
