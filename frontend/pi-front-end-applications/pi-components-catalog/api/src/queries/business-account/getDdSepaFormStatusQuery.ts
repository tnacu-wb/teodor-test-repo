import { gql } from 'graphql-request';

export const getDdSepaFormStatusQuery = () => gql`
  query GetDdSepaFormStatus($hostedPageGuid: String!, $scheme: Scheme!) {
    getDdSepaFormStatus(hostedPageGuid: $hostedPageGuid, scheme: $scheme) {
      status
    }
  }
`;
