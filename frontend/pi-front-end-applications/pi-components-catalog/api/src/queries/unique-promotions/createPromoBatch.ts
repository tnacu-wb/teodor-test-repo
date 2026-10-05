import { gql } from 'graphql-request';

export const CREATE_PROMO_BATCH = gql`
  mutation CreatePromoBatch(
    $operaPromoCode: String!
    $batchCount: Int!
    $codeLength: Int!
    $expiryDate: Date!
    $notes: String!
    $prefix: String!
    $hotelId: String!
    $requestedBy: String!
    $campaignName: String!
    $isMultiple: Boolean!
    $maxRedemptionLimit: Int
    $batchEligibilities: [BatchEligibilityInput!]!
  ) {
    createPromoBatch(
      createPromoBatchInput: {
        operaPromoCode: $operaPromoCode
        batchCount: $batchCount
        codeLength: $codeLength
        expiryDate: $expiryDate
        notes: $notes
        prefix: $prefix
        hotelId: $hotelId
        requestedBy: $requestedBy
        campaignName: $campaignName
        isMultiple: $isMultiple
        maxRedemptionLimit: $maxRedemptionLimit
        batchEligibilities: $batchEligibilities
      }
    ) {
      batchId
      status
      createdAt
      operaPromoCode
      prefix
      batchCount
      codeLength
      s3Key
      notes
      requestedBy
      expiryDate
      updatedAt
      completedAt
      campaignName
    }
  }
`;
