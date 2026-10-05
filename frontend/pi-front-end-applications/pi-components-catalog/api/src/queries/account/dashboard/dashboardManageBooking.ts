import { gql } from 'graphql-request';

export const DASHBOARD_MANAGE_BOOKING = gql`
  query manageBooking($cancelInformationCriteria: CancelInformationCriteria!) {
    manageBooking(cancelInformationCriteria: $cancelInformationCriteria) {
      isCancellable
      isAmendable
      isRuleCompliant
      aemLabelKey
    }
  }
`;
