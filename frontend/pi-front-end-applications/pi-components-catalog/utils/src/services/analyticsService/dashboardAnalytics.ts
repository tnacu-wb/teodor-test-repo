import { RESERVATION_SOURCE_PMS, RESERVATION_STATUS, SearchBooking } from '@whitbread-eos/api';

import { getNightsNumber } from '../../getters';
import analytics from './analytics';

export interface SecureBookingsAnalytics {
  bookingReference: string;
  arrivalDate: string;
  hotelCode: string;
  secureBookingAvailable: boolean;
}
export interface UpdateDashboardAnalytics {
  bookings?: SearchBooking[];
  totalResults?: number;
  inputs?: object;
  newSearch?: boolean;
  secureBookingAction?: boolean;
  secureBookingComplete?: boolean;
  secureBookings?: SecureBookingsAnalytics;
}

export const inputsMap = {
  arrivalDate: 'arrival date',
  bookerLastName: 'booking surname',
  bookingReference: 'booking ref',
  thirdPartyBookingReferenceNumber: 'third party booking ref',
  guestLastName: 'guest surname',
  bookerPostcode: 'postcode',
  bookerEmail: 'email',
  bookerPhone: 'tel num',
  cancellationDate: 'cancellation date',
  companyName: 'company name',
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const getSearchFields = (inputs: any) => {
  const searchFields = [];
  for (const key in inputs) {
    if (inputs[key]?.length && inputsMap[key as keyof typeof inputsMap]) {
      searchFields.push(inputsMap[key as keyof typeof inputsMap]);
    }
  }
  return searchFields.join(', ');
};

const updateDashboardAnalytics = ({
  bookings,
  totalResults,
  inputs,
  newSearch,
  secureBookingAction,
  secureBookingComplete,
  secureBookings,
}: UpdateDashboardAnalytics) => {
  const initialData =
    !newSearch && window?.analyticsData?.dashboard
      ? window?.analyticsData?.dashboard
      : {
          cancelledBookings: 0,
          futureBookings: 0,
          totalBookings: 0,
          totalOperaBookings: 0,
          totalBartBookings: 0,
          moreThanNineNights: 0,
          moreThanFourRooms: 0,
          searchBookingResults: '',
          bookingsReturned: 0,
          userAgentEmail: window?.analyticsData?.dashboard?.userAgentEmail ?? '',
        };
  if (initialData.bookingsReturned !== undefined && bookings) {
    const dashboard = bookings.reduce(
      (acc, entry) => {
        if (entry.status === RESERVATION_STATUS.CANCELLED) {
          acc.cancelledBookings!++;
        }
        if (entry.status === RESERVATION_STATUS.UPCOMING) {
          acc.futureBookings!++;
        }
        if (entry.sourcePms === RESERVATION_SOURCE_PMS.OPERA) {
          acc.totalOperaBookings!++;
        }
        if (entry.sourcePms === RESERVATION_SOURCE_PMS.BART) {
          acc.totalBartBookings!++;
        }
        if (getNightsNumber(entry?.arrivalDate ?? '', entry?.departureDate ?? '') > 9) {
          acc.moreThanNineNights!++;
        }
        if (entry.stayingGuests?.length > 4) {
          acc.moreThanFourRooms!++;
        }
        return acc;
      },
      {
        ...initialData,
        totalBookings: totalResults,
        bookingsReturned: initialData.bookingsReturned + bookings.length,
        searchBookingResults: getSearchFields(inputs),
      }
    );

    analytics.update({
      dashboard,
    });
  }

  if (secureBookings || secureBookingAction || secureBookingComplete) {
    const dashboard = {
      ...window?.analyticsData?.dashboard,
      bookings: secureBookings,
    };

    analytics.update({
      dashboard,
      secureBookingAction: secureBookingAction,
      secureBookingComplete: secureBookingComplete,
    });
  }
};

export default updateDashboardAnalytics;
