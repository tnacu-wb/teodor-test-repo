import { gql } from 'graphql-request';

export const getCostCentreDetailsQuery = () => gql`
  query GetCostCentreDetails($tetheredUserGuid: String!) {
    getCostCentreDetails(tetheredUserGuid: $tetheredUserGuid) {
      accountUniqueCustomerId
      costCentreUniqueCustomerId
      costCentreCode
      costCentreName
    }
  }
`;
