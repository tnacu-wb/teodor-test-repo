import { createAxios } from '../../../client/axios';

const baseUrl = process.env.BASKET_SERVICE ?? 'http://basket-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  BASKET: {
    endpoint: '/v1/baskets/{basketReference}',
    flowCode: 'DIGITAL_BAS_001',
    axiosClient: axiosClient
  },
  BASKET_STATUS: {
    endpoint: '/v1/baskets/{basketReference}/checkStatus',
    flowCode: 'DIGITAL_BAS_002',
    axiosClient: axiosClient
  },
  ECKOH_RECORDING_STATUS: {
    endpoint: '/v1/baskets/ccui/{basketReference}/eckoh',
    flowCode: 'DIGITAL_PAY_007',
    axiosClient: axiosClient
  },
  INITIATE_ECKOH_PAYMENT: {
    endpoint: '/v1/baskets/ccui/{basketReference}/eckoh',
    flowCode: 'DIGITAL_PAY_006',
    axiosClient: axiosClient
  },
  INITIATE_CCUI_PAYMENT: {
    endpoint: '/v1/baskets/ccui/{basketReference}/pay',
    flowCode: 'DIGITAL_PAY_005',
    axiosClient: axiosClient
  },
  CONFIRM_PRE_CHECKIN: {
    endpoint: '/v1/baskets/{basketReference}/preCheckIn',
    flowCode: 'DIGITAL_DRC_002',
    axiosClient: axiosClient
  },
  EMAIL_NOTIFICATION: {
    endpoint: '/v1/baskets/{basketReference}/emailNotifications',
    flowCode: 'DIGITAL_GDE_003',
    axiosClient: axiosClient
  },
  UPDATE_DISCOUNT: {
    endpoint: '/v1/baskets/ccui/discount',
    flowCode: 'DIGITAL_CRE_003',
    axiosClient: axiosClient
  },
  INITIATE_PAYMENT: {
    endpoint: '/v1/baskets/{basketReference}/pay',
    flowCode: 'DIGITAL_PAY_003',
    axiosClient: axiosClient
  },
  INITIATE_PAYPAL_PAYMENT: {
    endpoint: '/v1/baskets/{basketReference}/pp-pay',
    flowCode: 'DIGITAL_PAY_004',
    axiosClient: axiosClient
  },
  UPDATE_RESERVATION: {
    endpoint: '/v1/baskets/reservations/update',
    flowCode: 'DIGITAL_UPD_001',
    axiosClient: axiosClient
  },
  CONFIRM_PRE_CHECK_OUT: {
    endpoint: '/v1/baskets/{basketReference}/preCheckOut',
    flowCode: 'DIGITAL_COOL_001',
    axiosClient: axiosClient
  },
  BACKGROUND_CHARGE: {
    endpoint: '/v1/basket/background-charge',
    flowCode: 'DIGITAL_CIOL_001',
    axiosClient: axiosClient
  }
};
