import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.RESERVATION_MANAGER_ENTITY_SERVICE ??
  'http://reservations-manager-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  BOOKING_HISTORY: {
    endpoint: '/v1/bookings/history',
    flowCode: 'DIGITAL_FBO_005',
    axiosClient: axiosClient
  },
  BOOKING_INFO_CARD_DETAILS: {
    endpoint: '/v1/bookings/information',
    flowCode: 'DIGITAL_BIN_005',
    axiosClient: axiosClient
  },
  CANCEL_BOOKING: {
    endpoint: '/v1/bookings/cancel',
    flowCode: 'DIGITAL_CAN_005',
    axiosClient: axiosClient
  },
  RESEND_INVOICE_EMAIL: {
    endpoint: '/v1/bookings/invoice',
    flowCode: 'DIGITAL_BIN_007',
    axiosClient: axiosClient
  },
  RESEND_CONFIRMATION_EMAIL: {
    endpoint: '/v1/bookings/confirmation',
    flowCode: 'DIGITAL_BIN_006',
    axiosClient: axiosClient
  },
  DOWNLOAD_BOOKING_INVOICE: {
    endpoint: '/v1/bookings/invoices/download',
    flowCode: 'DIGITAL_BIN_009',
    axiosClient: axiosClient
  },
  GET_UPCOMING_BOOKINGS: {
    endpoint: '/v1/bookings/upcomingBookings',
    flowCode: 'DIGITAL_BIN_008',
    axiosClient: axiosClient
  }
};
