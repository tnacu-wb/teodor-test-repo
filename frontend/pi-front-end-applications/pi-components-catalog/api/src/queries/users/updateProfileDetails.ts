import { gql } from 'graphql-request';

export const updateProfileDetailsQuery = () => gql`
  mutation updateProfileDetails(
    $customerId: String!
    $business: Boolean
    $payload: CustomerRequest!
    $innBusiness: Boolean
  ) {
    updateProfileDetails(
      customerId: $customerId
      business: $business
      innBusiness: $innBusiness
      payload: $payload
    ) {
      success
      customerId
    }
  }
`;
