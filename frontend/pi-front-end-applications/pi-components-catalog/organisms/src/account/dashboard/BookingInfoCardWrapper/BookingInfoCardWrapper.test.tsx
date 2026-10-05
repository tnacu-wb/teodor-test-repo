import '@testing-library/jest-dom';
import { Area, CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';
import { useFeatureToggle, useAuth0Navigation } from '@whitbread-eos/utils';
import React from 'react';

import { render, userEvent } from '../../../utils/test-utils';
import ManageBookingCardWrapper from './BookingInfoCardWrapper';

const mockedProps = {
  basketReference: '',
  area: Area.PI,
  isAmendPage: false,
  inputValues: {},
  amendBookingStatus: CONFIRM_AMEND_STATUS.success,
  bookingReference: '',
};

const mockNavigateToLogin = jest.fn();
const mockNavigateToSignup = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({
    isLoading: false,
    isError: false,
    error: '',
  }),
  useMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  graphQLRequest: jest.fn(),
  useFeatureToggle: jest.fn(() => ({})),
  useAuth0Navigation: jest.fn(() => ({
    navigateToLogin: mockNavigateToLogin,
    navigateToSignup: mockNavigateToSignup,
  })),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    fetchQuery: jest.fn(async () => {
      return Promise.resolve({});
    }),
    prefetchQuery: jest.fn().mockResolvedValue(undefined),
    invalidateQueries: jest.fn().mockResolvedValue(undefined),
  }),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('ManageBookingCardWrapper', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (useFeatureToggle as jest.Mock).mockReturnValue({});
    (useAuth0Navigation as jest.Mock).mockReturnValue({
      navigateToLogin: mockNavigateToLogin,
      navigateToSignup: mockNavigateToSignup,
    });
  });

  it('renders ManageBookingCardWrapper with default props', () => {
    const { getByTestId } = render(<ManageBookingCardWrapper {...mockedProps} />);
    expect(getByTestId('BookingInfoCardHeader-Wrapper')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-Title')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-HotelName')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-CheckInDate')).toBeInTheDocument();
    expect(getByTestId('BookingInfoCardHeader-CheckOutDate')).toBeInTheDocument();
  });
  it('renders Sign Up and Log In buttons when area is PI and user is guest', () => {
    const amendMockedProps = { ...mockedProps, area: Area.PI };
    const { getByTestId } = render(<ManageBookingCardWrapper {...amendMockedProps} />);
    expect(getByTestId('amend-booking-confirmation-SignUpButton')).toBeInTheDocument();
    expect(getByTestId('amend-booking-confirmation-LogInLink')).toBeInTheDocument();
  });

  it('renders success notification for amending booking', () => {
    const amendMockedProps = { ...mockedProps, isAmendPage: true };
    const { getByTestId } = render(<ManageBookingCardWrapper {...amendMockedProps} />);
    expect(
      getByTestId('amend-booking-confirmation-success-notification-Alert')
    ).toBeInTheDocument();
  });

  it('renders error notification for amending booking', () => {
    const amendMockedProps = {
      ...mockedProps,
      isAmendPage: true,
      amendBookingStatus: CONFIRM_AMEND_STATUS.error,
    };
    const { getByTestId } = render(<ManageBookingCardWrapper {...amendMockedProps} />);
    expect(getByTestId('amend-booking-confirmation-error-notification-Alert')).toBeInTheDocument();
  });

  describe('login click behaviour', () => {
    it('opens the legacy Secure2 login modal when Auth0 is disabled', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({ release_pi_auth0_login: false });
      const { getByTestId, queryByTestId, findByTestId } = render(
        <ManageBookingCardWrapper {...mockedProps} />
      );

      expect(queryByTestId('Header-Auth-ModalContent')).toBeNull();

      await userEvent.click(getByTestId('amend-booking-confirmation-LogInLink'));

      expect(mockNavigateToLogin).not.toHaveBeenCalled();
      expect(await findByTestId('Header-Auth-ModalContent')).toBeInTheDocument();
    });

    it('redirects to the Auth0 login URL and never opens the Secure2 modal when Auth0 is enabled', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({ release_pi_auth0_login: true });
      const { getByTestId, queryByTestId } = render(<ManageBookingCardWrapper {...mockedProps} />);

      await userEvent.click(getByTestId('amend-booking-confirmation-LogInLink'));

      expect(mockNavigateToLogin).toHaveBeenCalledTimes(1);
      expect(queryByTestId('Header-Auth-ModalContent')).toBeNull();
    });
  });

  describe('sign up click behaviour', () => {
    beforeEach(() => {
      Object.defineProperty(window, 'location', {
        value: { href: '', hostname: 'www.premierinn.com' },
        writable: true,
      });
    });

    it('redirects to the Secure2 register page when Auth0 is disabled', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({ release_pi_auth0_login: false });
      const { getByTestId } = render(<ManageBookingCardWrapper {...mockedProps} />);

      await userEvent.click(getByTestId('amend-booking-confirmation-SignUpButton'));

      expect(mockNavigateToSignup).not.toHaveBeenCalled();
      expect(window.location.href).toContain('/account/register.html');
    });

    it('redirects to the Auth0 signup URL when Auth0 is enabled', async () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({ release_pi_auth0_login: true });
      const { getByTestId } = render(<ManageBookingCardWrapper {...mockedProps} />);

      await userEvent.click(getByTestId('amend-booking-confirmation-SignUpButton'));

      expect(mockNavigateToSignup).toHaveBeenCalledTimes(1);
      expect(window.location.href).toBe('');
    });
  });
});
