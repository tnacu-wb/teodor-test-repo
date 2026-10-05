import { gql } from 'graphql-request';

export const GET_BATCH_SUMMARY = gql`
  query GetBatchSummary($page: Int!, $size: Int!, $sort: String!) {
    getBatchSummary(page: $page, size: $size, sort: $sort) {
      page
      size
      totalElements
      totalPages
      hasNext
      startIndex
      endIndex
      items {
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
        maxRedemptionLimit
      }
    }
  }
`;
