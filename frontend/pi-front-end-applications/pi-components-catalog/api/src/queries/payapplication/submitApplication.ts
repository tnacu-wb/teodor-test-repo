import { gql } from 'graphql-request';

export const submitApplicationMutation = gql`
  mutation SubmitApplication($submitApplicationRequest: SubmitApplicationRequest!) {
    submitApplicationV1(submitApplicationRequest: $submitApplicationRequest) {
      message
    }
  }
`;
