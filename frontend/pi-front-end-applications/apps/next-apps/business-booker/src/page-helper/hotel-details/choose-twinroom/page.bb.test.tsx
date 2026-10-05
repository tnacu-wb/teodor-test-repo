import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import { Channel } from '@whitbread-eos/api';
import * as React from 'react';

import {
  mockBasketDetailsState,
  mockMutationRequest,
  mockEmptyBasketDetails,
  visualDisplayContext,
} from '~mocks/hotel-details';
import { render, waitFor } from '~utils/test-utils';

import ChooseTwinroomPage from './page.bb';

const mockCustomLocale = jest.fn();
const mockUseLocalStorage = jest.fn();
const mockSetBasketDetailsCookie = jest.fn();
const mockGetAuthCookie = jest.fn();
const mockFormatDataTestId = jest.fn();
const queryClient = new ReactQuery.QueryClient();

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    invalidateQueries: jest.fn(),
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  Info: () => <div />,
  Notification: () => <div />,
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  TwinroomOptions: () => <div />,
  RoomChoiceGallery: () => <div />,
  BackButton: () => <div />,
  Basket: () => <div />,
  SEO: () => <div />,
  ChooseRoomContinueBtn: () => <div data-testid="ChooseTwinroomPage-ContinueButton" />,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useLocalStorage: () => mockUseLocalStorage(),
  useCustomLocale: () => mockCustomLocale(),
  setBasketDetailsCookie: () => mockSetBasketDetailsCookie(),
  useMutationRequest: () => mockMutationRequest,
  getAuthCookie: () => mockGetAuthCookie(),
  formatDataTestId: () => mockFormatDataTestId(),
}));

const mockRouter = {
  replace: jest.fn(),
  asPath: '/en/business-booker/hotels/choose-twinroom',
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockProps = {
  channel: 'BB' as Channel,
  visualDisplayContext,
  queryClient,
  router: mockRouter as any,
};

describe('Page BB Choose Twinroom', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render Choose Twin room BB page', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { container } = render(<ChooseTwinroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should navigate to guest details page when bookReservationData is returned', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = true;
    mockMutationRequest.isError = false;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    render(<ChooseTwinroomPage {...mockProps} />);
    mockRouter.replace('/gb/en/business-booker/booking-business/guest-details?reservationId=123');
    expect(mockRouter.replace).toHaveBeenCalledWith(
      '/gb/en/business-booker/booking-business/guest-details?reservationId=123'
    );
  });

  it('should render page and ChooseRoomContinue button and click on it', async () => {
    const user = userEvent.setup();
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);

    const { getByTestId } = render(<ChooseTwinroomPage {...mockProps} />);
    const button = getByTestId('ChooseTwinroomPage-ContinueButton');
    expect(button).toBeInTheDocument();
    await user.click(button);

    await waitFor(() => {
      expect(mockRouter.replace).toHaveBeenCalledWith(
        '/gb/en/business-booker/booking-business/guest-details?reservationId=123'
      );
    });
  });

  it('should render empty Choose Twin room BB page', () => {
    mockUseLocalStorage.mockReturnValue([mockEmptyBasketDetails, jest.fn()]);
    const { queryByTestId } = render(<ChooseTwinroomPage {...mockProps} />);
    expect(queryByTestId('ChooseTwinroomPage-PageContent')).not.toBeInTheDocument();
  });
});
