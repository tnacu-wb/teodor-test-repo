import { createAxios } from '../../../client/axios';

const baseUrl = process.env.DIGITAL_KEY_SERVICE ?? 'http://digital-key-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  GENERATE_OTP: {
    endpoint: '/v1/digital-key/generate-otp',
    flowCode: 'DIGITAL_GENOTP_001',
    axiosClient: axiosClient
  },
  PASS_PROVISIONING_WITH_OTP: {
    endpoint: '/v1/digital-key/provision',
    flowCode: 'DIGITAL_VEROTP_001',
    axiosClient: axiosClient
  },
  CHECKIN: {
    endpoint: '/v1/digital-key/checkIn',
    flowCode: 'DIGITAL_CHECKIN_001',
    axiosClient: axiosClient
  },
  REGISTER_MOBILE_DEVICE: {
    endpoint: '/v1/digital-key/register-mobile-device',
    flowCode: 'DIGITAL_REGISTER_DEVICE_001',
    axiosClient: axiosClient
  },
  GOOGLE_WALLET_PROVISIONING: {
    endpoint: '/v1/digital-key/google-wallet-provisioning-with-otp',
    flowCode: 'DIGITAL_GOOGLE_WALLET_PROVISIONING_001',
    axiosClient: axiosClient
  }
};
