import { gql } from 'graphql-request';

export const updateBookingAlertsQuery = gql`
  mutation UpdateBookingAlerts($companyId: String!, $bookingAlerts: BookingAlertsCriteria!) {
    updateBookingAlerts(companyId: $companyId, bookingAlerts: $bookingAlerts)
  }
`;
