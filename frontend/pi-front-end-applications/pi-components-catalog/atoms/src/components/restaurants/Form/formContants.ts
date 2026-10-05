export const FORM_FIELD_TYPES = {
  SESSION_TABS: 'sessionTabs',
  USER_DETAILS: 'userDetails',
  TABLE_BOOKING_INFO: 'tableBookingInfo',
  SINGLE_DATE_PICKER: 'singleDatePicker',
  DYNAMIC_FIELD: 'dynamicField',
  INPUT_TEXT: 'input',
  TEXT_AREA: 'textArea',
  ENQUIRY_FORM_FIELDS: 'enquiryFormFields',
  RADIO_GROUP: 'radioGroup',
  SWITCH: 'switch',
  DROPDOWN: 'dropdown',
  MENUDROPDOWN: 'menuDropdown',
  CHILD_DROPDOWN: 'childDropdown',
  CHECKBOX: 'checkbox',
} as const;

export const FORM_BUTTON_TYPES = {
  SUBMIT: 'submit',
  RESET: 'reset',
  BUTTON: 'button',
} as const;

const FORM_FIELD_ERROR_LOCATION = {
  ON_TOP: 'onTop',
};

const FIRST_NAME = {
  MATCHES: /[^a-zA-Z\sÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]/g,
  MIN: 2,
  MAX: 100,
};

const LAST_NAME = {
  MATCHES: /[^a-zA-Z\sÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]/g,
  MIN: 2,
  MAX: 100,
};

//min and max will be increased by 3, to pass validation for GB and DE phone numbers, temp solution
const PHONE = {
  MATCHES: /(?:\+\d{1,}|\d{3}-\d{3}-\d{4})|[^0-9\s+]/g,
  MIN: 11,
  MAX: 11,
};
const ADULTS_CHILDREN_BY_ENQUIRY = {
  MATCHES: /[^0-9]/g,
  MIN: 1,
  MAX: 3,
};

export const FORM_VALIDATIONS = {
  FORM_FIELD_ERROR_LOCATION,
  FIRST_NAME,
  LAST_NAME,
  PHONE,
  ADULTS_CHILDREN_BY_ENQUIRY,
};

export type TabName = 'Breakfast' | 'Lunch' | 'Dinner';
