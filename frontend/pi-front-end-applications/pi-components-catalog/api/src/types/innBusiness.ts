import { LOCALES } from '../constants';
import {
  CardManagementContentInnB,
  InnBusinessHeader,
  Footer,
  MaxNightsLim,
  MaxRoomsLim,
  MaxArrivalDateLim,
  RoomOccupancyLimitations,
  CustomerAccountDetails,
} from './graphql';
import { Language } from './header';

export type ibNavProps = {
  label: string;
  path: string;
};

export type ibNavDefaultType = {
  [key: string]: ibNavProps;
};

export type SearchRules = {
  maxNightsLimitation: MaxNightsLim;
  globalConfig: {
    maxRoomsLim: MaxRoomsLim;
  };
  maxArrivalDateLimitation: MaxArrivalDateLim;
  roomOccupancyLimitations: RoomOccupancyLimitations;
};

export type InnBusinessServerSideProps = {
  labels: InnBusinessHeader;
  icons: Record<string, string>;
  cards: CardManagementContentInnB;
  userDetails: any;
  companyDetails: any;
  footer?: Footer;
  language?: Language;
  isAccountHolder: boolean;
  users: any;
  accounts: CustomerAccountDetails[];
  spending: any;
  isCardHolder: boolean;
  searchRules: SearchRules;
  layout?: any;
  notifications?: any;
};

export type CompanyDetailsCard = {
  cardId: string;
  cardLabel: string;
  cardType: string;
  cardNumber: string;
};

export type IBFormSelectOption = {
  value: string;
  displayValue: string;
  icon?: string;
};
export type OptionType = {
  value: string;
  label: string;
  employeeData?: {
    title?: string;
    firstName?: string;
    lastName?: string;
    emailAddress?: string;
    phoneNumber?: string;
    mobileNumber?: string;
    jobTitle?: string;
    employeeIdNumber?: string;
  };
};
export type SwitchState = {
  premierInnBreakfast?: boolean;
  continentalBreakfast?: boolean;
  mealDeal?: boolean;
  hubBreakfast?: boolean;
  ultimateWifi?: boolean;
  freeChildBreakfast?: boolean;
  carParking?: boolean;
  additionalCosts?: boolean;
};
export interface FormData {
  switchState: {
    carParking: boolean;
    additionalCosts: boolean;
  };
}

export type CompanySpending = {
  year: number;
  month: string;
  bookingValue: number;
  bookingCurrency?: string;
};

export type SpendingData = {
  month: string;
  spending: number;
  bookingCurrency?: string;
};

export interface WorldlineSmsSetting {
  smsType: string;
  smsTypeId?: number | string;
  isSmsSelected: boolean;
  smsTypeDescription?: string;
}

export interface WorldlinePreferenceDetails {
  showSmsStopsToCardholder: boolean;
  sendCardsToCardholder: boolean;
}

export interface WorldlineAccountInfo {
  tetheredGuid: string;
  accountName: string;
  accountNumber: string;
  registrationRoles: string[];
}

export interface WorldlinePreference {
  tetheredUserGuid: string;
  settings: WorldlineSmsSetting[];
  details: WorldlinePreferenceDetails;
}

export interface WorldlineGraphQLResponse {
  data?: {
    getWorldlineUserPreferences?: WorldlinePreference[];
  };
}

export interface WorldlineMergedPreference {
  tetheredUserGuid: string;
  settings: WorldlineSmsSetting[];
  preferenceDetails: WorldlinePreferenceDetails;
  accountName: string;
  registrationRoles: string[];
  accountNumber: string;
}

export interface InnBusinessPayPreferencesProps {
  accountName?: string;
  accountNumber?: string;
  accountLabels: string[];
  preferences?: PreferenceItemData[];
  tetheredGuid: string;
  worldlinePostUrl: string;
  worldlineReturnUrl: string;
  icons: Record<string, string>;
}

export interface InnBusinessPaySectionProps {
  locale?: LOCALES;
  companyId?: string;
  token?: string;
  icons: Record<string, string>;
}

export interface PreferenceItemData {
  id: string;
  translationKey: string;
  value: string;
}

export interface MergedPreferenceData {
  tetheredUserGuid: string;
  settings?: WorldlineSmsSetting[];
  preferenceDetails?: {
    showSmsStopsToCardholder?: boolean | null;
    sendCardsToCardholder?: boolean | null;
  };
  accountName: string | null;
  registrationRoles: string[] | null;
  accountNumber: string;
}

export interface InnBusinessPreference {
  id: string;
  translationKey: string;
  value: string;
}

export interface InnBusinessAccount {
  accountName: string;
  accountNumber: string;
  accountLabels: string[];
  preferences: InnBusinessPreference[];
  tetheredGuid: string;
}

export interface InnBusinessUserPilotData {
  role: string;
  innBusinessPayRoles: string[];
  employeeId: string;
  companyId: string;
  companySector?: string;
  numberOfEmployees?: number;
}
