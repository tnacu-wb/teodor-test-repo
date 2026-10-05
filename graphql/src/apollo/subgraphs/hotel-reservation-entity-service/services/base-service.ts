import { createAxios } from '../../../client/axios';

const baseUrl =
  process.env.HOTEL_RESERVATION_ENTITY_SERVICE ??
  'http://hotel-reservation-entity-service.opera-be';

const axiosClient = createAxios(baseUrl);

export const endpoints = {
  ADD_NEW_ROOM: {
    endpoint: '/v1/reservations/amend/addNewRoom',
    flowCode: 'DIGITAL_AME_003',
    axiosClient: axiosClient
  },
  AMEND_CONFIRMATION_PRICES: {
    endpoint: '/v1/reservations/amend/amendConfirmationPrices',
    flowCode: 'DIGITAL_AME_002',
    axiosClient: axiosClient
  },
  AMEND_PAYMENT_OPTIONS: {
    endpoint: '/v1/reservations/amend/paymentOptions',
    flowCode: 'DIGITAL_AME_013',
    axiosClient: axiosClient
  },
  AMEND_SUMMARY: {
    endpoint: '/v1/reservations/amend/summary',
    flowCode: 'DIGITAL_AME_001',
    axiosClient: axiosClient
  },
  BART_BOOKING_INFORMATION: {
    endpoint: '/v1/reservations/booking-card',
    flowCode: 'DIGITAL_BIN_004',
    axiosClient: axiosClient
  },
  BOOKING_ALLOWANCES: {
    endpoint: '/v1/reservations/basket/{basketReference}/allowances',
    flowCode: 'DIGITAL_PAY_009',
    axiosClient: axiosClient
  },
  CANCEL_RESERVATION: {
    endpoint: '/v1/reservations/cancellations',
    flowCode: 'DIGITAL_CAN_003',
    axiosClient: axiosClient
  },
  CANCELLATION_POLICIES: {
    endpoint: '/v1/reservations/cancellationPolicies',
    flowCode: 'DIGITAL_CAN_002',
    axiosClient: axiosClient
  },
  CREATE_RESERVATION: {
    endpoint: '/v1/reservations',
    flowCode: 'DIGITAL_CRE_001',
    axiosClient: axiosClient
  },
  FIND_BOOKING: {
    endpoint: '/v1/reservations/find',
    flowCode: 'DIGITAL_FBO_002',
    axiosClient: axiosClient
  },
  FIND_BOOKING_FOR_KIOSK: {
    endpoint: '/v1/reservations/find/kiosk',
    flowCode: 'DIGITAL_FBO_006',
    axiosClient: axiosClient
  },
  MANAGE_BOOKING: {
    endpoint: '/v1/reservations/cancel',
    flowCode: 'DIGITAL_FBO_001',
    axiosClient: axiosClient
  },
  MEMOS: {
    endpoint: '/v1/reservations/basket/{basketReference}/memos',
    flowCode: 'DIGITAL_CRE_004',
    axiosClient: axiosClient
  },
  PMS_BOOKING_INFORMATION: {
    endpoint: '/v1/reservations/basket/{basketReference}',
    flowCode: 'DIGITAL_BIN_001',
    axiosClient: axiosClient
  },
  RETRIEVE_CHANGES_LOG: {
    endpoint: '/v1/reservations/changeLog',
    flowCode: 'DIGITAL_CON_001',
    axiosClient: axiosClient
  },
  SAVE_CHARITY_PACKAGES: {
    endpoint: '/v1/reservations/ancillaries',
    flowCode: '',
    axiosClient: axiosClient
  },
  GET_SAVED_PACKAGES_OPERA: {
    endpoint: '/v1/reservations/ancillaries',
    flowCode: 'DIGITAL_PKG_001',
    axiosClient: axiosClient
  },
  SEARCH_BOOKINGS: {
    endpoint: '/v1/reservations/search',
    flowCode: 'DIGITAL_FBO_003',
    axiosClient: axiosClient
  },
  SEARCH_BOOKINGS_CCUI: {
    endpoint: '/v1/reservations/search/booking/cdh',
    flowCode: 'DIGITAL_FBO_004',
    axiosClient: axiosClient
  },
  PRE_CHECK_IN_STATUS: {
    endpoint: '/v1/reservations/pre-checkin',
    flowCode: 'DIGITAL_DRC_002',
    axiosClient: axiosClient
  },
  CANCEL_ON_HOLD_RESERVATION: {
    endpoint: '/v1/reservations/cancellations/on-hold',
    flowCode: 'DIGITAL_CAN_004',
    axiosClient: axiosClient
  },
  REASON_FOR_STAY: {
    endpoint: '/v1/reservations/reasonForStay',
    flowCode: 'DIGITAL_GDE_002',
    axiosClient: axiosClient
  },
  RATE_CODE: {
    endpoint: '/v1/reservations/rate-code',
    flowCode: 'DIGITAL_CRE_002',
    axiosClient: axiosClient
  },
  ROOM_TYPE: {
    endpoint: '/v1/reservations/roomTypeUpdate',
    flowCode: 'DIGITAL_CRE_006',
    axiosClient: axiosClient
  },
  AMEND_DISTRIBUTION: {
    endpoint: '/v1/reservations/amendDistribution/{basketReference}',
    flowCode: 'DIGITAL_AME_010',
    axiosClient: axiosClient
  },
  RESERVATION_OVERRIDE_REASONS: {
    endpoint: '/v1/reservations/overrideReasons',
    axiosClient: axiosClient
  },
  REMOVE_ROOM: {
    endpoint: '/v1/reservations/rooms/delete',
    flowCode: 'DIGITAL_AME_009',
    axiosClient: axiosClient
  },
  UPDATE_CNP: {
    endpoint: '/v1/reservations/amend/cnp/{basketReference}',
    flowCode: 'DIGITAL_AME_007',
    axiosClient: axiosClient
  },
  COPY_BOOKING: {
    endpoint: '/v1/reservations/copy',
    flowCode: 'DIGITAL_AME_005',
    axiosClient: axiosClient
  },
  CREATE_MEMO: {
    endpoint: '/v1/reservations/memos',
    flowCode: 'DIGITAL_CRE_005',
    axiosClient: axiosClient
  },
  ATTACH_TO_RESERVATION: {
    endpoint: '/v1/reservations/attachments',
    flowCode: 'DIGITAL_DRC_001',
    axiosClient: axiosClient
  },
  UPDATE_RESERVATION_PACKAGES_SCHEDULED: {
    endpoint: '/v1/reservations/ancillaries/scheduled',
    flowCode: 'DIGITAL_PKG_005',
    axiosClient: axiosClient
  },
  UPDATE_RESERVATION_PREFERENCES: {
    endpoint: '/v1/reservations/preferences',
    flowCode: 'DIGITAL_PRE_001',
    axiosClient: axiosClient
  },
  UPDATE_EMAIL: {
    endpoint: '/v1/reservations/email/{basketReference}',
    flowCode: 'DIGITAL_AME_011',
    axiosClient: axiosClient
  },
  SAVE_RESERVATION: {
    endpoint: '/v1/reservations/ancillaries',
    flowCode: 'DIGITAL_PKG_003',
    axiosClient: axiosClient
  },
  CREATE_RESERVATION_GUEST: {
    endpoint: '/v1/reservations/guests',
    flowCode: 'DIGITAL_GDE_001',
    axiosClient: axiosClient
  },
  UPDATE_RESERVATION_PACKAGES_BY_RESERVATION: {
    endpoint: '/v1/reservations/ancillaries/reservation-id',
    flowCode: 'DIGITAL_PKG_004',
    axiosClient: axiosClient
  },
  CONFIRM_AMEND: {
    endpoint: '/v1/reservations/amend/confirmAmend',
    flowCode: 'DIGITAL_AME_006',
    axiosClient: axiosClient
  },
  CONFIRM_AMEND_LOGIC: {
    endpoint: '/v1/reservations/amend/confirmAmendLogic',
    flowCode: 'DIGITAL_AME_012',
    axiosClient: axiosClient
  },
  EDIT_ROOM: {
    endpoint: '/v1/reservations/amend/editRoom',
    flowCode: 'DIGITAL_AME_008',
    axiosClient: axiosClient
  },
  CHANGE_BOOKING_DATES: {
    endpoint: '/v1/reservations/amendStayDates',
    flowCode: 'DIGITAL_AME_004',
    axiosClient: axiosClient
  },
  BOOKING_INFORMATION_AUTHENTICATED: {
    endpoint: '/v1/reservations/basket/{bookingReference}/authenticated',
    flowCode: 'DIGITAL_BIN_002',
    axiosClient: axiosClient
  },
  BOOKING_INFORMATION_AUTHENTICATED_WITH_TOKEN: {
    endpoint: '/v1/reservations/basket/{bookingReference}/authenticatedWithToken',
    flowCode: 'DIGITAL_BIN_002',
    axiosClient: axiosClient
  },
  UPDATE_UDFC_20: {
    endpoint: '/v1/reservations/updateUdfc20',
    flowCode: 'DIGITAL_UDFC20_001',
    axiosClient: axiosClient
  }
};
