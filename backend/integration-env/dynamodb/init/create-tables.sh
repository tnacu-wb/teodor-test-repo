#!/usr/bin/env sh
set -eu

endpoint="http://dynamodb-local:8000"
region="eu-west-1"

export AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID:-key}"
export AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY:-secret}"
export AWS_DEFAULT_REGION="${AWS_DEFAULT_REGION:-$region}"

until aws dynamodb list-tables --endpoint-url "$endpoint" --region "$region" >/dev/null 2>&1; do
  sleep 1
done

if ! aws dynamodb describe-table \
  --table-name newBasket \
  --endpoint-url "$endpoint" \
  --region "$region" >/dev/null 2>&1; then
  aws dynamodb create-table \
    --table-name newBasket \
    --attribute-definitions \
      AttributeName=threeLetterHotelId,AttributeType=S \
      AttributeName=sortKey,AttributeType=S \
      AttributeName=reference,AttributeType=S \
    --key-schema \
      AttributeName=threeLetterHotelId,KeyType=HASH \
      AttributeName=sortKey,KeyType=RANGE \
    --global-secondary-indexes \
      'IndexName=referenceIndex,KeySchema=[{AttributeName=reference,KeyType=HASH}],Projection={ProjectionType=KEYS_ONLY},ProvisionedThroughput={ReadCapacityUnits=10,WriteCapacityUnits=5}' \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --endpoint-url "$endpoint" \
    --region "$region" >/dev/null
fi

if ! aws dynamodb describe-table \
  --table-name PrepaidBookingCharges \
  --endpoint-url "$endpoint" \
  --region "$region" >/dev/null 2>&1; then
  aws dynamodb create-table \
    --table-name PrepaidBookingCharges \
    --attribute-definitions \
      AttributeName=reservationId,AttributeType=S \
      AttributeName=paymentNo,AttributeType=N \
    --key-schema \
      AttributeName=reservationId,KeyType=HASH \
      AttributeName=paymentNo,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --endpoint-url "$endpoint" \
    --region "$region" >/dev/null
fi

if ! aws dynamodb describe-table \
  --table-name threec-payment \
  --endpoint-url "$endpoint" \
  --region "$region" >/dev/null 2>&1; then
  aws dynamodb create-table \
    --table-name threec-payment \
    --attribute-definitions \
      AttributeName=payment-id,AttributeType=S \
      AttributeName=request-id,AttributeType=S \
    --key-schema \
      AttributeName=payment-id,KeyType=HASH \
    --global-secondary-indexes \
      'IndexName=request-id,KeySchema=[{AttributeName=request-id,KeyType=HASH}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=5,WriteCapacityUnits=5}' \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --endpoint-url "$endpoint" \
    --region "$region" >/dev/null
fi

aws dynamodb wait table-exists \
  --table-name newBasket \
  --endpoint-url "$endpoint" \
  --region "$region"
aws dynamodb wait table-exists \
  --table-name PrepaidBookingCharges \
  --endpoint-url "$endpoint" \
  --region "$region"
aws dynamodb wait table-exists \
  --table-name threec-payment \
  --endpoint-url "$endpoint" \
  --region "$region"
ttl_status="$(aws dynamodb describe-time-to-live \
  --table-name threec-payment \
  --endpoint-url "$endpoint" \
  --region "$region" \
  --query 'TimeToLiveDescription.TimeToLiveStatus' \
  --output text)"

if [ "$ttl_status" != "ENABLED" ] && [ "$ttl_status" != "ENABLING" ]; then
  aws dynamodb update-time-to-live \
    --table-name threec-payment \
    --time-to-live-specification 'Enabled=true,AttributeName=expire' \
    --endpoint-url "$endpoint" \
    --region "$region" >/dev/null
fi
