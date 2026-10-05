import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Channel, Claims } from '@whitbread-eos/api';
import * as React from 'react';

import {
  mockBasketDetailsState,
  mockMutationRequest,
  mockEmptyBasketDetails,
  visualDisplayContext,
} from '~mocks/hotel-details';
import { render, fireEvent, waitFor } from '~utils/test-utils';

import ChooseTwinroomPage from './page.ccui';

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file

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

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  BOOK_MUTATION: 'mockedBookMutation',
}));

const mockRouter = {
  replace: jest.fn(),
  asPath: '/en/hotels/choose-twinroom',
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const setAnalyticsUser = jest.fn();

const mockProps = {
  channel: 'CCUI' as Channel,
  visualDisplayContext,
  queryClient,
  router: mockRouter as any,
  user: 'agent' as unknown as Claims,
  setAnalyticsUser: setAnalyticsUser,
};

describe('Page CCUI Choose Twinroom', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should render Choose Twin room CCUI page', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { container } = render(<ChooseTwinroomPage {...mockProps} />);
    expect(container).toBeInTheDocument();
  });

  it('should navigate to ancillaries page when bookReservationData is returned', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    mockMutationRequest.isSuccess = true;
    mockMutationRequest.isError = false;
    mockMutationRequest.data = { createReservation: { basketReference: '123' } };
    render(<ChooseTwinroomPage {...mockProps} />);
    mockRouter.replace('/gb/en/ancillaries?reservationId=123');
    expect(mockRouter.replace).toHaveBeenCalledWith('/gb/en/ancillaries?reservationId=123');
  });

  it('should render page and ChooseRoomContinue button and click on it', () => {
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsState, jest.fn()]);
    const { getByTestId } = render(<ChooseTwinroomPage {...mockProps} />);
    const button = getByTestId('ChooseTwinroomPage-ContinueButton');
    expect(button).toBeInTheDocument();
    fireEvent.click(button);

    waitFor(() => {
      expect(mockRouter.replace).toHaveBeenCalledWith('/gb/en/ancillaries?reservationId=123');
    });
  });

  it('should render empty Choose Twin room CCUI page', () => {
    mockUseLocalStorage.mockReturnValue([mockEmptyBasketDetails, jest.fn()]);
    const { queryByTestId } = render(<ChooseTwinroomPage {...mockProps} />);
    expect(queryByTestId('ChooseTwinroomPage-PageContent')).not.toBeInTheDocument();
  });
});
