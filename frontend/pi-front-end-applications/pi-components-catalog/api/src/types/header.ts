export interface BFIQueryInput {
  bookingFlowId: string;
  country: string;
  language: string;
}

export interface BFStep {
  id: string;
  step: string;
  title: string;
}

export interface BFIResponse {
  brand: HotelBrandType | null;
  bookingFlowSteps: Array<BFStep>;
}

export interface UniqueHeaderPage {
  [key: string]: string;
}

export type SubNavCategoryItem = {
  title: string;
  url?: string;
  subMenuLinks?: SubNavItemLinks[];
  superRole?: boolean;
};

export type SubNavItemLinks = {
  title: string;
  url: string;
};

export type businessAccountsLinks = {
  subMenuLinks?: SubNavItemLinks[];
  title: string;
};

export type SubNavCategory = {
  title: string;
  navOptions: SubNavCategoryItem[];
};

export type AccountLink = {
  icon: string;
  title: string;
  url: string;
};

export type PromotionBanner = {
  enabled: boolean;
  icon: string;
  title: string;
  description: string;
  terms: string;
  srpNotificationTitle: string;
  srpNotificationText: string;
};

type BBConfigInfo = {
  api: {
    bookingChannel: {
      business: string;
      leisure: string;
    };
  };
  authentication: {
    accountLinks: {
      icon: string;
      title: string;
      url: string;
    }[];
    business: {
      businessAccountLinks: businessAccountsLinks[];
    };
    businessAccountCardRedirectPath: string;
  };
  bookingSearch: {
    show: boolean;
    dashboardRedirect: {
      bookingReference: string;
      cookie: {
        domain: string;
        minutesTillExpiry: string;
        name: string;
      };
      url: string;
    };
  };
  roomCodes: {
    single: string;
    double: string;
    accessible: string;
    twin: string;
    family: string;
  };
  promotionBanner: PromotionBanner;
};
type BBContentInfo = {
  authentication: {
    forgottenPassword: {
      business: ForgottenPasswordLabels;
      cancel: string;
      leisure: ForgottenPasswordLabels;
    };
    goBackButton: string;
    login: {
      business: LoginLabelsLeisure;
      forgotPassword: string;
      leisure: LoginLabelsLeisure;
      passwordPlaceholder: string;
      signupLink: string;
      signupMessage: string;
    };
    logoutButton: string;
    resetPassword: ResetPasswordAuthLabels;
  };
  countries: {
    flagUrl: string;
    language: string;
  }[];
  global: {
    accessible: string;
    addRoom: string;
    adult: string;
    adultsLabel: string;
    adults: string;
    brand: {
      hub: string;
      hubBadge: string;
      hubLogo: string;
      piLogo: string;
      pid: string;
      pi: string;
      pidLogo: string;
      zip: string;
      zipBadge: string;
      zipLogo: string;
    };
    child: string;
    children: string;
    childrenLabel: string;
    done: string;
    double: string;
    family: string;
    night: string;
    room: string;
    roomLabel: string;
    rooms: string;
    tomorrow: string;
    today: string;
    single: string;
    twin: string;
  };
  header: {
    image: string;
  };
  menu: {
    agentMemo: string;
    bookHotel: string;
    business: string;
    changeLogs: string;
    discoverPI: string;
    findBooking: string;
    guestAccount: string;
    language: string;
    languageButton: string;
    logIn: string;
    mobileMenuButton: string;
    tick: string;
  };
  subNav: {
    navOptions: {
      title: string;
      url: string;
    }[];
    title: string;
  }[];
};
type BBFormInfo = {
  adultsHelperText: string;
  arrivalDateLabel: string;
  bookingInvalid: string;
  bookingReferenceLabel: string;
  bookingSurnameLabel: string;
  checkout: string;
  childrenHelperText: string;
  cotLimit: string;
  findBookingDescription: string;
  findBookingTitle: string;
  includeCot: string;
  invalidFutureDate: string;
  invalidDate: string;
  invalidLocation: string;
  invalidNights: string;
  invalidPastDate: string;
  invalidReference: string;
  invalidRooms: string;
  invalidSurname: string;
  removeRoom: string;
  roomType: string;
  searchBookingError: string;
  snowdropError: string;
  snowdropErrorRetry: string;
  where: string;
};
type BBResultsInfo = {
  notifications: {
    availabilitiesErrorMessage: string;
    ccuiGroupBookingMessage: string;
    errorTitle: string;
    groupBookingHeader: string;
    groupBookingMessage: string;
    noResults: string;
    groupBookingFormPageMessage: string;
  };
};
type BBAnnouncementInfo = {
  browserCompatibilityMessage: string;
  text: string;
  type: string;
};
type BBDatePickerInfo = {
  checkOut: string;
  invalidDate: string;
  reset: string;
};

export type BBHeaderInfo = {
  announcement: BBAnnouncementInfo;
  config: BBConfigInfo;
  content: BBContentInfo;
  datePicker: BBDatePickerInfo;
  form: BBFormInfo;
  results: BBResultsInfo;
};

export type NavItems = {
  discoverPI: string;
  business: string;
  businessSubNav?: SubNavCategory;
  findBooking: string;
  logIn: string;
  logOut: string;
  subNav?: SubNavCategory[];
  accountLinks?: AccountLink[];
  mobileMenuButton?: string;
  language: string;
  signUpButton?: string;
};

export type BusinessNavItems = {
  navTitle: string;
  subNav?: SubNavCategory[];
  url?: string;
  id: string;
};

export type HotelBrandType =
  | 'pi'
  | 'pid'
  | 'pi-simple'
  | 'pid-simple'
  | 'pi-icon'
  | 'hub'
  | 'hub-simple'
  | 'zip'
  | 'zip-simple';

export const BOOKING_STEPS_MAPPING: UniqueHeaderPage = {
  ancillaries: 'ancillaries',
  guestDetails: 'guest-details',
  payment: 'payment',
  spf: 'spf',
};

export type Language = 'en' | 'de';

export interface CountryOption {
  language: string;
  flagUrl: string;
}

type LoginLabelsLeisure = {
  formLabel: string;
  emailPlaceholder: string;
  loginButton: string;
};

type LoginLabelsBusiness = {
  bookingsInvalidEmailMsg: string;
  bookingLoginRequiredText: string;
  bookingEmailMaxLengthMsg: string;
  companyActivateFailTitle: string;
  signupButton: string;
  companyActivateFailBody: string;
  tab: string;
  doubleOptInSuccessMessage: string;
  companyActivateSuccessBody: string;
  companyActivateSuccessTitle: string;
  employeeActivationSuccessTitle: string;
  loginInfoNotification: string;
  tabMobile: string;
  doubleOptInFailMessage: string;
  formLabel: string;
  emailPlaceholder: string;
  loginButton: string;
};

type BusinessAccountCardLabels = {
  banner: { imagePath: string };
  buttonLabel: string;
  tab: string;
  tabMobile: string;
  textBody: string;
  title: string;
};

type LoginAuthLabels = {
  leisure: LoginLabelsLeisure;
  business: LoginLabelsBusiness;
  businessAccountCard: BusinessAccountCardLabels;
  passwordPlaceholder: string;
  badCredentialsError: string;
  invalidEmail: string;
  forgotPassword: string;
  signupMessage: string;
  signupLink: string;
};

type ForgottenPasswordLabels = {
  formLabel: string;
  emailPlaceholder: string;
  formTitle: string;
  submitButton: string;
};

type ForgottenPasswordAuthLabels = {
  backToLogin: string;
  backToYourDetails: string;
  genericError: string;
  leisure: ForgottenPasswordLabels;
  business: ForgottenPasswordLabels;
  cancel: string;
  emailSentHeader: string;
  emailSentMessage: string;
};

export type AuthenticationLabels = {
  login: LoginAuthLabels;
  forgottenPassword: ForgottenPasswordAuthLabels;
  goBackButton: string;
  businessAccountCardRedirectPath: string;
};

type ResetPasswordAuthLabels = {
  criteria: string;
  criteriaDescription: string;
  email: string;
  invalidPassword: string;
  invalidPasswordConfirmation: string;
  logInButton: string;
  password: string;
  passwordConfirmation: string;
  passwordMin: string;
  passwordRequired: string;
  passwordRequirementsAllowed: string;
  passwordRequirementsIdentical: string;
  passwordRequirementsMin: string;
  resetPasswordTitle: string;
  submitButton: string;
  successMessage: string;
};

export type ResetPasswordLabels = {
  resetPassword: ResetPasswordAuthLabels;
  login: {
    business: {
      bookingsInvalidEmailMsg: string;
      bookingLoginRequiredText: string;
      bookingEmailMaxLengthMsg: string;
    };
    invalidEmail: string;
  };
};
