import { gql } from 'graphql-request';

/**
 * Query used for card security check
 */
export const FRAUD_SECURITY_CHECK = gql`
  mutation securityCheck($basketReference: String!, $card: CardCcui!) {
    initiateSecurityCheck(
      basketReference: $basketReference
      securityCheckRequest: { card: $card }
    ) {
      fraudCheckDecision
      fraudCheckResult
    }
  }
`;
