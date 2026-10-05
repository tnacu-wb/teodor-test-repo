import { createAxios } from '../../../client/axios';

const baseUrl = process.env.FEEDBACK_SERVICE_OPERA ?? 'http://feedback-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  ADD_FEEDBACK: {
    endpoint: '/feedback',
    flowCode: '',
    axiosClient: axiosClient
  }
};
