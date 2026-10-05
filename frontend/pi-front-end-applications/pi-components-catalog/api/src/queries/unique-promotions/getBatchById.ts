import { gql } from 'graphql-request';

export const Get_BATCH_BY_ID = gql`
  query GetBatchById($batchId: ID!) {
    getBatchById(batchId: $batchId) {
      batchId
      operaPromoCode
      prefix
      batchCount
      codeLength
      status
      s3Key
      notes
      downloaded
      password
      requestedBy
      createdAt
      expiryDate
      updatedAt
      completedAt
      campaignName
      batchEligibilities {
        region
        channel
        platforms
      }
      isMultiple
    }
  }
`;
