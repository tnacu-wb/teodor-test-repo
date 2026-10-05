import { gql } from 'graphql-request';

export const getNotificationsQuery = () => gql`
  query getNotifications($tetheredUserId: String, $scheme: Scheme!) {
    getNotifications(tetheredUserId: $tetheredUserId, scheme: $scheme) {
      status
      profileUpdateRequired
      usersAwaitingApproval
    }
  }
`;
