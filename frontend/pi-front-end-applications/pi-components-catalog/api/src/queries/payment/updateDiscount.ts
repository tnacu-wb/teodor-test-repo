import { gql } from 'graphql-request';

export const UPDATE_DISCOUNT = gql`
  mutation updateDiscountMutation($basketReference: String!, $discountAmount: Float) {
    updateDiscount(
      updateDiscountRequest: { basketReference: $basketReference, discountAmount: $discountAmount }
    )
  }
`;
