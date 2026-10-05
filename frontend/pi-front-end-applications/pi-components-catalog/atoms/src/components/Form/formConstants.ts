export const FORM_FIELD_TYPES = {
  INPUT_TEXT: 'input',
  INPUT_EMAIL: 'email',
  INPUT_PASSWORD: 'password',
  RADIO_GROUP: 'radioGroup',
  NON_FIELD_CONTENT: 'nonFieldContent',
  DROPDOWN: 'dropdown',
  CHECKBOX: 'checkbox',
  DYNAMIC_FIELD: 'dynamicField',
  TEXTAREA: 'textArea',
  ACCORDIAN: 'accordian',
  BUTTON: 'button',
  SUBMIT: 'submit',
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
  MATCHES: RegExp(
    /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g
  ),
  MIN: 2,
  MAX: 20,
};

const LAST_NAME = {
  MATCHES: RegExp(
    /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g
  ),
  MIN: 2,
  MAX: 30,
};

const LAST_NAME_BB_GUEST_DETAILS = {
  MATCHES: RegExp(
    /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g
  ),
  MIN: 1,
  MAX: 30,
};

const LAST_NAME_AMEND_BOOKING_GUEST_DETAILS = {
  MATCHES: RegExp(
    /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g
  ),
  MIN: 1,
  MAX: 30,
};

//min and max will be increased by 3, to pass validation for GB and DE phone numbers, temp solution
const PHONE = {
  MATCHES: RegExp(/^\s*\+?\s*(\d[\s-]*){6,}$/im),
  MIN: 9,
  MAX: 28,
};

const UNBRANDED_RESTAURANT_PHONE = {
  MATCHES: /^(?:\+\d+|\d{3}-\d{3}-\d{4})$/,
  MIN: 11,
  MAX: 15,
};

const ADULTS_CHILDREN_BY_ENQUIRY = {
  MATCHES: /[^0-9]/g,
  MIN: 1,
  MAX: 3,
};

const LANDLINE = {
  MATCHES: RegExp(/^\s*\+?\s*(\d[\s-]*)*$/im),
  MIN: 9,
  MAX: 28,
};

const EMAIL = {
  MATCHES: RegExp(
    /^[a-z0-9]+(?:[._'-][a-z0-9]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i
  ),
  MAX: 50,
};

const EMAIL_GD = {
  MATCHES: RegExp(
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  ),
};

const BB_EMAIL_GD = {
  MATCHES: RegExp(
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))|^$/
  ),
};

const ADDRESS = {
  MATCHES: RegExp(/^[ \p{L}0-9\u00E4\u00F6\u00FC\u00C4\u00D6\u00DC\u00df/'.,-]*$/gu),
  MAX: 35,
};

const COMPANY_NAME = {
  MATCHES: RegExp(/^[a-z.äöüß&'()-\d ]+$/gi),
  MAX: 40,
};

const COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS = {
  MATCHES: RegExp(
    /^[a-zÀÁÂÃÄÅĀẶĄẮÆǼẞÇĆĈĊČĎĐÈÉÊËĒĔĖĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİĴĶĹĻĽĿŁÑŃŅŇȠÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽàáâãäåāặąắæǽßçćĉċčďđèéêëēĕėěĝğġģĥħìíîïĩīǐįĵķĺļľŀłñńņňƞòóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž\u005B\u005D\u005C\u002F\u00AB\u00BB\u2018.,:;_!?"“”’*%=+£$€¥&@#()\-'\d{}>< ]+$/gi
  ),
  MIN: 2,
  MAX: 40,
};

const POSTAL_CODE = {
  MATCHES:
    /([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z]\d{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y]\d{1,2})|(([A-Za-z]\d[A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y]\d[A-Za-z]?))))\s?\d[A-Za-z]{2}(?![\s$&+,:;=?@#|'<>.\-^*()%!])+$)/,
  MATCHES_UK:
    /^(GIR ?0AA|[A-PR-UWYZ](\d{1,2}|([A-HK-Y]\d([0-9ABEHMNPRV-Y])?)|\d[A-HJKPS-UW]) ?\d[ABD-HJLNP-UW-Z]{2})$/i,

  MATCHES_DE: RegExp(/^([0123456789]\d{4})(?![\\s$&+,:;=?@#|'<>.\-^*()%!])+$/),
  MAX: 8,
  MAX_DE: 5,
  MAX_OTHERS: 12,
};

const PASSWORD = {
  /* The regex ensures that the password:
    - has no more than 2 identical characters in a row
    - has at least 3 of the following levels of complexity
        - English uppercase characters (A-Z).
        - English lowercase characters (a-z).
        - Digits (0-9).
        - Any/all Non alphanumeric (!@#$%^&*)
    - has at least 8 characters in length
  */
  MATCHES:
    /^(?!.*(.)\1\1)((?=.*[a-z])(?=.*[A-Z])(?=.*\d)|(?=.*[a-z])(?=.*[A-Z])(?=.*[!?@#$%^&*])|(?=.*[a-z])(?=.*\d)(?=.*[!!?@#$%^&*])|(?=.*[A-Z])(?=.*\d)(?=.*[!?@#$%^&*])).{8,}$/,
  MIN: 8,
  MAX: 72,
};

const CITY = {
  MATCHES: RegExp("[a-zA-Z0-9\u00E4\u00F6\u00FC\u00C4\u00D6\u00DC\u00df/'.,-]*"),
  MAX: 35,
};

const BOOKING_REFERENCE = {
  MATCHES: /^[a-zA-Z0-9]+$/g,
  MIN: 10,
  MAX: 10,
};

const BOOKING_REFERENCE_CCUI = {
  MATCHES: /^[a-zA-Z0-9]+$/g,
  MIN: 6,
  MAX: 20,
};

const BOOKING_SURNAME = {
  MATCHES: /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g,
  MIN: 2,
  MAX: 30,
};

const GUEST_SURNAME = {
  MATCHES: /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[\sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g,
  MIN: 2,
  MAX: 30,
};

const MANAGE_BOOKING_BOOKING_REFERENCE = {
  MATCHES: /^[a-zA-Z0-9]+$/g,
  MIN: 4,
  MAX: 10,
};

const MANAGE_BOOKING_MODAL_SURNAME = {
  MATCHES: RegExp(/^[^0-9:;~]+$/i),
  MIN: 2,
  MAX: 30,
};

const AGENT_OVERRIDE_MODAL_TEXT_INPUT = {
  MATCHES: RegExp(
    /^[-A-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']+-?[sA-Za-zÀ-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ ']*$/g
  ),
  MAX: 30,
};

const CCUI_COMPANY_NAME = {
  MATCHES: RegExp(/^[a-zA-Z0-9\s.,:;_!?"*%=+£$€¥&@#()\-'À-ÖØ-ʒͰ-ͳͶ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấ]+$/g),
  MIN: 2,
  MAX: 40,
};

const CCUI_EMAIL = {
  MATCHES: RegExp(
    /^[a-zÀ-ÖØ-ʒ0-9!#$&'*+=?_|-]+(?:\.[a-zÀ-ÖØ-ʒ0-9!#$&'*+=?_|-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i
  ),
  MAX: 50,
};

const CCUI_POSTAL_CODE = {
  MATCHES: RegExp(/^([A-Za-z0-9])*\s?([A-Za-z0-9])*$/),
  MIN: 2,
  MAX: 12,
};

const CCUI_THIRD_PARTY_REF = {
  MATCHES: RegExp(/^([A-Za-z0-9])*$/),
  MIN: 4,
  MAX: 20,
};

const CCUI_PHONE = {
  MATCHES: RegExp(/^\s*\+?\s*(\d[\s-]*)*$/im),
  MIN: 6,
  MAX: 25,
};

export const REFERENCES = {
  MATCHES: RegExp(/^([a-zA-Z0-9\s.-]){0,24}$/),
};

export const CUSTOM_QUESTION = {
  MATCHES: RegExp(/^([A-Za-zÀ-ÖØ-öø-ÿ0-9.,;:&()_?!/\-\s"'~#*]){0,50}$/),
};

export const DINNER_ALLOWANCE = {
  MATCHES: RegExp(/^(\d){0,3}$/),
};

export const FORM_VALIDATIONS = {
  FORM_FIELD_ERROR_LOCATION,
  FIRST_NAME,
  LAST_NAME,
  LAST_NAME_BB_GUEST_DETAILS,
  LAST_NAME_AMEND_BOOKING_GUEST_DETAILS,
  EMAIL,
  EMAIL_GD,
  BB_EMAIL_GD,
  PHONE,
  UNBRANDED_RESTAURANT_PHONE,
  LANDLINE,
  ADDRESS,
  COMPANY_NAME,
  COMPANY_NAME_ADVANCE_SPECIAL_CHARACTERS,
  POSTAL_CODE,
  PASSWORD,
  CITY,
  BOOKING_REFERENCE,
  BOOKING_REFERENCE_CCUI,
  BOOKING_SURNAME,
  GUEST_SURNAME,
  MANAGE_BOOKING_BOOKING_REFERENCE,
  MANAGE_BOOKING_MODAL_SURNAME,
  AGENT_OVERRIDE_MODAL_TEXT_INPUT,
  CCUI_COMPANY_NAME,
  CCUI_EMAIL,
  CCUI_POSTAL_CODE,
  CCUI_THIRD_PARTY_REF,
  CCUI_PHONE,
  REFERENCES,
  CUSTOM_QUESTION,
  DINNER_ALLOWANCE,
  ADULTS_CHILDREN_BY_ENQUIRY,
};
