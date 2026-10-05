export const BOOKERS_REFERENCE_MAX_LENGTH = 50;

export const TOTAL_DINNER_BUDGET_REGEX = /^\d*([.,]\d{0,2})?$/;

export const PAYMENT_FAILED_KEY = 'paymentStatus';
export const PAYMENT_FAILED_INFO_KEY = 'paymentFailureInfo';
export const PAYMENT_FAILURE_INFO_INITIAL_VALUE = {
  errCode: 0,
  debugMessage: '',
  globalErrTextTemplate: '',
};
export const PAYMENT_FAILURE_CODE_KEY = 'paymentFailureCode';
export const PAYMENT_FAILURE_CODE_INITIAL_VALUE = '';
export const PAYMENT_FAILURE_DESCRIPTION_KEY = 'paymentFailureDescription';
export const PAYMENT_FAILURE_DESCRIPTION_INITIAL_VALUE = '';

export const PAYMENT_ANALYTICS_KEY = 'paymentAnalytics';
export const PAYMENT_ANALYTICS_INITIAL_VALUE = {
  paymentSessionID: '',
  paymentTemplateID: '',
  cardNotPresent: false,
  paymentCardSelected: '',
  paymentTakenNow: '',
  basketReference: '',
};

export const PAYMENT_FAILED_INITIAL_VALUE = '';
export const PAYMENT_FAILED_VALUE = 'failed';
export const PAYPAL_PAYMENT = 'PAYPAL';
export const APGP_PAYMENT = 'APGP';
export const APGP_PLANET_MESSAGE_GP = 'Google pay button clicked';
export const APGP_PLANET_MESSAGE_AP = 'Apple pay button clicked';
export const APPLE = 'APPLE';
export const GOOGLE = 'GOOGLE';
export const WALLET_APPLE = 'WALLET_APPLE';

export const OTHER_ALLOWANCES_CCUI = {
  MEAL_DEAL: {
    type: 'mealDeal',
    name: 'Meal Deal',
    operaId: 'MDP',
  },
  PI_BREAKFAST: {
    type: 'premierInnBreakfast',
    name: 'Premier Inn Breakfast',
    operaId: 'BFADBF',
  },
  CONT_BREAKFAST: {
    type: 'continentalBreakfast',
    name: 'Continental Breakfast',
    operaId: 'BFADCT',
  },
  CAR_PARKING: {
    type: 'carParking',
    name: 'Car Parking',
    operaId: null,
  },
  WIFI: {
    type: 'ultimateWifi',
    name: 'Ultimate Wifi',
    operaId: null,
  },
};
export const PIBA_NOT_ALLOWED_HOTEL_KEYS = {
  PIBA_EU_ALLOWED_ONLY_IN_EU: 'PIBA_EU_ALLOWED_ONLY_IN_EU',
  PIBA_UK_ALLOWED_ONLY_IN_UK: 'PIBA_UK_ALLOWED_ONLY_IN_UK',
};

export const PAYMENT_REDESIGN_OPTIONS = {
  LED: 'led',
  TAB: 'tab',
};

export const PAYMENT_REDESIGN = 'payment_redesign';
export const THIRTY_MINUTES = 30; // 30 min
export const DEFAULT_SELECTED_PAYMENT_DETAIL = {
  type: 'default',
  order: 1,
  enabled: false,
};
