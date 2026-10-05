import { gql } from 'graphql-request';

export const updateBookingAllowancesQuery = () => gql`
  mutation updateBookingAllowances(
    $companyId: String!
    $bookingAllowances: BookingAllowancesCriteria!
  ) {
    updateBookingAllowances(companyId: $companyId, bookingAllowances: $bookingAllowances)
  }
`;
