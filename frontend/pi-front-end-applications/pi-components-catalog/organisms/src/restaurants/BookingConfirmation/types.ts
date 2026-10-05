export interface BookingInfo {
  adults: number;
  areaId: string | null;
  areaName: string | null;
  bookingReference: string;
  braintreeCustomerId: string | null;
  cancelLink: string;
  children: number;
  consent: {
    email: boolean;
    phone: boolean;
    sms: boolean;
    postal: boolean;
    pushNotification: boolean;
    profiling: boolean;
    privacyStatement: boolean;
    consentStatement: boolean;
    termsAndConditions: boolean;
  };
  date: string;
  editLink: string;
  emailAddress: string;
  firstname: string;
  id: string;
  lastname: string;
  name: string | null;
  occasionId: string;
  occasionName: string;
  siteId: string;
  siteName: string;
  specialRequest: string | null;
  telephoneNumber: string;
  siteTimezone: string | null;
  time: string;
  turnTimeMinutes: number;
}

export interface Event {
  eventById: BookingInfo;
}

export interface Enquiry {
  enquiryById: BookingInfo;
}
