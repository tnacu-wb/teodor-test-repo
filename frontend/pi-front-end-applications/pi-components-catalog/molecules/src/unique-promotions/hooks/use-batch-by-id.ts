import { Get_BATCH_BY_ID, MARK_PROMO_BATCH_AS_DOWNLOAD } from '@whitbread-eos/api';
import { useQueryRequest } from '@whitbread-eos/utils';

import { GetBatchByIdResponse, UseBatchByIdOptions } from './types';

const isValidBatchByIdResponse = (data: any): data is GetBatchByIdResponse => {
  if (
    !data ||
    typeof data !== 'object' ||
    !('getBatchById' in data) ||
    !data.getBatchById ||
    typeof data.getBatchById !== 'object'
  ) {
    return false;
  }

  return true;
};

export const useBatchById = (batchId: string, options: UseBatchByIdOptions) => {
  const { enabled } = options;

  const {
    data,
    isLoading,
    isError: queryError,
    isFetching,
    refetch,
  } = useQueryRequest(
    ['getBatchById', batchId],
    Get_BATCH_BY_ID,
    { batchId },
    {
      enabled,
    }
  );

  // If API returned string "no healthy upstream", or any incorrect structure
  const isBatchByIdSafe = isValidBatchByIdResponse(data);

  const batchIdData = isBatchByIdSafe ? data.getBatchById : null;

  const finalIsError = queryError || !isBatchByIdSafe;
  return {
    batchData: isBatchByIdSafe && batchIdData ? batchIdData : null,
    isLoading,
    isError: finalIsError,
    isFetching,
    refetch,
  };
};

const isValidBatchAsDownloadResponse = (
  data: any
): data is { markPromoBatchAsDownloaded: boolean } => {
  return typeof data === 'object' && typeof data?.markPromoBatchAsDownloaded === 'boolean';
};

export const usePromoBatchAsDownload = (batchId: string, options: UseBatchByIdOptions) => {
  const { enabled } = options;

  const {
    data,
    isLoading,
    isError: queryError,
    isFetching,
    refetch,
  } = useQueryRequest(
    ['getPromoBatchAsDownload', batchId],
    MARK_PROMO_BATCH_AS_DOWNLOAD,
    { batchId },
    {
      enabled,
    }
  );

  const safe = isValidBatchAsDownloadResponse(data);

  const summary = safe ? data.markPromoBatchAsDownloaded : null;

  const finalIsError = queryError || !safe;

  return {
    data: safe && summary ? summary : null,
    isLoading,
    isError: finalIsError,
    isFetching,
    refetch,
  };
};
