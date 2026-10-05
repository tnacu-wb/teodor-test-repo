import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';
import { Area } from '@whitbread-eos/api';
import React from 'react';

import { render } from '../../../../utils/test-utils';
import {
  mockBookingConfirmationAuthenticatedMock,
  mockBookingConfirmationData,
} from '../mockResponse';
import HotelDetails, { Props } from './HotelDetails.container';

const props: Props = {
  bookingReference: 'AKU9491086',
  basketReference: 'AKU-ff0a17d2-8035-4755-b982-b2bdca22c7b6',
  hotelId: 'LONEUS',
  area: Area.PI,
};

const mockCustomLocale = jest.fn();
const mockAuthCookie = jest.fn();

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Notification: () => <div>Notification</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
  getAuthCookie: () => mockAuthCookie(),
  useCustomLocale: () => mockCustomLocale(),
}));

describe('<HotelDetailsContainer>', () => {
  beforeAll(() => {
    mockAuthCookie.mockReturnValue('token');

    // Mock fetchQuery on QueryClient prototype
    jest
      .spyOn(ReactQuery.QueryClient.prototype, 'fetchQuery')
      .mockImplementation(async (options: any) => {
        const queryKey = options.queryKey || options;
        const key = Array.isArray(queryKey) ? queryKey[0] : queryKey;

        switch (key) {
          case 'getBookingConfirmationAuthenticated':
            return mockBookingConfirmationAuthenticatedMock;
          case 'getBookingConfirmation':
            return mockBookingConfirmationData;
          case 'GetHotelInformation':
            return { hotelInformation: { brand: 'PI', address: ['St', 'Tower'] } };
          default:
            return {};
        }
      });

    jest
      .spyOn(ReactQuery.QueryClient.prototype, 'prefetchQuery')
      .mockResolvedValue(undefined as any);
  });

  afterAll(() => {
    jest.restoreAllMocks();
  });

  beforeEach(async () => {
    jest.clearAllMocks();

    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
  });

  it('should call getHotelIdAndDetails (success) and display container for PI', async () => {
    mockAuthCookie.mockReturnValue(null);
    const { getByTestId } = render(
      <HotelDetails {...{ ...props, hotelId: '', bookingReference: '' }} />
    );
    await waitFor(() => expect(getByTestId('HotelDetails-Container')).toBeInTheDocument());
  });

  it('should call getHotelDetails (success) and display container for CCUI', async () => {
    const { getByTestId } = render(<HotelDetails {...{ ...props, area: Area.CCUI }} />);
    await waitFor(() => expect(getByTestId('HotelDetails-Container')).toBeInTheDocument());
  });

  it('should call getHotelIdAndDetails (success) and display container for CCUI', async () => {
    const { getByTestId } = render(
      <HotelDetails {...{ ...props, area: Area.CCUI, hotelId: '' }} />
    );
    await waitFor(() => expect(getByTestId('HotelDetails-Container')).toBeInTheDocument());
  });

  it('should call getHotelIdAndDetails (fail)', async () => {
    // Temporarily override the fetchQuery mock to throw an error
    const originalFetchQuery = ReactQuery.QueryClient.prototype.fetchQuery;
    jest
      .spyOn(ReactQuery.QueryClient.prototype, 'fetchQuery')
      .mockRejectedValue(new Error('Mock error'));

    const { getByText } = render(<HotelDetails {...{ ...props, area: Area.CCUI, hotelId: '' }} />);
    await waitFor(() => {
      expect(getByText('Notification')).toBeInTheDocument();
    });

    // Restore the original mock
    ReactQuery.QueryClient.prototype.fetchQuery = originalFetchQuery;
  });
});
