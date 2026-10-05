import { gql } from 'graphql-request';

export const BUSINESS_TETHER_LOGIN = gql`
  mutation businessTetherLogin($loginCriteria: LoginCriteria!) {
    businessTetherLogin(loginCriteria: $loginCriteria) {
      errorCode
      errorDescription
      hash
      sessionId
      sharedSecret
      nonce
      timestamp
    }
  }
`;
