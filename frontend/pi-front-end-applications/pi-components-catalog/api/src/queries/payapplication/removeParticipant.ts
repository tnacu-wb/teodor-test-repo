import { gql } from 'graphql-request';

export const REMOVE_PARTICIPANT = gql`
  mutation RemoveParticipant($removeParticipantRequest: RemoveParticipantRequest!) {
    removeParticipant(removeParticipantRequest: $removeParticipantRequest)
  }
`;
