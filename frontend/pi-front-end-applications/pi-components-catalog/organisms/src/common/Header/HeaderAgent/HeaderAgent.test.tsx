import { ChakraProvider } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, waitFor, screen } from '@testing-library/react';
import { MemoModalVariants } from '@whitbread-eos/api';
import { isPromoAdmin } from '@whitbread-eos/utils';

import { mockUseQueryRequest } from '../mockResponse';
import HeaderAgent, { Props } from './HeaderAgent';

const mockUseRouter = jest.fn();
const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();
const mockSetAgentMemoReservationId = jest.fn();
const mockOpenAgentMemo = jest.fn();
const mockCloseAgentMemo = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  useQueryRequest: () => mockUseQueryRequest,
  useFeatureSwitch: () => mockUseFeatureSwitch(),
  isPromoAdmin: jest.fn().mockReturnValue(true),
  useFeatureToggle: () => ({ release_ccui_unique_promo_code: true }),
  useHotelBrands: () => ({ brand: 'pi' }),
  useAgentMemo: () => ({
    openAgentMemo: mockOpenAgentMemo,
    isAgentMemoOpen: false,
    setAgentMemoReservationId: mockSetAgentMemoReservationId,
    agentMemoCount: 0,
    closeAgentMemo: mockCloseAgentMemo,
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Popover: () => <div />,
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: jest.fn(),
}));

const mockQueryClient = new QueryClient();

const mockProps: Props = {
  user: { test: true },
  roles: ['FINANCE_USER', 'CARD_HOLDER', 'ROLE-G-SC-CCUI-PROMOADMIN'],
  queryClient: mockQueryClient,
};

const Component = () => {
  return (
    <QueryClientProvider client={mockQueryClient}>
      <HeaderAgent {...mockProps} />
    </QueryClientProvider>
  );
};

describe('HeaderAgent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUseFeatureSwitch.mockReturnValue(true);
  });

  it('should render a <HeaderAgent> with default props ', function () {
    mockUseRouter.mockReturnValue({
      query: {},
      pathname: '',
      asPath: 'agent_country=gb&',
      push: jest.fn(),
    });

    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeTruthy();
  });

  it('should render a <HeaderAgent> on DE site ', function () {
    mockUseRouter.mockReturnValue({
      query: { agent_country: 'de' },
      pathname: '',
      asPath: '&agent_country=de',
      push: jest.fn(),
    });

    mockCustomLocale.mockReturnValue({
      language: 'de',
      country: 'de',
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeTruthy();
  });

  it('should render a <HeaderAgent> on EN site', function () {
    mockUseRouter.mockReturnValue({
      query: { agent_country: 'en', reservationId: '' },
      pathname: '',
      asPath: 'agent_country=en&',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );
    expect(getByTestId('common-header-wrapper')).toBeTruthy();
  });

  it('should render a <HeaderAgent> different agent country path', function () {
    mockUseRouter.mockReturnValue({
      query: { agent_country: 'en' },
      pathname: '',
      asPath: '?agent_country=en',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    expect(getByTestId('common-header-wrapper')).toBeTruthy();
  });

  it('should render a <HeaderAgent> with router null', function () {
    mockUseRouter.mockReturnValue(null);

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    expect(getByTestId('common-header-wrapper')).toBeTruthy();
  });

  it('check if Guest Account button is displayed if flag is enabled', async () => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    const { queryByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    mockUseFeatureSwitch.mockReturnValue(true);

    await waitFor(() => {
      expect(queryByTestId('navigation-link-guestAccount')).toBeInTheDocument();
    });
  });

  it('check if Guest Account is null if flag is disabled', async () => {
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });

    mockUseFeatureSwitch.mockReturnValue(false);

    const { queryByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      expect(queryByTestId('navigation-link-guestAccount')).toHaveStyle('pointer-events: none');
    });
  });
  it('should enable Promo Code menu when promo flag is ON', async () => {
    mockUseRouter.mockReturnValue({
      query: {},
      pathname: '',
      asPath: '',
      push: jest.fn(),
    });

    // Promo enabled
    mockUseFeatureSwitch.mockReturnValue(true);

    const { queryByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      const promoLink = queryByTestId('navigation-link-promoCode');
      expect(promoLink).toBeInTheDocument();
      expect(promoLink).not.toHaveStyle('pointer-events: none');
    });
  });

  it('should disable Promo Code menu when promo flag is OFF', async () => {
    mockUseRouter.mockReturnValue({
      query: {},
      pathname: '',
      asPath: '',
      push: jest.fn(),
    });

    // Promo disabled
    mockUseFeatureSwitch.mockReturnValue(false);

    const { queryByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      const promoLink = queryByTestId('navigation-link-promoCode');
      expect(promoLink).toBeInTheDocument();
    });
  });
});

describe('Promo code navigation link', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      query: {},
      pathname: '',
      asPath: 'agent_country=gb&',
      push: jest.fn(),
    });

    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
  });

  it('should show promo navigation link when user is promo admin and feature flag is enabled', async () => {
    (isPromoAdmin as jest.Mock).mockReturnValue(true);

    render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    expect(
      await waitFor(() => screen.getByTestId('navigation-link-promoCode'))
    ).toBeInTheDocument();
  });
});

describe('AgentMemo navigation link', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUseFeatureSwitch.mockReturnValue(true);
    mockSetAgentMemoReservationId.mockClear();
    mockOpenAgentMemo.mockClear();
    mockCloseAgentMemo.mockClear();
  });

  it('should enable agentMemo link when reservationId is present', async () => {
    mockUseRouter.mockReturnValue({
      query: { reservationId: 'RES123' },
      pathname: '',
      asPath: '',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      const agentMemoLink = getByTestId('navigation-link-agentMemo');
      expect(agentMemoLink).not.toHaveStyle('pointer-events: none');
    });
  });

  it('should enable agentMemo link when tempBasketReference is present in amend flow', async () => {
    mockUseRouter.mockReturnValue({
      query: { tempBasketReference: 'AQN-temp-basket-123' },
      pathname: '/amend/details',
      asPath: '',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      const agentMemoLink = getByTestId('navigation-link-agentMemo');
      expect(agentMemoLink).not.toHaveStyle('pointer-events: none');
    });
  });

  it('should disable agentMemo link when neither reservationId, tempBasketReference, nor tempBookingReference is present', async () => {
    mockUseRouter.mockReturnValue({
      query: {},
      pathname: '',
      asPath: '',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      const agentMemoLink = getByTestId('navigation-link-agentMemo');
      expect(agentMemoLink).toHaveStyle('pointer-events: none');
    });
  });

  it('should use tempBasketReference for agent memo when both bookingReference and tempBasketReference are present in amend flow', async () => {
    const tempBasketRef = 'AQN-f88ec02a-2ea6-40a3-be21-c19c3f692e66';
    const bookingRef = 'AQN9304890';

    mockUseRouter.mockReturnValue({
      query: {
        bookingReference: bookingRef,
        tempBasketReference: tempBasketRef,
      },
      pathname: '/amend/details',
      asPath: '',
      push: jest.fn(),
    });

    render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      // Verify that setAgentMemoReservationId was called with tempBasketReference, not bookingReference
      expect(mockSetAgentMemoReservationId).toHaveBeenCalledWith(tempBasketRef);
    });
  });

  it('should enable agentMemo link when tempBookingReference is present in amend confirmation flow', async () => {
    mockUseRouter.mockReturnValue({
      query: { tempBookingReference: 'AQN-temp-booking-456' },
      pathname: '/amend/booking-confirmation',
      asPath: '',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      const agentMemoLink = getByTestId('navigation-link-agentMemo');
      expect(agentMemoLink).not.toHaveStyle('pointer-events: none');
    });
  });

  it('should use tempBookingReference for agent memo when present in amend confirmation flow', async () => {
    const tempBookingRef = 'AQN-booking-123';
    const bookingRef = 'AQN9304890';

    mockUseRouter.mockReturnValue({
      query: {
        bookingReference: bookingRef,
        tempBookingReference: tempBookingRef,
      },
      pathname: '/amend/booking-confirmation',
      asPath: '',
      push: jest.fn(),
    });

    render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    await waitFor(() => {
      // Verify that setAgentMemoReservationId was called with tempBookingReference, not bookingReference
      expect(mockSetAgentMemoReservationId).toHaveBeenCalledWith(tempBookingRef);
    });
  });

  it('should call openAgentMemo and setAgentMemoReservationId when clicking enabled agentMemo link', async () => {
    const reservationId = 'RES123';
    mockUseRouter.mockReturnValue({
      query: { reservationId },
      pathname: '',
      asPath: '',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    const agentMemoLink = await waitFor(() => getByTestId('navigation-link-agentMemo'));

    // Click the link
    agentMemoLink.click();

    await waitFor(() => {
      expect(mockOpenAgentMemo).toHaveBeenCalledWith(MemoModalVariants.PAGE);
      expect(mockSetAgentMemoReservationId).toHaveBeenCalledWith(reservationId);
    });
  });

  it('should not call openAgentMemo when clicking disabled agentMemo link', async () => {
    mockUseRouter.mockReturnValue({
      query: {},
      pathname: '',
      asPath: '',
      push: jest.fn(),
    });

    const { getByTestId } = render(
      <ChakraProvider>
        <Component />
      </ChakraProvider>
    );

    const agentMemoLink = await waitFor(() => getByTestId('navigation-link-agentMemo'));

    // Clear previous calls
    mockOpenAgentMemo.mockClear();
    mockSetAgentMemoReservationId.mockClear();

    // Click the disabled link
    agentMemoLink.click();

    // Should not have been called
    expect(mockOpenAgentMemo).not.toHaveBeenCalled();
  });
});
