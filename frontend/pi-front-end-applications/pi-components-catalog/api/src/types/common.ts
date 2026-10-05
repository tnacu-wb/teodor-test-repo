import { LOCALES } from '../constants';
import { RegistrationRole } from './index';

export type Claims = Record<string, any>;

export type CurrencyType = 'GBP' | 'EUR';

export type Country = {
  countryCode: string;
  countryCodeLegacy?: string;
  countryName: string;
  passportRequired?: boolean;
  dialingCode: string;
  flagSrc: string;
  nationality?: string;
};

export type CountryWithIso = {
  countryCode: string;
  countryCodeISO: string;
  countryLegend: string;
  dialingCode: string;
  flagImg: string;
};

export type DatepickerRangeSelectionDate = [Date | null, Date | null];
export type DatepickerSelectionDate = Date | DatepickerRangeSelectionDate;

type SEOIcons = {
  rel: string;
  sizes: string;
  href: string;
};

type SEOMsIcons = {
  name: string;
  content: string;
};

export type SEOHrefLangs = {
  hreflang: string;
  href: string;
};

export type SEOInformation = {
  pageTitle: string;
  pageDescription: string;
  cardImageUrl: string;
  faviconUrl: string;
  icons: SEOIcons[];
  msIcons: SEOMsIcons[];
  faq: FAQ;
  hreflangs: SEOHrefLangs[];
  geoJsonLd: any;
};

export type FAQ = {
  title: string;
  faqItems: FAQItem[];
};

export type FAQItem = {
  question: string;
  answer: string;
};

export type SEOBreadcrumbItem = {
  position: number;
  title: string;
  link: string;
};

export type KeyValuePair = {
  key: string;
  value: string;
};

export type RoomTypeInformation = {
  roomTypeCode: string[];
  roomCategory: string;
  roomLabel: string;
  roomDescription: string;
  roomImage: string;
  groupId: string;
};

export type RoomTypeCodeMapped = {
  DB: 'Double';
  DIS: 'Accessible';
  FAM: 'Family';
  SB: 'Single';
  TWIN: 'Twin';
  [key: string]: string;
};

export type ReservationRoomType = {
  roomLabelCode: string;
  silentSubstitution: boolean;
  adults: number;
  children: number;
  pmsRoomType: string;
  filtered?: boolean;
  standardRoomType?: string;
};

export type SilentSubstitutionLocalStorage = {
  value: ReservationRoomType[];
  expire: number;
};

export type MutationErrorResponse = {
  data: null;
  response: {
    errors: {
      data: null;
      errorInfo: null;
      errorType: string;
      locations: { line: number; column: number; sourceName: null }[];
      message: string;
    }[];
  };
};

export type ListIndex = number | string | undefined;
export type ErrorRequestType = {
  status: number;
};

export type ErrorDataType = {
  details: Array<string>;
};

export type ErrorResponseType = {
  data: ErrorDataType;
};

export type ErrorType = {
  request: ErrorRequestType;
  response: ErrorResponseType;
};

export type CountryLanguage = {
  country: 'gb' | 'de';
  language: 'en' | 'de';
};

export type PathParams = {
  locale?: LOCALES.EN | LOCALES.DE;
};

export type SearchParams = {
  [key: string]: string | string[] | undefined;
};

export type BreadcrumbItem = {
  url: string;
  name: string;
  isCurrentPage?: boolean;
};

export type ResponsiveValue = {
  mobile: string;
  xs: string;
  sm: string;
  md: string;
  lg: string;
  xl: string;
};

export type RolesList = {
  wl?: RegistrationRole[];
  bb?: string[];
};
