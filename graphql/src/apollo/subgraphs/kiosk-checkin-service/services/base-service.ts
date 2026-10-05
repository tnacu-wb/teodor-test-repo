import { createAxios } from '../../../client/axios';

const baseUrl = process.env.KIOSK_CHECKIN_SERVICE ?? 'http://kiosk-checkin-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  CHECKIN: {
    endpoint: '/v1/kiosk/checkIn',
    axiosClient: axiosClient
  },
  ROOM_ALLOCATION: {
    endpoint: '/v1/kiosk/allocate',
    axiosClient: axiosClient
  }
};
