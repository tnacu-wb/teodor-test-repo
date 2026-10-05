import { createAxios } from '../../../client/axios';

const baseUrl = process.env.MARKETING_SERVICE_OPERA ?? 'http://marketing-service-opera.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  ANONYMOUS_NEWSLETTER_PREFERENCES: {
    endpoint: '/marketing/newsletter/{email}',
    flowCode: 'DIGITAL_PRE_002',
    axiosClient: axiosClient
  },
  GET_CONTACT_PREFERENCES: {
    endpoint: '/marketing/newsletter/{contactType}/{contactValue}',
    flowCode: 'DIGITAL_PRE_003',
    axiosClient: axiosClient
  },
  UPDATE_CONTACT_PREFERENCES: {
    endpoint: '/marketing/newsletter/{contactType}/{contactValue}',
    flowCode: 'DIGITAL_PRE_004',
    axiosClient: axiosClient
  },
  UPDATE_EMAIL_PREFERENCES: {
    endpoint: '/marketing/newsletter/email',
    flowCode: 'DIGITAL_PRE_005',
    axiosClient: axiosClient
  },
  UPDATE_EMAIL_PREFERENCES_WITH_CHANNEL_ID: {
    endpoint: '/marketing/newsletter/channel/{contactChannelId}',
    flowCode: 'DIGITAL_PRE_006',
    axiosClient: axiosClient
  }
};
