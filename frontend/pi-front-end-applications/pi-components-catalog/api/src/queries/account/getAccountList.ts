import { gql } from 'graphql-request';

export const GET_ACCOUNT_LIST = gql`
  query getAccountList($viewAll: Boolean) {
    getAccountList(viewAll: $viewAll) {
      accounts {
        accountName
        accountNumber
        schemeCustomerId
        tetheredGuid
        registrationRoles
        errorCode
        scheme
        apiUserGuid
      }
      totalRecordCount
    }
  }
`;
