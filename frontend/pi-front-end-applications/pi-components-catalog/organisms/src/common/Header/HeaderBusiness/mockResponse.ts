export const mockedBBLabels = {
  content: {
    global: {
      addRoom: 'Add another room',
      done: 'Done',
      room: 'room',
      roomLabel: 'Room',
      single: 'Single',
      twin: 'Twin',
      accessible: 'Accessible',
      family: 'Family',
      adult: 'adult',
      adults: 'adults',
      child: 'child',
      children: 'children',
      night: 'night',
      rooms: 'rooms',
      adultsLabel: 'Adults',
      childrenLabel: 'Children',
      today: 'Today',
      tomorrow: 'Tomorrow',
      brand: {
        zipLogoWhite: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-zip-white.svg',
        piLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc.svg',
        pidLogo: null,
        zipLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-zip.svg',
        pi: 'Premier Inn',
        hub: 'Hub by Premier Inn',
        pid: null,
        zip: 'ZIP by Premier Inn',
        hubLogo: '/content/dam/pi/websites/desktop/icons/brand/pi-icon-disc-hub.svg',
        hubBadge: null,
        zipBadge: null,
      },
      double: 'Double',
    },
    menu: {
      mobileMenuButton: 'Menu',
      language: 'Language',
      business: 'About Premier Inn',
      languageButton: 'English',
      tick: '/etc/clientlibs/pi-header/resources/images/tick.svg',
      logIn: 'Log in',
      findBooking: 'Manage booking',
      bookHotel: 'Search for a hotel',
      guestAccount: null,
      changeLogs: null,
      agentMemo: null,
      discoverPI: 'Discover Premier Inn',
    },
    countries: [
      {
        language: 'English',
        flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
      },
      {
        language: 'German',
        flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
      },
    ],
    header: {
      image: '/etc/clientlibs/pi-header/resources/images/pi-refresh-logo.svg',
    },
    subNav: [
      {
        title: 'Business Customers',
        navOptions: [
          {
            title: 'Food & drink',
            url: '',
          },
          {
            title: 'Our rooms',
            url: '',
          },
          {
            title: 'Our rates',
            url: '',
          },
        ],
      },
    ],
    authentication: {
      login: {
        leisure: {
          formLabel: 'Log into your Premier Inn account',
          emailPlaceholder: 'Email address',
          loginButton: 'Log in',
        },
        business: {
          formLabel: 'Log into your Business Booker account',
          emailPlaceholder: 'Business email address',
          loginButton: 'Log into Business Booker',
        },
        passwordPlaceholder: 'Password',
        forgotPassword: 'Forgotten password?',
        signupMessage: null,
        signupLink: null,
      },
      forgottenPassword: {
        leisure: {
          formLabel: 'We will send you an email with instructions to reset your password',
          emailPlaceholder: 'Email address',
          formTitle: 'Reset your password',
          submitButton: 'Submit',
        },
        business: {
          formLabel: 'We will send you an email with instructions to reset your password',
          emailPlaceholder: 'Email address',
          formTitle: 'Reset your password',
          submitButton: 'Submit',
        },
        cancel: 'Cancel',
      },
    },
  },
  form: {
    childrenHelperText: null,
    adultsHelperText: null,
    cotLimit: null,
    includeCot: null,
    removeRoom: 'Remove room',
    checkout: 'Check out:',
    roomType: null,
    where: 'Enter place, postcode or hotel',
    invalidLocation: 'Please enter a location or a hotel',
    invalidPastDate: 'Your selected date is in the past, rates below are for availability today',
    invalidDate: 'Please enter a valid date',
    invalidNights: 'Please enter a valid number of nights',
    invalidRooms: 'Please enter a valid room composition',
    snowdropError: 'Oh dear, something went wrong when we tried to load the suggestions.',
    snowdropErrorRetry: 'Retry',
    invalidFutureDate:
      'Unable to book over a year in advance, rates below are for availability today',
    findBookingTitle: 'Amend or cancel a booking',
    findBookingDescription:
      "Please enter your details below. You'll find your booking reference in your confirmation email.",
    bookingReferenceLabel: 'Booking reference *',
    bookingSurnameLabel: 'Booking surname *',
    arrivalDateLabel: 'Arrival date *',
    invalidReference: 'Invalid reference',
    invalidSurname:
      'Must be at least 2 characters long – any letters and special characters (except :;~)',
    searchBookingError: null,
    bookingInvalid: 'Some of these details are missing or invalid. Please provide correct details',
  },
  datePicker: {
    reset: null,
    invalidDate: 'Invalid Date',
    checkOut: null,
  },
  results: {
    notifications: {
      groupBookingHeader: null,
      groupBookingMessage:
        'If you’d like to book five rooms or more, please call us and we’ll be happy to help.',
      ccuiGroupBookingMessage: null,
      noResults:
        'Sorry, we couldn’t find any hotels with available rooms. Try changing your search area.',
      availabilitiesErrorMessage: null,
      errorTitle: null,
      groupBookingFormPageMessage:
        'To make a group booking of 5 to 9 rooms, contact us via Live Chat for guidance. To book 10 rooms or more, please complete the <a href="/why/groups/form{groupBookingLink}">group booking form</a> and we will be in contact to discuss your enquiry.',
    },
  },
  announcement: {
    text: 'We have removed all our hotels from sale up until 30th April 2020.  This is in response to the government request to close all hotels.  We will review this on an ongoing basis and will update our ',
    type: null,
    browserCompatibilityMessage:
      '<p>Looks like your web browser isn’t supported. Try downloading <a href="https://www.google.com/chrome/">Google Chrome</a>, <a href="https://www.microsoft.com/en-us/edge">Microsoft Edge</a> or <a href="https://www.mozilla.org/en-CA/firefox/new/">Firefox</a> for a better online experience with us.</p>',
  },
  config: {
    roomCodes: {
      family: 'FAM',
      accessible: 'DIS',
      single: 'SB',
      twin: 'TWIN',
      double: 'DB',
    },
    api: {
      bookingChannel: {
        business: 'CBT',
        leisure: 'WEB',
      },
    },
    bookingSearch: {
      show: true,
      dashboardRedirect: {
        bookingReference: 'bookingReference',
        url: '/gb/en/business-booker/account/dashboard.html',
        operaUrl: '/gb/en/business-booker/account/dashboard',
        cookie: {
          domain: 'premierinn.com',
          name: 'pi.single-booking',
          minutesTillExpiry: '30',
        },
      },
    },
    authentication: {
      accountLinks: [
        {
          title: 'Bookings',
          url: '/gb/en/business-booker/account/dashboard.html',
          icon: 'bookings',
        },
        {
          title: 'Account settings',
          url: '/gb/en/business-booker/account/profile.html',
          icon: 'settings',
        },
      ],
      business: {
        businessAccountLinks: [
          {
            title: 'Company management',
            subMenuLinks: [
              {
                title: 'Manage employees',
                url: '/gb/en/business-booker/account/company-management.html#/manage-employees',
              },
              {
                title: 'Booking Allowances',
                url: '/gb/en/business-booker/account/company-management.html#/booking-allowances',
              },
              {
                title: 'Booking Alerts',
                url: '/gb/en/business-booker/account/company-management.html#/booking-alerts',
              },
              {
                title: 'Payment options',
                url: '/gb/en/business-booker/account/company-management.html#/payment-options',
              },
              {
                title: 'Employee Questions',
                url: '/gb/en/business-booker/account/company-management.html#/employee-questions',
              },
              {
                title: 'Company details',
                url: '/gb/en/business-booker/account/company-management.html#/company-details',
              },
            ],
          },
          {
            title: 'Reporting',
            subMenuLinks: [
              {
                title: 'Management Information Report',
                url: '/gb/en/business-booker/account/reporting.html#/management-information-report',
              },
              {
                title: 'Emergency report',
                url: '/gb/en/business-booker/account/reporting.html#/emergency-report',
              },
              {
                title: 'Out of policy report',
                url: '/gb/en/business-booker/account/reporting.html#/out-of-policy-report',
              },
            ],
          },
        ],
      },
    },
  },
};

export const mobileHeaderLabels = [
  {
    navTitle: 'Bills Group LTD',
    subNav: [],
    id: 'Company name',
  },
  {
    navTitle: 'Bookings',
    subNav: [
      {
        navOptions: [],
        title: 'Bookings',
      },
    ],
    id: 'Company',
    url: '/gb/en/business-booker/account/dashboard.html',
  },
  {
    navTitle: 'Account settings',
    subNav: [
      {
        navOptions: [],
        title: 'Account settings',
      },
    ],
    id: 'Company',
    url: '/gb/en/business-booker/account/profile.html',
  },
  {
    navTitle: 'Company management',
    subNav: [
      {
        navOptions: [
          {
            title: 'Manage employees',
            url: '/gb/en/business-booker/account/company-management.html#/manage-employees',
          },
          {
            title: 'Booking Allowances',
            url: '/gb/en/business-booker/account/company-management.html#/booking-allowances',
          },
          {
            title: 'Booking Alerts',
            url: '/gb/en/business-booker/account/company-management.html#/booking-alerts',
          },
          {
            title: 'Payment options',
            url: '/gb/en/business-booker/account/company-management.html#/payment-options',
          },
          {
            title: 'Employee Questions',
            url: '/gb/en/business-booker/account/company-management.html#/employee-questions',
          },
          {
            title: 'Company details',
            url: '/gb/en/business-booker/account/company-management.html#/company-details',
          },
        ],
        title: 'Company management',
      },
    ],
    id: 'Company',
  },
  {
    navTitle: 'Reporting',
    subNav: [
      {
        navOptions: [
          {
            title: 'Management Information Report',
            url: '/gb/en/business-booker/account/reporting.html#/management-information-report',
          },
          {
            title: 'Emergency report',
            url: '/gb/en/business-booker/account/reporting.html#/emergency-report',
          },
          {
            title: 'Out of policy report',
            url: '/gb/en/business-booker/account/reporting.html#/out-of-policy-report',
          },
        ],
        title: 'Reporting',
      },
    ],
    id: 'Company',
  },
  {
    navTitle: 'Log out',
    subNav: [
      {
        navOptions: [],
        title: 'Log out',
      },
    ],
    id: 'Company',
  },
  {
    navTitle: 'About Premier Inn',
    subNav: [
      {
        navOptions: [
          {
            title: 'Food & drink',
            url: '/gb/en/business-booker/why/food.html?INTCMP=BBtopNav',
          },
          {
            title: 'Our rooms',
            url: '/gb/en/business-booker/sleep/our-rooms.html?INTCMP=BBtopNav',
          },
          {
            title: 'Our rates',
            url: '/gb/en/business-booker/why/rates.html?INTCMP=BBtopNav',
          },
        ],
        title: 'Business Customers',
      },
    ],
    id: 'About',
  },
  {
    navTitle: 'Business Account',
    subNav: [
      {
        navOptions: [
          {
            title: 'About Business account',
            url: '/',
          },
          {
            title: 'Account dashboard',
            url: '/',
          },
          {
            title: 'PIBA registration',
            url: '/',
          },
        ],
        title: 'Business Account',
      },
    ],
    id: 'Business',
  },
];
