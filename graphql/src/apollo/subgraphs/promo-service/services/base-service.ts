import { createAxios } from '../../../client/axios';

const baseUrl = process.env.PROMO_SERVICE ?? 'http://promo-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GET_BATCHES: {
    endpoint: '/v1/promo/batches',
    flowCode: 'DIGITAL_PR_001',
    axiosClient: axiosClient
  },
  GET_BATCHID: {
    endpoint: '/v1/promo/batches/{batchId}',
    flowCode: 'DIGITAL_PR_002',
    axiosClient: axiosClient
  },
  GET_BATCH_SUMMARY: {
    endpoint: '/v1/promo/batches',
    flowCode: 'DIGITAL_PR_003',
    axiosClient: axiosClient
  },
  MARK_BATCH_DOWNLOADED: {
    endpoint: '/v1/promo/batches/{batchId}/mark-downloaded',
    flowCode: 'DIGITAL_PR_004',
    axiosClient: axiosClient
  },
  GET_PROMO_KIND: {
    endpoint: '/v1/promo/batches/promo-kind',
    flowCode: 'DIGITAL_PR_004',
    axiosClient: axiosClient
  }
};
