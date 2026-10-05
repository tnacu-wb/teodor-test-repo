import { gql } from 'graphql-request';

export const GET_ECKOH_STATUS = gql`
  query GetEckohStatus($basketReference: String!) {
    eckohRecordingStatus(basketReference: $basketReference) {
      status
    }
  }
`;
