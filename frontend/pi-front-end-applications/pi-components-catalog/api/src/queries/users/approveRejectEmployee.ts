import { gql } from 'graphql-request';

export const approveRejectEmployeeQuery = () => gql`
  mutation approveRejectEmployee($approveRejectRequest: ApproveRejectRequest!) {
    approveRejectEmployee(approveRejectRequest: $approveRejectRequest)
  }
`;
