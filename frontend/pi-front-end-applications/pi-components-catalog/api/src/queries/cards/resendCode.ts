import { gql } from 'graphql-request';

export const resendCodeQuery = () => gql`
  mutation InviteCardHolder(
    $tetheredUserGuid: String!
    $cardId: String!
    $inviteCardHolderRequest: InviteCardHolderRequest!
  ) {
    inviteCardHolder(
      tetheredUserGuid: $tetheredUserGuid
      cardId: $cardId
      inviteCardHolderRequest: $inviteCardHolderRequest
    )
  }
`;
