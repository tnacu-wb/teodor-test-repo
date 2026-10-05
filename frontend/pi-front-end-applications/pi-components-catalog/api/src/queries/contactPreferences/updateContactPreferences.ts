import { gql } from 'graphql-request';

export const UPDATE_EMAIL_PREFERENCES = gql`
  mutation updateEmailPreferences($request: PreferencesUpdateRequest!) {
    updateEmailPreferences(request: $request)
  }
`;
