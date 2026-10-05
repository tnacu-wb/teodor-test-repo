import { gql } from 'graphql-request';

export const deleteCardCDHMutation = () => gql`
  mutation deleteCompanyCard($companyId: String!, $cardId: String!) {
    deleteCompanyCard(companyId: $companyId, cardId: $cardId)
  }
`;
