import { gql } from 'graphql-request';

export const GET_PAGE_CONTENT = gql`
  query labelQuery {
    label {
      key
      value
    }
  }
`;
