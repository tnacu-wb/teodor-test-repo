export interface AnalyticsData {
  brandID?: number;
  brandName?: string;
  currencyCode?: string;
  language?: string;
  pageName?: string;
  pageType?: string;
  pageURL?: string;
  restaurantID?: string | number;
  userID?: string;
  userLoggedIn?: string;
  allowMarketing?: boolean;
  amcv?: string;
  arrivalTime?: string;
  bookingFormState?: string;
  bookingReference?: string;
  date?: string;
  guests?: number;
  adults?: number;
  children?: number;
  isUserDataConfirmTerms?: boolean;
  isUserDataClickedOnSubmission?: boolean;
  isUserDataConfirmInput?: boolean;
  isUserDataEmail?: boolean;
  isUserDataForeName?: boolean;
  isUserDataSurName?: boolean;
  isUserDataTel?: boolean;
  isUserDataTitle?: boolean;
  menuType?: string;
  requestsComments?: string | boolean;
  additionalRequirements?: boolean;
  unavailableTimes?: string;
  sessionType?: string;
  highChairs?: number;
  wheelChairAccess?: boolean;
  largeGroupsEnquiry?: boolean;
  sessionUnavailability?: string;
}

export interface FormFields {
  adults: number;
  adultsByEnquiry: string;
  children: number;
  childrenByEnquiry: string;
  consent: boolean;
  date: string;
  emailAddress: string;
  firstname: string;
  highchair: number;
  lastname: string;
  occasionId: string;
  privacyStatement: false;
  siteId: string;
  specialRequest: string;
  telephoneNumber: string;
  time: string;
  wheelchair: boolean;
  additionalRequirements: boolean;
}
