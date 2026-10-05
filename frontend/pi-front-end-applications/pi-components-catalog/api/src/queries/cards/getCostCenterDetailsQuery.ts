import { gql } from 'graphql-request';

export const getCostCenterDetailsQuery = () => gql`
  query GetCostCentreDetails($tetheredUserGuid: String!) {
    getCostCentreDetails(tetheredUserGuid: $tetheredUserGuid) {
      accountUniqueCustomerId
      costCentreUniqueCustomerId
      costCentreCode
      costCentreName
    }
  }
`;
