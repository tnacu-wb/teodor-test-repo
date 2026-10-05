import { endpoints } from './base-service';
import { get, post, patch } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, getServiceEndpoint } from '../../../utils/base-utils';

/**
 * This method is used to create or retrieve a promo batch.
 * The Promo Batch API expects the payload to be sent directly
 * in the request body as JSON (no query parameters required).
 *
 * @param promoBatchRequest Promo batch request payload
 * @returns The response containing the promo batch details
 */
export const createPromoBatch = async (args: any, context: any): Promise<any> => {
  try {
    return await post(endpoints.GET_BATCHES, createPromoBatch, args.createPromoBatchInput, context);
  } catch (error: Error | any) {
    handleError(error, args.createPromoBatchInput);
  }
};

export const getPromoBatchById = async (
  { batchId }: { batchId: string },
  context: any
): Promise<any> => {
  try {
    const promoBatchById = endpoints.GET_BATCHID.endpoint.replace('{batchId}', batchId.toString());

    const promoBatchByIdEndPoint = getServiceEndpoint(promoBatchById, endpoints.GET_BATCHID);
    return await get(promoBatchByIdEndPoint, getPromoBatchById, null, context);
  } catch (error: Error | any) {
    handleError(error, { batchId });
  }
};

export const getPromoBatchSummary = async (
  {
    page,
    size,
    sort
  }: {
    page: number;
    size: number;
    sort?: string;
  },
  context: any
): Promise<any> => {
  const finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [
      { key: 'page', value: page, required: true },
      { key: 'size', value: size, required: true },
      { key: 'sort', value: sort, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.GET_BATCH_SUMMARY, getPromoBatchSummary, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

export const markPromoBatchAsDownloadedApi = async (
  { batchId }: { batchId: string },
  context: any
): Promise<any> => {
  const finalMap: { [key: string]: any } = {};

  try {
    addFieldsToMap([{ key: 'batchId', value: batchId, required: true }], finalMap);

    const resolvedEndpoint = endpoints.MARK_BATCH_DOWNLOADED.endpoint.replace('{batchId}', batchId);

    const apiConfig = getServiceEndpoint(resolvedEndpoint, endpoints.MARK_BATCH_DOWNLOADED);

    const response = await patch(apiConfig, markPromoBatchAsDownloadedApi, {}, context);

    return response?.status === 204;
  } catch (error) {
    handleError(error, finalMap);
    return false;
  }
};

export const promoKind = async (
  {
    promoCode,
    country,
    channel,
    subChannel
  }: {
    promoCode: string;
    country: string;
    channel: string;
    subChannel?: string;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'promoCode', value: promoCode, required: true },
      { key: 'country', value: country, required: true },
      { key: 'channel', value: channel, required: true },
      { key: 'subChannel', value: subChannel, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    const promoKindResponse = await get(endpoints.GET_PROMO_KIND, promoKind, finalMap, context);

    const baseResponse = {
      promoKind: promoKindResponse?.promoKind ?? null,
      operaPromoCode: promoKindResponse?.operaPromoCode ?? null,
      uniquePromoCodeStatus: promoKindResponse?.uniquePromoCodeStatus ?? null
    };

    return baseResponse;
  } catch (error) {
    handleError(error, finalMap);
  }
};
