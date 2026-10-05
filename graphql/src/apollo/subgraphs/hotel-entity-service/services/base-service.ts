import { createAxios } from '../../../client/axios';

const baseUrl = process.env.HOTEL_ENTITY_SERVICE ?? 'http://hotel-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  CANCELLATION_REASONS: {
    endpoint: '/v1/hotels/{hotelId}/cancellationReasons',
    flowCode: 'DIGITAL_CAN_001',
    axiosClient: axiosClient
  },
  CREATE_GROUP_BOOKING: {
    endpoint: '/v1/hotels/{hotelId}/group/bookingForm',
    flowCode: 'DIGITAL_CRE_001',
    axiosClient: axiosClient
  },
  HOTEL_AVAILABILITIES: {
    endpoint: '/v2/hotels/availabilities',
    flowCode: 'DIGITAL_AVA_003',
    axiosClient: axiosClient
  },
  HOTEL_AVAILABILITY: {
    endpoint: '/v1/hotels/{hotelId}/availabilities',
    flowCode: 'DIGITAL_AVA_001',
    axiosClient: axiosClient
  },
  HOTEL_AVAILABILITY_BY_IDS: {
    endpoint: '/v1/hotels/availabilities/distr',
    flowCode: 'DIGITAL_AVA_002',
    axiosClient: axiosClient
  },
  HOTEL_AVAILABILITY_BY_IDS_V2: {
    endpoint: '/v2/hotels/availabilities/distr',
    flowCode: 'DIGITAL_AVA_005',
    axiosClient: axiosClient
  },
  HOTEL_AVAILABILITY_BY_IDS_V3: {
    endpoint: '/v3/hotels/availabilities/distr',
    flowCode: 'DIGITAL_AVA_006',
    axiosClient: axiosClient
  },
  HOTEL_DISTANCE_FROM_SEARCH: {
    endpoint: '/v1/hotels/{hotelId}/distance',
    flowCode: 'DIGITAL_AVA_007',
    axiosClient: axiosClient
  },
  HOTEL_INVENTORY: {
    endpoint: '/v1/hotels/{hotelId}/hotelInventory',
    flowCode: 'DIGITAL_AVA_009',
    axiosClient: axiosClient
  },
  HOTELS_LOCATIONS: {
    endpoint: '/v1/hotels/locations',
    flowCode: 'DIGITAL_AVA_008',
    axiosClient: axiosClient
  },
  HOTEL_PREFERENCES: {
    endpoint: '/v1/hotels/{hotelId}/preferences',
    flowCode: 'DIGITAL_CON_014',
    axiosClient: axiosClient
  },
  RATE_CODE_PRICING: {
    endpoint: '/v1/hotels/{hotelId}/rate-code-pricing',
    flowCode: 'DIGITAL_BIN_003',
    axiosClient: axiosClient
  },
  DONATION_PACKAGES_OPERA: {
    endpoint: '/v1/hotels/{hotelId}/packages/donations',
    flowCode: 'DIGITAL_PAY_010',
    axiosClient: axiosClient
  },
  GET_PACKAGES_OPERA: {
    endpoint: '/v1/hotels/{hotelId}/packages',
    flowCode: 'DIGITAL_PKG_002',
    axiosClient: axiosClient
  }
};
