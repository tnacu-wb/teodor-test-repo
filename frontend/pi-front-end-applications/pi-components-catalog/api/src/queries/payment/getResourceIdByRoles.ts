import { gql } from 'graphql-request';

export const GET_RESOURCE_ID_BY_ROLES = gql`
  query searchResourceId($roleIdList: String!) {
    searchResourceId(roleIdList: $roleIdList)
  }
`;
