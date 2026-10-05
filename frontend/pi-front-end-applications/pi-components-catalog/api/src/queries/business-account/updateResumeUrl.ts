import { gql } from 'graphql-request';

export const updateResumeUrlMutation = () => gql`
  mutation UpdateResumeUrl($updateResumeUrlRequest: UpdateResumeUrlRequest!) {
    updateResumeUrl(updateResumeUrlRequest: $updateResumeUrlRequest) {
      status
      message
    }
  }
`;
