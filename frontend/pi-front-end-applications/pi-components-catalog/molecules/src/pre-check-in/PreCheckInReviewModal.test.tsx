import '@testing-library/jest-dom';
import {
  getFindBookingToken,
  useIPageSubmission,
  useMutationRequest,
  useQueryRequest,
} from '@whitbread-eos/utils';
import * as React from 'react';

import { fireEvent, render, userEvent } from '../utils/test-utils';
import PreCheckInReviewModal from './PreCheckInReviewModal';

const bookingConfirmation = {
  reservationByIdList: [
    {
      reservationId: '1697820',
      billing: {
        address: {
          addressLine1: 'Ashdown House, Destinations Place',
          postalCode: 'RH6 0NP',
        },
        title: 'Mr',
        telephone: '+447008652622',
        firstName: 'Subhendu',
        lastName: 'Das',
        email: 'subhendu.das@whitbread.com',
      },
      reservationGuestList: [
        {
          givenName: 'Subhendu',
          surName: 'Dash',
          email: null,
          nameTitle: 'Mr',
          additionalDetails: {
            dob: null,
            passportNumber: null,
            nationality: null,
          },
          address: {
            addressType: null,
            addressLine1: null,
            addressLine2: null,
            cityName: null,
            countryCode: null,
            postalCode: null,
          },
        },
      ],
      gdsReferenceNumber: null,
      roomStay: {
        arrivalDate: '2024-06-30',
        departureDate: '2024-07-01',
        childrenNumber: 0,
        adultsNumber: 1,
        roomType: 'DOUBLE',
        roomExtraInfo: {
          roomName: 'Double room',
        },
      },
    },
    {
      reservationId: '1697819',
      billing: {
        address: {
          addressLine1: 'Ashdown House, Destinations Place',
          postalCode: 'RH6 0NP',
        },
        title: 'Mr',
        telephone: '+447008652622',
        firstName: 'Subhendu',
        lastName: 'Das',
        email: 'subhendu.das@whitbread.com',
      },
      reservationGuestList: [
        {
          givenName: 'Naveen',
          surName: 'Kumar',
          email: null,
          nameTitle: 'Mr',
          additionalDetails: {
            dob: null,
            passportNumber: null,
            nationality: null,
          },
          address: {
            addressType: null,
            addressLine1: null,
            addressLine2: null,
            cityName: null,
            countryCode: null,
            postalCode: null,
          },
        },
      ],
      gdsReferenceNumber: null,
      roomStay: {
        arrivalDate: '2024-06-30',
        departureDate: '2024-07-01',
        childrenNumber: 0,
        adultsNumber: 1,
        roomType: 'DOUBLE',
        roomExtraInfo: {
          roomName: 'Double room',
        },
      },
    },
  ],
  hotelId: 'STUAIR',
  hotelName: 'Stuttgart Airport Messe',
  bookingFlowId: 'booking-ct-a1',
  rateMessage: '<p>Flex: Amend or cancel up to 1pm on arrival day</p>\n',
  bookingReference: 'GBH4433004',
  basketReference: 'GBH-892c7eda-2d34-4d8c-8faf-f94f6d66f399',
  balanceOutstanding: '0',
  currencyCode: 'USD',
  infoMessages: [],
  newTotal: '0',
};

const mockGetCountriesData = {
  data: {
    countries: {
      countries: [
        {
          countryCode: 'GB',
          countryCodeLegacy: 'GB',
          nationality: 'British, UK',
          dialingCode: '+44',
          flagSrc: '',
          passportRequired: true,
        },
        {
          countryCode: 'DE',
          countryCodeLegacy: 'D',
          nationality: 'German',
          dialingCode: '+49',
          flagSrc: '',
          passportRequired: true,
        },
      ],
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error country selection',
  },
};
const mockFindBookingToken = {
  basketReference: '1234',
  bookingReference: '123',
  token: 'token',
};
const mockUseRouter = jest.fn().mockReturnValue({
  query: {},
  push: jest.fn(),
});
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));
window.scrollTo = jest.fn();

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMultiStyleConfig: jest.fn(() => ({ field: { _focus: {} } })),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQuery: () => jest.fn(),
  useQueryRequest: jest.fn(() => ({
    data: mockGetCountriesData,
    isSuccess: true,
  })),
  useMutationRequest: jest.fn(() => ({
    data: {},
    mutation: {
      mutateAsync: jest.fn().mockImplementation(() => ({})),
    },
    isSuccess: true,
  })),
  useIPageSubmission: jest.fn(() => ({
    isPaymentComplete: true,
    paymentStatus: 'SUCCESS',
    setIsPaymentComplete: jest.fn(),
  })),
  useFeatureSwitch: () => true,
  analytics: {
    update: jest.fn(),
  },
  getFindBookingToken: jest.fn(() => mockFindBookingToken),
}));
global.URL.createObjectURL = jest.fn();
window.open = jest.fn();
HTMLCanvasElement.prototype.toDataURL = jest.fn(() => 'data:image/png;base64,');
HTMLCanvasElement.prototype.getContext = jest.fn();

const mockBookingData = {
  bookingReference: '12345678',
  hotelName: 'Frankfurt Messe',
  firstName: 'John',
  lastName: 'Smith',
  arrivalDate: new Date('2024-05-10T18:30:00.000Z'),
  departureDate: new Date('2024-05-11T18:30:00.000Z'),
  address: '105 LONDON STREET',
  city: 'London',
  country: 'GB',
  postalCode: 'AA9A 9AA',
  dateOfBirth: new Date('2024-05-11T18:30:00.000Z'),
  nationality: { value: 'US', label: 'American' },
  passport: 'ABCDEFGH123DE',
  dependents: [
    {
      firstname: 'Tom',
      lastname: 'Gerry',
      dateofbirth: new Date('2024-05-11T18:30:00.000Z'),
      nationality: { value: 'US', label: 'American' },
      passport: 'ABCDEFGH123DA',
      testField: 'testField',
    },
  ],
  scheduledDate: new Date('2024-05-11T18:30:00.000Z'),
  dependent: '0',
  rooms: 1,
  noOfRooms: 2,
  roomNo: 3,
};

const mockBookingDataWithNoRooms = { ...mockBookingData, rooms: 0 };
const mockBookingDataWithNoGuests = {
  ...mockBookingData,
  dependents: [],
};

const mockBookingDataWithNoLeadGuests = {};

const paymentData = {
  initiatePayment: {
    status: 'PAYMENT_REQUIRED',
    paymentRequiredDetails: {
      paymentRedirect:
        'PCFET0NUWVBFIGh0bWw+CjxodG1sPgo8Ym9keSBzdHlsZT0icG9zaXRpb246IGFic29sdXRlOyB0b3A6IDUwJTsgdHJhbnNmb3JtOiB0cmFuc2xhdGVZKC01MCUpOyB3aWR0aDogMTAwJTsiPgo8ZGl2IHN0eWxlPSJtYXJnaW46IDAgYXV0bzt3aWR0aDogODBweDsiPgogICAgPHN2ZyB3aWR0aD0nODBweCcgaGVpZ2h0PSc4MHB4JyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAxMDAgMTAwIgogICAgICAgICBwcmVzZXJ2ZUFzcGVjdFJhdGlvPSJ4TWlkWU1pZCIgY2xhc3M9InVpbC1zcGluIj4KICAgICAgICA8cmVjdCB4PSIwIiB5PSIwIiB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgZmlsbD0ibm9uZSIgY2xhc3M9ImJrIj48L3JlY3Q+CiAgICAgICAgPGcgdHJhbnNmb3JtPSJ0cmFuc2xhdGUoNTAgNTApIj4KICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJyb3RhdGUoMCkgdHJhbnNsYXRlKDM0IDApIj4KICAgICAgICAgICAgICAgIDxjaXJjbGUgY3g9IjAiIGN5PSIwIiByPSI4IiBmaWxsPSIjM2M4NjhiIj4KICAgICAgICAgICAgICAgICAgICA8YW5pbWF0ZSBhdHRyaWJ1dGVOYW1lPSJvcGFjaXR5IiBmcm9tPSIxIiB0bz0iMC4xIiBiZWdpbj0iMHMiIGR1cj0iMXMiCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiPjwvYW5pbWF0ZT4KICAgICAgICAgICAgICAgICAgICA8YW5pbWF0ZVRyYW5zZm9ybSBhdHRyaWJ1dGVOYW1lPSJ0cmFuc2Zvcm0iIHR5cGU9InNjYWxlIiBmcm9tPSIxLjQiIHRvPSIxIiBiZWdpbj0iMHMiIGR1cj0iMXMiCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiPjwvYW5pbWF0ZVRyYW5zZm9ybT4KICAgICAgICAgICAgICAgIDwvY2lyY2xlPgogICAgICAgICAgICA8L2c+CiAgICAgICAgICAgIDxnIHRyYW5zZm9ybT0icm90YXRlKDQ1KSB0cmFuc2xhdGUoMzQgMCkiPgogICAgICAgICAgICAgICAgPGNpcmNsZSBjeD0iMCIgY3k9IjAiIHI9IjgiIGZpbGw9IiMzYzg2OGIiPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9Im9wYWNpdHkiIGZyb209IjEiIHRvPSIwLjEiIGJlZ2luPSIwLjEycyIgZHVyPSIxcyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlVHJhbnNmb3JtIGF0dHJpYnV0ZU5hbWU9InRyYW5zZm9ybSIgdHlwZT0ic2NhbGUiIGZyb209IjEuNCIgdG89IjEiIGJlZ2luPSIwLjEycyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBkdXI9IjFzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlVHJhbnNmb3JtPgogICAgICAgICAgICAgICAgPC9jaXJjbGU+CiAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJyb3RhdGUoOTApIHRyYW5zbGF0ZSgzNCAwKSI+CiAgICAgICAgICAgICAgICA8Y2lyY2xlIGN4PSIwIiBjeT0iMCIgcj0iOCIgZmlsbD0iIzNjODY4YiI+CiAgICAgICAgICAgICAgICAgICAgPGFuaW1hdGUgYXR0cmlidXRlTmFtZT0ib3BhY2l0eSIgZnJvbT0iMSIgdG89IjAuMSIgYmVnaW49IjAuMjVzIiBkdXI9IjFzIgogICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIj48L2FuaW1hdGU+CiAgICAgICAgICAgICAgICAgICAgPGFuaW1hdGVUcmFuc2Zvcm0gYXR0cmlidXRlTmFtZT0idHJhbnNmb3JtIiB0eXBlPSJzY2FsZSIgZnJvbT0iMS40IiB0bz0iMSIgYmVnaW49IjAuMjVzIgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGR1cj0iMXMiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIj48L2FuaW1hdGVUcmFuc2Zvcm0+CiAgICAgICAgICAgICAgICA8L2NpcmNsZT4KICAgICAgICAgICAgPC9nPgogICAgICAgICAgICA8ZyB0cmFuc2Zvcm09InJvdGF0ZSgxMzUpIHRyYW5zbGF0ZSgzNCAwKSI+CiAgICAgICAgICAgICAgICA8Y2lyY2xlIGN4PSIwIiBjeT0iMCIgcj0iOCIgZmlsbD0iIzNjODY4YiI+CiAgICAgICAgICAgICAgICAgICAgPGFuaW1hdGUgYXR0cmlidXRlTmFtZT0ib3BhY2l0eSIgZnJvbT0iMSIgdG89IjAuMSIgYmVnaW49IjAuMzdzIiBkdXI9IjFzIgogICAgICAgICAgICAgICAgICAgICAgICAgICAgIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIj48L2FuaW1hdGU+CiAgICAgICAgICAgICAgICAgICAgPGFuaW1hdGVUcmFuc2Zvcm0gYXR0cmlidXRlTmFtZT0idHJhbnNmb3JtIiB0eXBlPSJzY2FsZSIgZnJvbT0iMS40IiB0bz0iMSIgYmVnaW49IjAuMzdzIgogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIGR1cj0iMXMiIHJlcGVhdENvdW50PSJpbmRlZmluaXRlIj48L2FuaW1hdGVUcmFuc2Zvcm0+CiAgICAgICAgICAgICAgICA8L2NpcmNsZT4KICAgICAgICAgICAgPC9nPgogICAgICAgICAgICA8ZyB0cmFuc2Zvcm09InJvdGF0ZSgxODApIHRyYW5zbGF0ZSgzNCAwKSI+CiAgICAgICAgICAgICAgICA8Y2lyY2xlIGN4PSIwIiBjeT0iMCIgcj0iOCIgZmlsbD0iIzNjODY4YiI+CiAgICAgICAgICAgICAgICAgICAgPGFuaW1hdGUgYXR0cmlidXRlTmFtZT0ib3BhY2l0eSIgZnJvbT0iMSIgdG89IjAuMSIgYmVnaW49IjAuNXMiIGR1cj0iMXMiCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgcmVwZWF0Q291bnQ9ImluZGVmaW5pdGUiPjwvYW5pbWF0ZT4KICAgICAgICAgICAgICAgICAgICA8YW5pbWF0ZVRyYW5zZm9ybSBhdHRyaWJ1dGVOYW1lPSJ0cmFuc2Zvcm0iIHR5cGU9InNjYWxlIiBmcm9tPSIxLjQiIHRvPSIxIiBiZWdpbj0iMC41cyIgZHVyPSIxcyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlVHJhbnNmb3JtPgogICAgICAgICAgICAgICAgPC9jaXJjbGU+CiAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJyb3RhdGUoMjI1KSB0cmFuc2xhdGUoMzQgMCkiPgogICAgICAgICAgICAgICAgPGNpcmNsZSBjeD0iMCIgY3k9IjAiIHI9IjgiIGZpbGw9IiMzYzg2OGIiPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9Im9wYWNpdHkiIGZyb209IjEiIHRvPSIwLjEiIGJlZ2luPSIwLjYycyIgZHVyPSIxcyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlVHJhbnNmb3JtIGF0dHJpYnV0ZU5hbWU9InRyYW5zZm9ybSIgdHlwZT0ic2NhbGUiIGZyb209IjEuNCIgdG89IjEiIGJlZ2luPSIwLjYycyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBkdXI9IjFzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlVHJhbnNmb3JtPgogICAgICAgICAgICAgICAgPC9jaXJjbGU+CiAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJyb3RhdGUoMjcwKSB0cmFuc2xhdGUoMzQgMCkiPgogICAgICAgICAgICAgICAgPGNpcmNsZSBjeD0iMCIgY3k9IjAiIHI9IjgiIGZpbGw9IiMzYzg2OGIiPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9Im9wYWNpdHkiIGZyb209IjEiIHRvPSIwLjEiIGJlZ2luPSIwLjc1cyIgZHVyPSIxcyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlVHJhbnNmb3JtIGF0dHJpYnV0ZU5hbWU9InRyYW5zZm9ybSIgdHlwZT0ic2NhbGUiIGZyb209IjEuNCIgdG89IjEiIGJlZ2luPSIwLjc1cyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBkdXI9IjFzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlVHJhbnNmb3JtPgogICAgICAgICAgICAgICAgPC9jaXJjbGU+CiAgICAgICAgICAgIDwvZz4KICAgICAgICAgICAgPGcgdHJhbnNmb3JtPSJyb3RhdGUoMzE1KSB0cmFuc2xhdGUoMzQgMCkiPgogICAgICAgICAgICAgICAgPGNpcmNsZSBjeD0iMCIgY3k9IjAiIHI9IjgiIGZpbGw9IiMzYzg2OGIiPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlIGF0dHJpYnV0ZU5hbWU9Im9wYWNpdHkiIGZyb209IjEiIHRvPSIwLjEiIGJlZ2luPSIwLjg3cyIgZHVyPSIxcyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlPgogICAgICAgICAgICAgICAgICAgIDxhbmltYXRlVHJhbnNmb3JtIGF0dHJpYnV0ZU5hbWU9InRyYW5zZm9ybSIgdHlwZT0ic2NhbGUiIGZyb209IjEuNCIgdG89IjEiIGJlZ2luPSIwLjg3cyIKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICBkdXI9IjFzIiByZXBlYXRDb3VudD0iaW5kZWZpbml0ZSI+PC9hbmltYXRlVHJhbnNmb3JtPgogICAgICAgICAgICAgICAgPC9jaXJjbGU+CiAgICAgICAgICAgIDwvZz4KICAgICAgICA8L2c+CiAgICA8L3N2Zz4KPC9kaXY+CjxoMSBzdHlsZT0idGV4dC1hbGlnbjogY2VudGVyOyIgY2xhc3M9IndiLWhlYWRpbmctLWgzIj5Mb2FkaW5nLi4uPC9oMT4KPGZvcm0gbmFtZT0iRm9ybTEiIGlkPSJGb3JtMSIgbWV0aG9kPSJwb3N0IiBhY3Rpb249Imh0dHBzOi8vd2ViMnBheXVhdC4zY2ludC5jb20vaVBhZ2UvU2VydmljZS9fMjAwNl8wNV92MV8wXzEvc2VydmljZS5hc3B4Ij4KICAgIDxpbnB1dCB0eXBlPSJoaWRkZW4iIG5hbWU9IlhYWF9JUEdTRVNTSU9OX1hYWCIgdmFsdWU9IjZZNzFOZWFEMW9IR3Q4ZXFtdmxSUDJWOFJWeDdDYTFjT09Yb2VnN1BDYmhFMkpLZUpNRzJhTEd4RVBMZmtNZHoxdnRrVVIwYXJhTEl3ci9QN2t6eG92akFxbXIycnpkdysvekszWnZYaGJSTGdpSS92S2ZBeU5ZTXFpYU8vL0dGZXd4b1pjZ2x5aTJXZWwzUUtSZmR4dDlaUGhBc3ZKZE9KT0Z5WTV3OGxtYVIya1EyVlNZOFpzZ3ZXSzdwRzQ3OGk2UmV3VTNvcU1kQ0pmOC9IeDFFU0swMjUxR0hwcHlMVXlpUDlwUmNzOHBMaHJvR0hOS0J6MGFKME8xbCs5eGdSeW1yaWVzZTBITjc1RGdqTGN0TnlCcWhHOFlpOFZ0Rkt6K0xZcWtBM2N5YUJXbER5WXVmOWEyV2I5L1dySURvZzlwbVVCWmYrcmJiM1VOWnRNcitMa2xvRWpOcHhZZzZhcDhxTHhxdHdWSW1wS2tPUldLUk12WkkxazNxZ0FjbVFuT1E1OVFSczRBV2JKbnRGTGZVTGY2WXZYWkYrc0ZMRTUzeWU5empOZndlTjI1TENCc2RnRU1MVEdOd04yejJibmJ3V0RWdkY2cHlEa1NUMTZVZGNSYkZkVFV6RFZMTGFKY2ZtUkZiUy91N0Y2ek95QTQ4S3RkS3hYTkt0QXBUa1N5cGF1WGQvcHZPOW1YV3Y1VTR3VzNVMHNYMk1ZMmNob2MvVVZxZHZHaEFCY3k2TitmeFgrUGRRRDlyTjhBWTFkSUlxT2JTaDZWZlA0N3gxcjdNTG1pUVZObGYrakVKN0dnWlZETVNFL1BVMEQvWldZajRObTEzUXBXdlgzMk4zTGhid2tHajRJTTJpclFiMk1NQm5ZYWpUS3BKSGFjRXZDdFZhN1RjRDYyQ2V2SzVGNnNkZkRpdUNaUnNDVnRLOEFuczU5a205RjhLM3ZWbEVibS9PTFNkVUtDMlV3VmtrWERnc3FnQjk0anZjZlo5bzE5TUhuVFB3U3lpdE5XRTVYaXBSTlVYbVhaWjhvTnhaMkVmdVl1YU1tTmhEek1LbzgrUHgwbk5VdTI1ejVzRWI5WktXbVJJK29OZWZnWEVadklCSHNoaVpQSFU4NXlWWnBiYUV5Rk53MERYWGhveUYvQ1dNSUJHeVBPOGVCZGl4VVluUXBUZGNIRjdhaUdsUTgwaGd4WmlWK2JSS0FROEttbmYvMURIUW91MGdxVnNpNEswNlNXVzh1dk1ESkorUmRZbnF5TjJUZnlNWVFZL2x5ZFJ5aHk5R2cveWR5S0hTOUtZdnlyclBVZ0JIVDljbjJRNVZZbnBnaFlPRjVLT3BxUEx0WDR6cTJDWlRESndFRmxwQUZOaDQ1b1FkTUtTQWNSRUR2WktMcWxpVmViWVFWYmNGL1hUaXo1S0MzL0FZRTVMdXp2R0lHY1dxVHJoUENuaUxJeEFiY1RMNm5pTnRCdk54TVpzWDR0c05UdTY0NEZCbWw4ZzlqR0k3eHQ5OEtiekVPUHpNcjd5S2RYbVM2dnFEVUFCZUU4MTZmOE9rai9MZFNOdXdBanVZQk04N1BJdVJERCtieUFvaExGSXZrZG54ZlNIR0RnZnlSL1UzWlF4NmQxZEo3UzJZZ1UyWm9nRzZ3dnFLUEZXWGlmRXF3b1cvVklBR2g1ck9HaVlHblZFWlVVajJkU25FRk50MnNOeFpIeDJQSEZFZ25WaHNtRlRBRGJyY0prK0dKc3NUQ0Y1ZGRUMGJYWHl6SmMzcWwwQ09lTkhiamxianl5ZC9wdE9LY1ZYb3pETlUwTDR6NndxdVJsZVk0dHZsK1F3K0pGcHp1VzNaazB6Vzl6ZGk3UzI2TXBPbjZMdHpmdUJMQmVlZEkvdk8vUXhRanBIREtMZTZxTHpEM2FpTzVYbGZ6R2xiK3p3UkJNc1ArVTM0TDJNcHBURW43RDNrM3JndTk1UFZxdERjTGZuWUN2akZxWkRoZVdDY1Rvbnl1K0RSZVhlUHlIY29odFZIcExGYmZPNEdSQzFpNkZIdlV3SVNGcEdBUUF6S25Dc2h4Tk1RVFUxNEJUaEJpSS8yZXJoQzhJTGZ4TVlKM0s4K2gzbTdmNkg0ekFrZW9ZbEdNNE16dGhoMFpYZTBHejFNaHR3U2RpRkcxSWprTG9YekEyZytYUHYwYUlSNGlFMWppNHphL0RWQ0x6bmlINEcwdVhWRkhXWjVxMzMvcmFqNW9JaWJheFNsR2NsdnZ2Tm0wZk5VbWJMYjU4cFFnenowWjY3em5ZZWtzNU0yNHBPL0JGN01HK0dha2EzQld5U1VQNXpjeU01TEJUOHRZY3NNeWJkYWpldzAxWExDL3FTZVBrbVU0aTQ1RDlPTld3ODNCSVZOaDdxWnh0VVpzKzYxWjgyUnkxTlZLbGV4UDJjZUtKUVRnbSt3dmhwVktwOEhvajVqdXpzWHd1OStLYk8wRzNJNjVUNlIzTC82OGVCTy9ZWWJTOWlGZitZYzA1MnA5N3ZMZ2w1OFhPU3orM2pVbU10VmgzZStxNkdxdGdkM1NBY2MyNG5wbTZ5K1hwcndycC9McFBLY2wrYTVHRUJ4QzdxNExVNGRMcmhkMG1RK3RtVHlLaWlwUDlVVExYR3YxMUdKWDZIN3U0d0JMSUFtMk1kUjNHZ25keWt4NXJjcmo2UFRKQldHNDBzdTJ2UENWNlpyRnpIZzBIVndzZFZQZTJiRzA4a0ZvZzRMTm5yVVpVNGlJUUFYTktxVUM0NUlJTDgydURWRWpaV04vTThXL0NnRmxaK2F0djVDclJvRXFLT0wwODlNNy9VMkZqSVVva1Bld1BlZngvaHJkUDZkY3U2eTNhTGhBYnJ6ako4anpIbjlyRFF3OVl6VkRLUWVzMHVjcWJGK2VqUU1Eb3ZndzBUS3BwQSt3V3NNcncvOVo1a3d6cG1oOWpJMm1jZFA2VXR3eXF3eHQ0SjRUUWtnU1daT2tEVHJraERma0NGMmE1NWRxdG14UmlUTHViRXkyamtwYUR6bDA5SUlOdWJrQ1FHTDUvaWk1aUlOM3JiMHgwUi9tSVdxdFlLUmtDSEtvTU9WV0FES3k0Ti8vSjh5aldIdnVTcU1vNllRZ1VWRXhZREp5YjdNOGRZMjlsTSt1OFp3c1QxcHJVbW00azNDYlV0RU9abEFlSzhPNDFrZ3NicVU1cjlTaGpsRU80aGdLb1UxK0lyV0dEc2tBQnozdHAvYmUyMGVxZ0pPQ0IyanR3UmZYWUM3d0ordzcyakJsR01Ralh2S3JTQkhvQTNVcm91TTk0TThaM3BWMEYyMEtvbXN3ZUUvVlhsZU1Bclg1RFN3ZFJlMXo5b1IzTWU4TnhicE04VlRGR0JxeG1HVDlTRFI4R1dmYWFtaGszZUh2d1E0a09RcmhqclB0MDF2TnFON1NocC9RSlpqQjdQeVg4ZXlRQmR4RHhEZ2o2SXBxT2lqL2RUTTRnM0hOWmJvektnIj4KICAgIDxpbnB1dCB0eXBlPSJoaWRkZW4iIGlkPSJjb250ZW50X2xhbmd1YWdlIiBuYW1lPSJjb250ZW50X2xhbmd1YWdlIiB2YWx1ZT0iZW4iPgogICAgPGlucHV0IHR5cGU9ImhpZGRlbiIgaWQ9Im1lcmNoYW50X3NjcmlwdF9kYXRhXzEiIG5hbWU9Im1lcmNoYW50X3NjcmlwdF9kYXRhXzEiIHZhbHVlPSJ7JnF1b3Q7bGFuZ3VhZ2UmcXVvdDs6JnF1b3Q7ZW4mcXVvdDssJnF1b3Q7ZGVwYXJ0dXJlRGF0ZSZxdW90OzomcXVvdDsyMDI0LTA3LTE1JnF1b3Q7fSI+CiAgICA8c2NyaXB0PmRvY3VtZW50LmdldEVsZW1lbnRCeUlkKCJGb3JtMSIpLnN1Ym1pdCgpOzwvc2NyaXB0Pgo8L2Zvcm0+CjwvYm9keT4KPC9odG1sPgo=',
      sessionId:
        '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',
      template: 'wb_newcard_poa_v4.xml',
      providerUrl: 'https://web2payuat.3cint.com',
    },
  },
};

describe('PreCheckInReviewModal', () => {
  it('should render PreCheckIn Review Modal', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    (useMutationRequest as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      data: paymentData,
    });
    const { getByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingData}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );

    const submitBtn = getByTestId('PreCheckInReviewModal-submitButton');
    expect(submitBtn).toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    const { getByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingData}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );

    const submitBtn = getByTestId('PreCheckInReviewModal-submitButton');
    expect(submitBtn).toBeInTheDocument();
    userEvent.click(submitBtn);
  });
  it('should render PreCheckIn Review Modal', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    (getFindBookingToken as jest.Mock).mockReturnValue({ bookingReference: '' });

    render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={null}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
  });
  it('should render PreCheckIn Review Modal click on typed canvas', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    const { getByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingData}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
    const typedCanvasBtn = getByTestId('precheckin.signature.type');
    userEvent.click(typedCanvasBtn);
    const submitBtn = getByTestId('PreCheckInReviewModal-submitButton');

    expect(submitBtn).toBeInTheDocument();
    userEvent.click(submitBtn);
  });
  it('should render PreCheckIn Review Modal', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    (useIPageSubmission as jest.Mock).mockImplementation(() => ({
      isPaymentComplete: true,
    }));
    render(
      <PreCheckInReviewModal
        isOpen
        onClose={jest.fn()}
        data={undefined}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
  });
  it('should render PreCheckIn Review Modal', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    (useIPageSubmission as jest.Mock).mockImplementation(() => ({
      isPaymentComplete: false,
    }));
    render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={undefined}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
  });
  it('should render PreCheckIn Review Modal with empty bookingConfirmation', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    const { getByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingData}
        bookingConfirmation={undefined}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );

    const submitBtn = getByTestId('PreCheckInReviewModal-submitButton');
    expect(submitBtn).toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal for empty data', () => {
    const { queryByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
    expect(queryByTestId('PreCheckInReviewModal-wrapper') as HTMLElement).not.toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal for no rooms', () => {
    jest.clearAllMocks();
    const { queryByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingDataWithNoRooms}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
    expect(queryByTestId('PreCheckInReviewModal-wrapper') as HTMLElement).toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal for no guests', () => {
    jest.clearAllMocks();
    const { queryByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingDataWithNoGuests}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
    expect(queryByTestId('PreCheckInReviewModal-wrapper') as HTMLElement).toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal for null lead guest', () => {
    const { queryByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingDataWithNoLeadGuests}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
    expect(queryByTestId('PreCheckInReviewModal-wrapper') as HTMLElement).toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal if nationality is empty', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: '', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: '',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    const { getByTestId } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingData}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );
    expect(getByTestId('PreCheckInReviewModal-wrapper')).toBeInTheDocument();
  });

  it('should render PreCheckIn Review Modal', () => {
    (useQueryRequest as jest.Mock).mockImplementation(() => ({
      data: {
        countries: {
          countries: [
            { countryName: 'Austria', nationality: 'Austrian', countryCode: 'AT', flagSrc: '' },
            {
              countryName: 'United Kingdom',
              nationality: 'British, UK',
              countryCode: 'GB',
              flagSrc: '',
            },
          ],
        },
      },
      isSuccess: true,
    }));
    (useMutationRequest as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      data: {},
      isError: true,
      isSuccess: true,
    });
    (useIPageSubmission as jest.Mock).mockReturnValueOnce({
      isPaymentComplete: true,
      paymentStatus: 'FAILURE',
      setIsPaymentComplete: jest.fn(),
    });
    const { getByTestId, getByText } = render(
      <PreCheckInReviewModal
        isOpen={true}
        onClose={jest.fn()}
        data={mockBookingData}
        bookingConfirmation={bookingConfirmation as any}
        handleScaSuccess={jest.fn()}
        hotelAddress="London SW1A 1AA"
      />
    );

    userEvent.click(getByText('precheckin.signature.draw'));
    const canvas = getByTestId('signature-canvas');
    fireEvent.mouseDown(canvas, { clientX: 10, clientY: 10 });
    fireEvent.mouseMove(canvas, { clientX: 20, clientY: 20 });
    fireEvent.mouseUp(canvas);

    const submitBtn = getByTestId('PreCheckInReviewModal-submitButton');
    expect(submitBtn).toBeInTheDocument();
    userEvent.click(submitBtn);
  });
});
