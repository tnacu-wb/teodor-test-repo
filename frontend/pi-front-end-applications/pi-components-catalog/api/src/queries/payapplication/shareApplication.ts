import { gql } from 'graphql-request';

export const SHARE_APPLICATION = gql`
  mutation ShareApplication($shareApplicationRequest: ShareApplicationRequest!) {
    shareApplication(shareApplicationRequest: $shareApplicationRequest) {
      message
      status
    }
  }
`;
