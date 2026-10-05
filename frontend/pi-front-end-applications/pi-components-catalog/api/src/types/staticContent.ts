import { HeaderInformation } from './graphql';

export interface StaticContent {
  labels: StaticContentLabels;
}

export interface StaticContentLabels {
  main: string;
  piBookings: string;
  booking: string;
  piPreCheckIn: string;
  piGroupBooking: string;
  extras: string;
  promotions?: string;
}

export type HeaderInformationData = {
  headerInformation: HeaderInformation;
};

export type HeaderInformationQuery = {
  data: HeaderInformationData;
  isError: boolean;
  isLoading: boolean;
  error: unknown;
};
