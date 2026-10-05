import { gql } from 'graphql-request';

export const appPreCheckQuery = () => gql`
  query AppPreCheck($scheme: Scheme!) {
    appPreCheck(scheme: $scheme) {
      isTetheredUser
      applicationGuid
    }
  }
`;
