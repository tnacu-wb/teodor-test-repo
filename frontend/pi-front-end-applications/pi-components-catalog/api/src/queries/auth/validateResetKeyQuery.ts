import { gql } from 'graphql-request';

export const validateResetKeyQuery = () => gql`
  mutation validateResetKey($validateResetKeyRequest: ValidateResetKeyRequest!) {
    validateResetKey(validateResetKeyRequest: $validateResetKeyRequest) {
      valid
      emailAddress
    }
  }
`;
