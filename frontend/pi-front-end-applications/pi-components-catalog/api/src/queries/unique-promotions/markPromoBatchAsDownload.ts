import { gql } from 'graphql-request';

export const MARK_PROMO_BATCH_AS_DOWNLOAD = gql`
  query MarkPromoBatchAsDownloaded($batchId: String!) {
    markPromoBatchAsDownloaded(batchId: $batchId)
  }
`;
