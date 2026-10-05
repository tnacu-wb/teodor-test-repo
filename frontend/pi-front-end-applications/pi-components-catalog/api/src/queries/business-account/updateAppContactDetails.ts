import { gql } from 'graphql-request';

export const updateAppContactDetailsQuery = () => gql`
  mutation updateAppContactDetails(
    $updateAppContactDetailsCriteria: UpdateAppContactDetailsCriteria!
  ) {
    updateAppContactDetails(updateAppContactDetailsCriteria: $updateAppContactDetailsCriteria) {
      status
      message
    }
  }
`;
