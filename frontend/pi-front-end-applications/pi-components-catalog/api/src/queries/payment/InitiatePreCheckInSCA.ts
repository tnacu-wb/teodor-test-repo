import { gql } from 'graphql-request';

export const INITIATE_PRE_CHECKIN_SCA = gql`
  mutation InitiatePreCheckInSCA($requestId: String!, $environment: String!, $language: String!) {
    authorizeCard(
      initiateAuthorizeScaRequest: {
        requestId: $requestId
        environment: $environment
        language: $language
      }
    ) {
      paymentRedirect
      template
      sessionId
      providerUrl
    }
  }
`;
