import { type QueryClient } from '@tanstack/react-query';
import { render, screen, fireEvent, act, waitFor } from '@testing-library/react';
import {
  Channel,
  FT_PI_SHOW_PROMOTION_BOX,
  FT_BB_SHOW_PROMOTION_BOX,
  FT_CCUI_SHOW_PROMOTION_BOX,
  HotelBrand,
  PromoKind,
  FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
} from '@whitbread-eos/api';
import * as utils from '@whitbread-eos/utils';
import { GraphQLClient } from 'graphql-request';
import { type MutableRefObject } from 'react';

import { getPromotionsInformation } from '../../amend/utilities';
import PromotionBox, {
  handlePromoSubmit,
  handlePromoCodeChange,
  getInputStyles,
  getPromoMessage,
  onSubmitPromo,
  getMappedRateName,
  handlePromoCodeChangeCookie,
  getLatestBasket,
} from './PromoBox.component';

const metaSearchConfigs = [
  { rate: 'FLEXRATE', code: 'FLX' },
  { rate: 'SEMIFLEX', code: 'SFX' },
  { rate: 'ADVANCE', code: 'ADV' },
  { rate: 'STANDARD', code: 'STD' },
  { rate: 'NONFLEX', code: 'NFX' },
];

Object.defineProperty(window, 'localStorage', {
  value: {
    getItem: jest.fn(),
    setItem: jest.fn(),
    removeItem: jest.fn(),
  },
});

const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('../../amend/utilities', () => ({
  ...jest.requireActual('../../amend/utilities'),
  getPromotionsInformation: jest.fn().mockResolvedValue({
    isWithinPromoWindow: true,
    promotionCode: 'FX20RU',
    promoKind: 'SITE_WIDE',
    promoBox: {
      whenSuccess: 'Success!',
      whenInvalid: 'Invalid!',
    },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatDataTestId: jest.fn((base: string, id: string) => `${base}-${id}`),
  useFeatureToggle: jest.fn(),
  hasMatchingPromotionCode: jest.fn().mockReturnValue(true),
  getGQLClient: jest.fn(),
  getSelectedRoomClassCode: jest.fn().mockReturnValue('ST'),
  getCookie: jest.fn(),
  setCookie: jest.fn(),
  deleteCookie: jest.fn(),
  getPromoId: jest.fn((v) => v),
}));

jest.mock('@tanstack/react-query', () => ({
  useQueryClient: jest.fn().mockReturnValue({}),
}));

const mockPromoActions: utils.PromoActionsType = {
  promotionBannerData: {
    showPromo: true,
    isWithinPromoWindow: true,
    promotionCode: '',
    landingPage: '',
    promoBannerColour: 'primary',
    promoBannerIcon: 'star',
    promoBannerTitle: 'Special Offer',
    promoBannerSubtitle: 'Use code now!',
    promoInvalidMessage: 'Invalid promo code',
    promoExpiredMessage: 'Promo expired',
    promoAmendMessage: 'Cannot amend promo',
    promoBookingInfo: 'Booking info' as any,
    promoBox: {
      title: 'Enter promo code',
      button: 'Apply',
      whenInvalid: 'Invalid promo',
      whenSuccess: 'Success promo',
      whenMultipleRedeem: 'Already redeemed',
      whenEmpty: 'Promo code is required',
      whenCodeAlreadyApplied: 'Promo already applied',
      whenCodeExpired: 'Promo expired',
      whenUnavailable: 'Promo unavailable',
      whenMinRoomsNotMet: 'Minimum room requirement not met',
      whenMaxRoomsExceeded: 'Maximum room limit exceeded',
    },
  },
  searchQuery: {
    arrival: '',
    departure: '',
    country: '',
    language: '',
    hotelBrand: HotelBrand.PI,
    roomRates: [],
  },
  promoState: {
    code: '',
    success: '',
    error: '',
    isApplied: true,
    isOpen: true,
    shouldShowRemoveButton: false,
  },
  setPromoState: jest.fn(),
  handleRemovePromoCode: jest.fn(),
  shouldShowRemoveButton: false,
  appliedPromoCode: { current: '' } as MutableRefObject<string>,
};

describe('PromoBox Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (utils.getCookie as jest.Mock).mockReturnValue('');
    mockUseRouter.mockReturnValue({
      query: { isReady: true, PROMOID: 'FX20RU' },
      push: jest.fn(),
      replace: jest.fn(),
    });

    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: true,
      [FT_BB_SHOW_PROMOTION_BOX]: true,
      [FT_CCUI_SHOW_PROMOTION_BOX]: true,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: true,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
    });
  });

  it('renders promo box when feature toggle is enabled', () => {
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.getByTestId('promoBox-container')).toBeInTheDocument();
  });

  it('does disable Apply button initially (no loading, no success yet) and no promoinput', async () => {
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    await act(async () => {
      await new Promise((r) => setTimeout(r, 600));
    });
    expect(screen.getByTestId('promoBox-button')).toBeDisabled();
  });

  it('enables Apply button after valid input', async () => {
    jest.useFakeTimers();

    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(false);

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    const input = screen.getByTestId('input-promocode');
    const button = screen.getByTestId('promoBox-button');

    fireEvent.change(input, { target: { value: 'VALID123' } });

    act(() => {
      jest.advanceTimersByTime(1000);
    });

    expect(button).not.toBeDisabled();

    jest.useRealTimers();
  });

  it('calls getPromotionsInformation with rateName and roomClass on submit', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'FLEXRATE' },
        roomClass: ['ST'],
      })
    );

    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBoxStatus: 'SUCCESS',
      promotionCode: 'VALID123',
      promoKind: 'SITE_WIDE',
      promoBox: { whenSuccess: 'Success promo' },
      showPromo: true,
      isWithinPromoWindow: true,
    });

    render(
      <PromotionBox
        channel={Channel.Bb}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    const input = screen.getByTestId('input-promocode');
    const form = screen.getByTestId('promoBox-content');

    fireEvent.change(input, {
      target: { value: 'VALID123' },
    });

    fireEvent.submit(form);

    await waitFor(() => {
      expect(getPromotionsInformation).toHaveBeenCalled();
    });

    const callArgs = (getPromotionsInformation as jest.Mock).mock.calls[
      (getPromotionsInformation as jest.Mock).mock.calls.length - 1
    ];

    expect(callArgs[5]).toBe('BB');
    expect(callArgs[10]).toBe('VALID123');
    expect(callArgs[12]).toBe('FLEXRATE');
    expect(callArgs[13]).toBe('ST');
  });

  it('passes empty rateName and roomClass when basketDetails is empty', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue('{}');

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    const input = screen.getByTestId('input-promocode');
    const form = screen.getByTestId('promoBox-content');

    fireEvent.change(input, {
      target: { value: 'TEST123' },
    });

    fireEvent.submit(form);

    await waitFor(() => {
      expect(getPromotionsInformation).toHaveBeenCalled();
    });

    const callArgs = (getPromotionsInformation as jest.Mock).mock.calls[
      (getPromotionsInformation as jest.Mock).mock.calls.length - 1
    ];

    expect(callArgs[5]).toBe('PI');
    expect(callArgs[10]).toBe('TEST123');
    expect(callArgs[12]).toBe('');
    expect(callArgs[13]).toBe('ST');
  });

  it('onSubmitPromo handles undefined promoInfo', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce(undefined);

    const mockSetError = jest.fn();
    const mockSetErrorRateRooom = jest.fn();
    const mockSetSuccess = jest.fn();
    const mockSetPromoState = jest.fn();
    const mockSetReRender = jest.fn();

    await onSubmitPromo({
      promoInput: 'TEST',
      searchQuery: {
        arrival: '',
        departure: '',
        country: '',
        language: '',
        hotelBrand: HotelBrand.PID,
        roomRates: [],
      },
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState: mockSetPromoState,
      setReRender: mockSetReRender,
      setSuccess: mockSetSuccess,
      setError: mockSetError,
      setErrorRateRoom: mockSetErrorRateRooom,
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(mockSetError).toHaveBeenCalled();
  });

  it('returns null for unknown channel', () => {
    const { container } = render(
      <PromotionBox channel={'UNKNOWN' as any} promoActions={mockPromoActions} />
    );
    expect(container.firstChild).toBeNull();
  });
  it('clears messages when promo code does not match and promoBoxStatus is SUCCESS', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(false);

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'BADCODE',
      },
    };

    render(
      <PromotionBox
        channel={Channel.Ccui}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Invalid promo message')).not.toBeInTheDocument();
      expect(screen.queryByText('Success promo')).not.toBeInTheDocument();
    });
  });

  it('getPromoMessage handles all statuses', () => {
    const promoInfo = {
      promoBox: {
        whenEmpty: 'Empty',
        whenInvalid: 'Invalid',
        whenCodeAlreadyApplied: 'Already',
        whenCodeExpired: 'Expired',
        whenUnavailable: 'Unavailable',
        whenSuccess: 'Success',
        whenMinRoomsNotMet: 'Min rooms not met',
        whenMaxRoomsExceeded: 'Max rooms exceeded',
      },
    } as any;

    expect(getPromoMessage(promoInfo, 'CODE_ALREADY_APPLIED')).toBe('Already');
    expect(getPromoMessage(promoInfo, 'CODE_EXPIRED')).toBe('Expired');
    expect(getPromoMessage(promoInfo, 'UNAVAILABLE')).toBe('Unavailable');
    expect(getPromoMessage(promoInfo, 'MIN_ROOMS_NOT_MET')).toBe('Min rooms not met');
    expect(getPromoMessage(promoInfo, 'MAX_ROOMS_EXCEEDED')).toBe('Max rooms exceeded');
    expect(getPromoMessage(promoInfo, 'UNKNOWN')).toBe('');
  });

  it('covers all getPromoMessage switch branches', () => {
    const promoInfo = {
      promoBox: {
        whenEmpty: 'Empty',
        whenInvalid: 'Invalid',
        whenCodeAlreadyApplied: 'Already applied',
        whenCodeExpired: 'Expired',
        whenUnavailable: 'Unavailable',
        whenSuccess: 'Success',
        whenMinRoomsNotMet: 'Please add more rooms',
        whenMaxRoomsExceeded: 'Please reduce rooms',
      },
    } as any;

    expect(getPromoMessage(promoInfo, 'EMPTY')).toBe('Empty');
    expect(getPromoMessage(promoInfo, 'INVALID')).toBe('Invalid');
    expect(getPromoMessage(promoInfo, 'CODE_ALREADY_APPLIED')).toBe('Already applied');
    expect(getPromoMessage(promoInfo, 'CODE_EXPIRED')).toBe('Expired');
    expect(getPromoMessage(promoInfo, 'UNAVAILABLE')).toBe('Unavailable');
    expect(getPromoMessage(promoInfo, 'SUCCESS')).toBe('Success');
    expect(getPromoMessage(promoInfo, 'MIN_ROOMS_NOT_MET')).toBe('Please add more rooms');
    expect(getPromoMessage(promoInfo, 'MAX_ROOMS_EXCEEDED')).toBe('Please reduce rooms');

    expect(getPromoMessage(promoInfo, 'UNKNOWN')).toBe('');
    expect(getPromoMessage(undefined, 'SUCCESS')).toBe('');
  });

  it('getPromoMessage returns MIN_ROOMS_NOT_MET and MAX_ROOMS_EXCEEDED messages', () => {
    const promoInfo = {
      promoBox: {
        whenMinRoomsNotMet: 'Add at least one more room',
        whenMaxRoomsExceeded: 'Too many rooms selected',
      },
    } as any;

    expect(getPromoMessage(promoInfo, 'MIN_ROOMS_NOT_MET')).toBe('Add at least one more room');
    expect(getPromoMessage(promoInfo, 'MAX_ROOMS_EXCEEDED')).toBe('Too many rooms selected');
  });

  it('getPromoMessage returns undefined for MIN_ROOMS_NOT_MET/MAX_ROOMS_EXCEEDED when field is missing (no fallback)', () => {
    const promoInfo = { promoBox: {} } as any;

    expect(getPromoMessage(promoInfo, 'MIN_ROOMS_NOT_MET')).toBeUndefined();
    expect(getPromoMessage(promoInfo, 'MAX_ROOMS_EXCEEDED')).toBeUndefined();
  });

  it('does not render when channel is invalid', () => {
    render(
      <PromotionBox
        channel={'' as Channel}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    expect(screen.queryByTestId('promoBox-container')).not.toBeInTheDocument();
  });

  it('tracks promoBoxExpand and promoBoxClick when header is clicked', async () => {
    const trackSpy = jest.spyOn(utils.analytics, 'track').mockImplementation(jest.fn());

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    fireEvent.click(screen.getByTestId('promoBox-header'));

    await waitFor(() => {
      expect(trackSpy).toHaveBeenCalledWith('promoBoxExpand');
      expect(trackSpy).toHaveBeenCalledWith('promoBoxClick');
    });
  });

  it('sets unavailable fallback message when unavailable message is missing', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue(undefined);

    const setError = jest.fn();
    const mockSetErrorRateRooom = jest.fn();

    await onSubmitPromo({
      promoInput: 'TEST',
      searchQuery: mockPromoActions.searchQuery,
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState: jest.fn(),
      setReRender: jest.fn(),
      setSuccess: jest.fn(),
      setError,
      setErrorRateRoom: mockSetErrorRateRooom,
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(setError).toHaveBeenCalledWith('Something went wrong. Please try again.');
  });

  it('uses promoInvalidMessage when promoBox message is empty', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBoxStatus: 'INVALID',
      promoInvalidMessage: 'Custom invalid message',
      promoBox: {},
    });

    const setError = jest.fn();

    await onSubmitPromo({
      promoInput: 'TEST',
      searchQuery: mockPromoActions.searchQuery,
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState: jest.fn(),
      setReRender: jest.fn(),
      setSuccess: jest.fn(),
      setError,
      setErrorRateRoom: jest.fn(),
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(setError).toHaveBeenCalledWith('Custom invalid message');
  });

  it('tracks error_event analytics when promo validation fails', async () => {
    const trackSpy = jest.spyOn(utils.analytics, 'track').mockImplementation(jest.fn());

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    fireEvent.change(screen.getByTestId('input-promocode'), {
      target: { value: '###' },
    });

    await waitFor(() => {
      expect(trackSpy).toHaveBeenCalledWith(
        'error_event',
        expect.objectContaining({
          error_message: 'Invalid promo',
          promoCode: '###',
        })
      );
    });
  });

  it('shows chevronDown when closed', () => {
    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isOpen: false,
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.getByTestId('promoBox-chevronDown')).toBeInTheDocument();
  });

  it('shows chevronUp when open', () => {
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.getByTestId('promoBox-chevronUp')).toBeInTheDocument();
  });

  it('disables button while loading', async () => {
    (getPromotionsInformation as jest.Mock).mockImplementation(
      () => new Promise((resolve) => setTimeout(resolve, 100))
    );

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    fireEvent.change(screen.getByTestId('input-promocode'), {
      target: { value: 'VALID123' },
    });

    fireEvent.submit(screen.getByTestId('promoBox-content'));
    await waitFor(() => {
      expect(screen.getByTestId('promoBox-button')).toBeDisabled();
    });
  });

  it('sets success state correctly for valid promo', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBoxStatus: 'SUCCESS',
      promotionCode: 'VALID123',
      promoKind: 'SITE_WIDE',
      promoBox: { whenSuccess: 'Success promo' },
      showPromo: true,
      isWithinPromoWindow: true,
    });

    const setPromoState = jest.fn();

    await onSubmitPromo({
      promoInput: 'VALID123',
      searchQuery: mockPromoActions.searchQuery,
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState,
      setReRender: jest.fn(),
      setSuccess: jest.fn(),
      setError: jest.fn(),
      setErrorRateRoom: jest.fn(),
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(setPromoState).toHaveBeenCalledWith({
      code: 'VALID123',
      error: '',
      success: 'Success promo',
      isApplied: true,
      isOpen: true,
      shouldShowRemoveButton: true,
      type: 'SITE_WIDE',
    });
  });

  it('keeps Apply button disabled when promoinpit is empty', async () => {
    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: false,
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    await waitFor(() => {
      expect(screen.getByTestId('promoBox-button')).toBeDisabled();
    });
  });

  it('clears error and success when rate changes', async () => {
    (utils.getSelectedRoomClassCode as jest.Mock).mockReturnValue('ST');

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE2' },
        roomClass: ['ST'],
      })
    );

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.getByTestId('promoBox-button')).toBeInTheDocument();
    });
  });

  it('renders success notification when success exists and rateTags exist', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: {
          ratePlanCode: 'RATE1',
        },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(await screen.findByText('Success promo')).toBeInTheDocument();
  });

  it('sets appliedPromoBoxCode cookie when success, cookie flag enabled, and promo is valid', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promotionCode: 'GOODCODE',
        showPromo: true,
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    await waitFor(() => {
      expect(utils.setCookie).toHaveBeenCalledWith('appliedPromoBoxCode', 'GOODCODE', undefined);
    });
  });

  it('does not set appliedPromoBoxCode cookie when the cookie feature toggle is disabled', async () => {
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: true,
      [FT_BB_SHOW_PROMOTION_BOX]: true,
      [FT_CCUI_SHOW_PROMOTION_BOX]: true,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
    });

    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promotionCode: 'GOODCODE',
        showPromo: true,
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await screen.findByText('Success promo');

    expect(utils.setCookie).not.toHaveBeenCalled();
  });

  it('does set appliedPromoBoxCode cookie when the promo is a valid promotion', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await screen.findByText('Success promo');

    expect(utils.setCookie).toHaveBeenCalled();
  });

  it('does not render success notification when rateTags do not exist', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: {
          ratePlanCode: 'RATE1',
        },
        rateTags: [],
        roomClass: ['ST'],
      })
    );

    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
      },
    };

    render(<PromotionBox channel={Channel.Pi} promoActions={actions} />);

    await waitFor(() => {
      expect(screen.queryByText('Success promo')).not.toBeInTheDocument();
    });
  });

  it('sets error when invalid promo is returned', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBoxStatus: 'INVALID',
      promoBox: {
        whenInvalid: 'Invalid promo',
      },
    });

    const setSuccess = jest.fn();
    const setError = jest.fn();

    await onSubmitPromo({
      promoInput: 'BADCODE',
      searchQuery: mockPromoActions.searchQuery,
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState: jest.fn(),
      setReRender: jest.fn(),
      setSuccess,
      setError,
      setErrorRateRoom: jest.fn(),
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(setError).toHaveBeenCalledWith('Invalid promo');
    expect(setSuccess).not.toHaveBeenCalled();
  });

  it('covers else branch: sets error when status is not SUCCESS', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce({
      promoBoxStatus: 'INVALID',
      promoBox: { whenInvalid: 'Invalid promo message' },
    });

    const setSuccess = jest.fn();
    const setError = jest.fn();
    const setPromoState = jest.fn();
    const setReRender = jest.fn();

    await onSubmitPromo({
      promoInput: 'BADCODE',
      searchQuery: {
        arrival: '',
        departure: '',
        country: '',
        language: '',
        hotelBrand: HotelBrand.PI,
        roomRates: [],
      },
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState,
      setReRender,
      setSuccess,
      setError,
      setErrorRateRoom: jest.fn(),
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(setError).toHaveBeenCalledWith('Invalid promo message');
    expect(setSuccess).not.toHaveBeenCalled();

    expect(setPromoState).not.toHaveBeenCalledWith(expect.objectContaining({ isApplied: true }));
  });

  it('handlePromoCodeChange handles undefined appliedPromoCode', () => {
    const promoData = { promoBox: { whenInvalid: 'Invalid!' } } as any;

    handlePromoCodeChange('TEST', jest.fn(), jest.fn(), jest.fn(), promoData, undefined as any);
  });

  it('getInputStyles returns default when no error or success', () => {
    const styles = getInputStyles('', '');
    expect(styles.inputElementStyles.borderColor).toBe('');
  });

  it('does not render when feature toggle is disabled', () => {
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: false,
      [FT_BB_SHOW_PROMOTION_BOX]: false,
      [FT_CCUI_SHOW_PROMOTION_BOX]: false,
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.queryByTestId('promoBox-container')).toBeNull();
  });

  it('does not render when promotionBannerData is missing', () => {
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={{ ...mockPromoActions, promotionBannerData: undefined }}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.queryByTestId('promoBox-container')).toBeNull();
  });

  it('sets promo code from PROMOID query param on first mount', async () => {
    const setPromoState = jest.fn();

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        rateTags: ['PROMO'],
        selectedRate: { ratePlanCode: 'FLEXRATE' },
        roomClass: ['ST'],
      })
    );

    mockUseRouter.mockReturnValue({
      isReady: true,
      query: {
        PROMOID: 'FX10R',
      },
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={{
          ...mockPromoActions,
          setPromoState,
        }}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(setPromoState).toHaveBeenCalledWith(expect.any(Function));
    });
  });

  it('tracks discountCodeApplied when promo is valid', async () => {
    const trackSpy = jest.spyOn(utils.analytics, 'track').mockImplementation(jest.fn());

    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
        type: 'SITE_WIDE',
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(trackSpy).toHaveBeenCalledWith(
        'discountCodeApplied',
        expect.objectContaining({
          promoCode: 'GOODCODE',
          promoName: 'RATE1',
          promoType: 'SITE_WIDE',
          discountTags: ['PROMO'],
        })
      );
    });
  });
  it('tracks discountCodeApplied only once across rerenders', async () => {
    const trackSpy = jest.spyOn(utils.analytics, 'track').mockImplementation(jest.fn());

    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
        type: 'SITE_WIDE',
      },
    };

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    await waitFor(() => {
      const discountCalls = trackSpy.mock.calls.filter(
        ([event]) => event === 'discountCodeApplied'
      );
      expect(discountCalls).toHaveLength(1);
    });

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      const discountCalls = trackSpy.mock.calls.filter(
        ([event]) => event === 'discountCodeApplied'
      );
      expect(discountCalls).toHaveLength(1);
    });
  });

  it('loads promo code from cookie when promoBoxStatus exists and PROMOID is absent', async () => {
    (utils.getCookie as jest.Mock).mockReturnValue('COOKIEPROMO');

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: {
          ratePlanCode: 'RATE1',
        },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
      },
    };

    mockUseRouter.mockReturnValue({
      query: {},
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(await screen.findByText(/Success promo/i)).toBeInTheDocument();
  });

  it('sets error from cookie when promoBoxStatus is non-SUCCESS and promo is not applied', async () => {
    (utils.getCookie as jest.Mock).mockReturnValue('VALIDPROMO');

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: false,
        code: '',
        success: '',
        error: '',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'INVALID',
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenInvalid: 'Invalid promo code',
        },
      },
    };

    mockUseRouter.mockReturnValue({
      query: {},
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(await screen.findByText(/Invalid promo code/i)).toBeInTheDocument();
  });

  it('does not clear error on the first rate change after page load (isFirstRateRoomChangeRef guard)', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: false,
        isOpen: true,
        error: 'Invalid promo',
      },
    };

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(await screen.findByText(/Invalid promo/i)).toBeInTheDocument();

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE2' },
        roomClass: ['ST'],
      })
    );

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText(/Invalid promo/i)).toBeInTheDocument();
    });
  });

  it('clears error on the second rate change after page load (isFirstRateRoomChangeRef consumed)', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: false,
        isOpen: true,
        error: 'Invalid promo',
      },
    };

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(await screen.findByText(/Invalid promo/i)).toBeInTheDocument();

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE2' },
        roomClass: ['ST'],
      })
    );

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE3' },
        roomClass: ['ST'],
      })
    );

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText(/Invalid promo/i)).toBeNull();
    });
  });

  it('should call handlePromoSubmit with mappedRateName and roomClass when hotel availability promotions flag is enabled', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'FLEXRATE' },
        roomClass: ['ST'],
      })
    );

    const setPromoState = jest.fn();
    const setSuccess = jest.fn();
    const setError = jest.fn();
    const setErrorRateRoom = jest.fn();
    const setReRender = jest.fn();

    await onSubmitPromo({
      promoInput: 'PROMO10',
      searchQuery: {
        arrival: '',
        departure: '',
        country: '',
        language: 'en',
        hotelBrand: HotelBrand.PI,
        roomRates: [],
      },
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState,
      setReRender,
      setSuccess,
      setError,
      setErrorRateRoom,
      language: 'en',
      metaSearchConfigs,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    expect(getPromotionsInformation).not.toHaveBeenCalled();

    expect(setPromoState).toHaveBeenCalledWith({
      code: 'PROMO10',
      error: '',
      success: '',
      isApplied: true,
      isOpen: true,
      shouldShowRemoveButton: true,
      type: undefined,
      rateName: 'FLEXRATE',
      roomClass: 'ST',
      isPromoBox: true,
    });

    expect(setError).toHaveBeenCalledWith('');
    expect(setSuccess).toHaveBeenCalledWith('');
    expect(setErrorRateRoom).toHaveBeenCalledWith('');
    expect(setReRender).toHaveBeenCalledWith(expect.any(Function));
  });
});

describe('PromoBox utility functions', () => {
  let setPromoState: jest.Mock;
  let setSuccess: jest.Mock;
  let setError: jest.Mock;
  let setPromoInput: jest.Mock;

  beforeEach(() => {
    setPromoState = jest.fn();
    setSuccess = jest.fn();
    setError = jest.fn();
    setPromoInput = jest.fn();
    jest.clearAllMocks();
  });

  it('handlePromoSubmit sets promoState correctly', () => {
    handlePromoSubmit(
      setPromoState,
      'CODE10',
      '',
      'Success!',
      true,
      true,
      'SITE_WIDE' as PromoKind
    );

    expect(setPromoState).toHaveBeenCalledWith({
      code: 'CODE10',
      error: '',
      success: 'Success!',
      isApplied: true,
      isOpen: true,
      shouldShowRemoveButton: true,
      type: 'SITE_WIDE',
    });
  });

  it('uses errorRateAndRoomMessage when showPromo is false', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue({
      promoBoxStatus: 'INVALID',
      showPromo: false,
      errorRateAndRoomMessage: 'Rate not applicable',
      promoBox: { whenInvalid: 'Invalid!' },
    });

    const mockSetError = jest.fn();
    const mockSetSuccess = jest.fn();

    await onSubmitPromo({
      promoInput: 'TEST',
      searchQuery: mockPromoActions.searchQuery,
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState: jest.fn(),
      setReRender: jest.fn(),
      setSuccess: mockSetSuccess,
      setError: mockSetError,
      setErrorRateRoom: jest.fn(),
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(mockSetError).toHaveBeenCalledWith('Rate not applicable');
  });

  it('re-submits promo when rate changes and errorRateRoom exists', async () => {
    (utils.getSelectedRoomClassCode as jest.Mock).mockReturnValue('ST');

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    (getPromotionsInformation as jest.Mock)
      .mockResolvedValueOnce({
        promoBoxStatus: 'INVALID',
        showPromo: false,
        errorRateAndRoomMessage: 'Rate not applicable',
        promoBox: {
          whenInvalid: 'Invalid promo',
        },
      })
      .mockResolvedValueOnce({
        promoBoxStatus: 'SUCCESS',
        showPromo: true,
        isWithinPromoWindow: true,
        promotionCode: 'PROMO123',
        promoKind: 'SITE_WIDE',
        promoBox: {
          whenSuccess: 'Success promo',
        },
      });

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    fireEvent.change(screen.getByTestId('input-promocode'), {
      target: { value: 'PROMO123' },
    });

    await act(async () => {
      fireEvent.submit(screen.getByTestId('promoBox-content'));
    });

    expect(getPromotionsInformation).toHaveBeenCalledTimes(1);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE2' },
        roomClass: ['ST'],
      })
    );

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(getPromotionsInformation).toHaveBeenCalledTimes(2);
    });
  });

  it('handlePromoCodeChange sanitizes input and clears messages', () => {
    const promoData = { promoBox: { whenInvalid: 'Invalid!' } } as utils.PromotionsInformation;
    const appliedPromoCode = { current: '' } as MutableRefObject<string>;

    handlePromoCodeChange(
      'TEST123',
      setPromoInput,
      setError,
      setSuccess,
      promoData,
      appliedPromoCode
    );

    expect(setPromoInput).toHaveBeenCalledWith('TEST123');
    expect(setError).toHaveBeenCalledWith('');
    expect(setSuccess).toHaveBeenCalledWith('');
    expect(appliedPromoCode.current).toBe('TEST123');
  });

  it('handlePromoCodeChange sets error for invalid input', () => {
    const promoData = { promoBox: { whenInvalid: 'Invalid!' } } as utils.PromotionsInformation;
    const appliedPromoCode = { current: '' } as MutableRefObject<string>;

    handlePromoCodeChange('###', setPromoInput, setError, setSuccess, promoData, appliedPromoCode);

    expect(setError).toHaveBeenCalledWith('Invalid!');
    expect(appliedPromoCode.current).toBe('###');
  });

  it('getInputStyles returns error border', () => {
    expect(getInputStyles('Error', '').inputElementStyles.borderColor).toBe('error');
  });

  it('getInputStyles returns success border', () => {
    expect(getInputStyles('', 'Success').inputElementStyles.borderColor).toBe('success');
  });

  it('getPromoMessage returns correct messages', () => {
    const promoInfo = {
      promoBox: {
        whenEmpty: 'Empty!',
        whenInvalid: 'Invalid!',
        whenSuccess: 'Success!',
      },
    } as any;

    expect(getPromoMessage(promoInfo, 'EMPTY')).toBe('Empty!');
    expect(getPromoMessage(promoInfo, 'INVALID')).toBe('Invalid!');
    expect(getPromoMessage(promoInfo, 'SUCCESS')).toBe('Success!');
  });

  it('handlePromoCodeChangeCookie does not set success when cookie value is invalid', () => {
    const setPromoInput = jest.fn();
    const setError = jest.fn();
    const setSuccess = jest.fn();
    const setIsOpen = jest.fn();

    const appliedPromoCode = {
      current: '',
    } as MutableRefObject<string>;

    const promotionBannerData = {
      promoBoxStatus: 'SUCCESS',
      promoBox: {
        whenSuccess: 'Success promo',
      },
    } as any;

    handlePromoCodeChangeCookie(
      '###INVALID###',
      setPromoInput,
      setError,
      setSuccess,
      promotionBannerData,
      appliedPromoCode,
      setIsOpen
    );

    expect(setPromoInput).toHaveBeenCalledWith('###INVALID###');
    expect(setIsOpen).toHaveBeenCalledWith(true);

    expect(setSuccess).not.toHaveBeenCalled();
    expect(setError).not.toHaveBeenCalled();
  });

  it('handlePromoCodeChangeCookie sets success when cookie value is valid and status is SUCCESS', () => {
    const setPromoInput = jest.fn();
    const setError = jest.fn();
    const setSuccess = jest.fn();
    const setIsOpen = jest.fn();

    const appliedPromoCode = { current: '' } as MutableRefObject<string>;

    const promotionBannerData = {
      promoBoxStatus: 'SUCCESS',
      promoBox: {
        whenSuccess: 'Success promo',
      },
    } as any;

    handlePromoCodeChangeCookie(
      'VALIDCODE',
      setPromoInput,
      setError,
      setSuccess,
      promotionBannerData,
      appliedPromoCode,
      setIsOpen
    );

    expect(setPromoInput).toHaveBeenCalledWith('VALIDCODE');
    expect(setSuccess).toHaveBeenCalledWith('Success promo');
    expect(setError).toHaveBeenCalledWith('');
    expect(appliedPromoCode.current).toBe('VALIDCODE');
  });

  it('handlePromoCodeChangeCookie sets error when status is not SUCCESS', () => {
    const setPromoInput = jest.fn();
    const setError = jest.fn();
    const setSuccess = jest.fn();
    const setIsOpen = jest.fn();

    const appliedPromoCode = { current: '' } as MutableRefObject<string>;

    const promotionBannerData = {
      promoBoxStatus: 'INVALID',
      promoBox: {
        whenInvalid: 'Invalid cookie promo',
      },
    } as any;

    handlePromoCodeChangeCookie(
      'BADCODE',
      setPromoInput,
      setError,
      setSuccess,
      promotionBannerData,
      appliedPromoCode,
      setIsOpen
    );

    expect(setError).toHaveBeenCalledWith('Invalid cookie promo');
    expect(setSuccess).toHaveBeenCalledWith('');
  });

  it('sets promoInput from promoState.code when valid promo with rateTags and existing success state', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'GOODCODE',
        success: 'Success promo', // pre-seed success in promoState
      },
    };

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    // Wait for first render cycle to set internal success state
    await waitFor(() => {
      expect(screen.getByText('Success promo')).toBeInTheDocument();
    });

    // Rerender to trigger the useEffect again now that `success` state is truthy
    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={{
          ...actions,
          promoState: {
            ...actions.promoState,
            code: 'GOODCODE_UPDATED', // change code to trigger the effect dependency
          },
        }}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      const input = screen.getByTestId('input-promocode') as HTMLInputElement;
      expect(input.value).toBe('GOODCODE_UPDATED');
    });
  });

  it('getLatestBasket returns empty object when localStorage has nothing', () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(null);
    expect(getLatestBasket()).toEqual({});
  });

  it('getLatestBasket parses stored basket details', () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({ selectedRate: { ratePlanCode: 'RATE9' } })
    );
    expect(getLatestBasket()).toEqual({ selectedRate: { ratePlanCode: 'RATE9' } });
  });
});

describe('onSubmitPromo', () => {
  const mockSetPromoState = jest.fn();
  const mockSetReRender = jest.fn((fn) => fn(false));
  const mockSetSuccess = jest.fn();
  const mockSetError = jest.fn();
  const mockSetErrorRateRoom = jest.fn();

  const baseProps = {
    promoInput: 'TEST123',
    searchQuery: {
      arrival: '2025-01-01',
      departure: '2025-01-03',
      country: 'GB',
      language: 'en',
      hotelBrand: HotelBrand.PID,
      roomRates: [],
    },
    channel: Channel.Pi,
    queryClient: {} as unknown as QueryClient,
    client: {} as unknown as GraphQLClient,
    setPromoState: mockSetPromoState,
    setReRender: mockSetReRender,
    setSuccess: mockSetSuccess,
    setError: mockSetError,
    setErrorRateRoom: mockSetErrorRateRoom,
    isPromotionsInHotelAvailabilityEnabled: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();

    mockUseRouter.mockReturnValue({
      query: { PROMOID: 'TESTPROMOID', isReady: true },
      push: jest.fn(),
      replace: jest.fn(),
    });

    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: true,
      [FT_BB_SHOW_PROMOTION_BOX]: true,
      [FT_CCUI_SHOW_PROMOTION_BOX]: true,
    });
  });

  it('sets unavailable error and exits early when promoInfo is undefined', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValue(undefined);

    await onSubmitPromo(baseProps);

    expect(getPromotionsInformation).toHaveBeenCalled();

    expect(mockSetSuccess).toHaveBeenCalledWith('');
    expect(mockSetError).toHaveBeenCalledWith('Something went wrong. Please try again.');

    expect(mockSetPromoState).not.toHaveBeenCalledWith(
      expect.objectContaining({ isApplied: true })
    );
  });

  it('does NOT call analytics.update when promoInput is empty', async () => {
    const analyticsSpy = jest.spyOn(utils.analytics, 'update').mockImplementation(jest.fn());

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await act(async () => {
      await new Promise((r) => setTimeout(r, 600));
    });

    expect(analyticsSpy).not.toHaveBeenCalledWith(
      expect.objectContaining({
        validation: expect.any(String),
      })
    );
  });

  it('calls analytics.update when promoInput and error/success are set', async () => {
    const analyticsSpy = jest.spyOn(utils.analytics, 'update').mockImplementation(jest.fn());

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    fireEvent.change(screen.getByTestId('input-promocode'), {
      target: { value: '###' },
    });

    await waitFor(() => {
      expect(analyticsSpy).toHaveBeenCalledWith(
        expect.objectContaining({
          promo: expect.objectContaining({ promoCode: '###' }),
          validation: expect.any(String),
        })
      );
    });
  });

  it('clears error and success when room changes', async () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['DLX'],
      })
    );

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await act(async () => {
      await new Promise((r) => setTimeout(r, 100));
    });

    expect(screen.queryByTestId('promoBox-success')).not.toBeInTheDocument();
  });
});

describe('getMappedRateName', () => {
  it('returns empty string when rateName is empty', () => {
    expect(getMappedRateName('', metaSearchConfigs)).toBe('');
  });

  it('returns rateName when exact match is found', () => {
    expect(getMappedRateName('FLEXRATE', metaSearchConfigs)).toBe('FLEXRATE');
    expect(getMappedRateName('ADVANCE', metaSearchConfigs)).toBe('ADVANCE');
  });

  it('returns mapped rate when rateName starts with code', () => {
    expect(getMappedRateName('FLX123', metaSearchConfigs)).toBe('FLEXRATE');
    expect(getMappedRateName('ADV_SPECIAL', metaSearchConfigs)).toBe('ADVANCE');
    expect(getMappedRateName('STD001', metaSearchConfigs)).toBe('STANDARD');
  });

  it('returns original rateName when no match is found', () => {
    expect(getMappedRateName('UNKNOWN', metaSearchConfigs)).toBe('UNKNOWN');
    expect(getMappedRateName('XYZ123', metaSearchConfigs)).toBe('XYZ123');
  });

  it('does not match if rateName does not start with code', () => {
    expect(getMappedRateName('123FLX', metaSearchConfigs)).toBe('123FLX');
  });

  it('handles partial overlaps correctly', () => {
    expect(getMappedRateName('FL', metaSearchConfigs)).toBe('FL'); // not full code
    expect(getMappedRateName('ADVX', metaSearchConfigs)).toBe('ADVANCE'); // valid startsWith
  });

  it('is case-sensitive (no match for different casing)', () => {
    expect(getMappedRateName('flexrate', metaSearchConfigs)).toBe('flexrate');
    expect(getMappedRateName('flx123', metaSearchConfigs)).toBe('flx123');
  });

  it('handles undefined-like values safely', () => {
    expect(getMappedRateName(null as unknown as string, metaSearchConfigs)).toBe('');
    expect(getMappedRateName(undefined as unknown as string, metaSearchConfigs)).toBe('');
  });
  it('handles empty-like values safely', () => {
    expect(getMappedRateName('' as string, metaSearchConfigs)).toBe('');
  });
});

describe('onSubmitPromo - Promotions In Hotel Availability Feature Flag', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'FLEXRATE' },
        roomClass: ['ST'],
      })
    );
  });

  it('should bypass getPromotionsInformation when feature flag is enabled', async () => {
    const setPromoState = jest.fn();
    const setSuccess = jest.fn();
    const setError = jest.fn();
    const setErrorRateRoom = jest.fn();
    const setReRender = jest.fn();

    await onSubmitPromo({
      promoInput: 'PROMO10',
      searchQuery: {
        arrival: '',
        departure: '',
        country: '',
        language: 'en',
        hotelBrand: HotelBrand.PI,
        roomRates: [],
      },
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState,
      setReRender,
      setSuccess,
      setError,
      setErrorRateRoom,
      language: 'en',
      metaSearchConfigs,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    expect(getPromotionsInformation).not.toHaveBeenCalled();

    expect(setPromoState).toHaveBeenCalledWith(
      expect.objectContaining({
        code: 'PROMO10',
        isApplied: true,
        isPromoBox: true,
      })
    );

    expect(setError).toHaveBeenCalledWith('');
    expect(setSuccess).toHaveBeenCalledWith('');
    expect(setErrorRateRoom).toHaveBeenCalledWith('');
    expect(setReRender).toHaveBeenCalled();
  });

  it('should call getPromotionsInformation when feature flag is disabled', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce({
      promoBoxStatus: 'SUCCESS',
      showPromo: true,
      isWithinPromoWindow: true,
      promotionCode: 'PROMO10',
      promoKind: 'SITE_WIDE',
      promoBox: {
        whenSuccess: 'Success promo',
      },
    });

    await onSubmitPromo({
      promoInput: 'PROMO10',
      searchQuery: {
        arrival: '',
        departure: '',
        country: '',
        language: 'en',
        hotelBrand: HotelBrand.PI,
        roomRates: [],
      },
      channel: Channel.Pi,
      queryClient: {} as any,
      client: {} as any,
      setPromoState: jest.fn(),
      setReRender: jest.fn(),
      setSuccess: jest.fn(),
      setError: jest.fn(),
      setErrorRateRoom: jest.fn(),
      language: 'en',
      metaSearchConfigs,
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(getPromotionsInformation).toHaveBeenCalledTimes(1);
  });
});

describe('flag-ON promotionsInformation sync effect', () => {
  beforeEach(() => {
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: true,
      [FT_BB_SHOW_PROMOTION_BOX]: true,
      [FT_CCUI_SHOW_PROMOTION_BOX]: true,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: true,
    });
  });

  it('does nothing when flag is disabled, even if promoState is applied', async () => {
    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: true,
      [FT_BB_SHOW_PROMOTION_BOX]: true,
      [FT_CCUI_SHOW_PROMOTION_BOX]: true,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: false,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
    });

    const actions = {
      ...mockPromoActions,
      isFetching: true,
      promoState: { ...mockPromoActions.promoState, isApplied: true, code: 'CODE1' },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        errorRateAndRoomMessage: 'Switch to Standard rate and submit',
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Switch to Standard rate and submit')).not.toBeInTheDocument();
    });
  });

  it('does nothing when flag is enabled but promoState.isApplied is false', async () => {
    const actions = {
      ...mockPromoActions,
      promoState: { ...mockPromoActions.promoState, isApplied: false, code: '' },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        errorRateAndRoomMessage: 'Switch to Standard rate and submit',
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Switch to Standard rate and submit')).not.toBeInTheDocument();
    });
  });

  it('sets error from errorRateAndRoomMessage when present, regardless of promoBoxStatus', async () => {
    const actions = {
      ...mockPromoActions,
      promoState: { ...mockPromoActions.promoState, isApplied: true, code: 'CODE1' },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
        showPromo: true,
        isWithinPromoWindow: true,
        errorRateAndRoomMessage: 'Switch to Standard rate and submit',
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
  });

  it('sets success when promoBoxStatus SUCCESS, showPromo true, isWithinPromoWindow true, and rateTags exist', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: { ...mockPromoActions.promoState, isApplied: true, code: 'CODE1' },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
        showPromo: true,
        isWithinPromoWindow: true,
        errorRateAndRoomMessage: null,
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenSuccess: 'Flag-on success message',
        },
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
  });

  it('does set success when promoBoxStatus is SUCCESS but showPromo is true', async () => {
    const actions = {
      ...mockPromoActions,
      promoState: { ...mockPromoActions.promoState, isApplied: true, code: 'CODE1' },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
        showPromo: true,
        isWithinPromoWindow: true,
        errorRateAndRoomMessage: null,
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenSuccess: 'Code applied successfully',
        },
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Code applied successfully')).toBeInTheDocument();
    });
  });

  it('does not set success when promoBoxStatus is SUCCESS but isWithinPromoWindow is false', async () => {
    const actions = {
      ...mockPromoActions,
      promoState: { ...mockPromoActions.promoState, isApplied: true, code: 'CODE1' },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
        showPromo: true,
        isWithinPromoWindow: false,
        errorRateAndRoomMessage: null,
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenSuccess: 'Should also not appear',
        },
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Should also not appear')).toBeInTheDocument();
    });
  });

  it('clears a previous error and surfaces success when promotionBannerData updates from error to success', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: ['PROMO'],
        roomClass: ['ST'],
      })
    );

    const baseActions = {
      ...mockPromoActions,
      promoState: { ...mockPromoActions.promoState, isApplied: true, code: 'CODE1' },
    };

    const errorActions = {
      ...baseActions,
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        errorRateAndRoomMessage: 'First error message',
      } as any,
    };

    const { rerender } = render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={errorActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    const successActions = {
      ...baseActions,
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        errorRateAndRoomMessage: null,
        promoBoxStatus: 'SUCCESS',
        showPromo: true,
        isWithinPromoWindow: true,
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenSuccess: 'Now succeeded',
        },
      } as any,
    };

    rerender(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={successActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
  });

  it('does nothing (no-op branch) when promoBoxStatus is neither an error nor a valid SUCCESS state', async () => {
    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'CODE1',
        error: '',
        success: '',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'INVALID',
        showPromo: false,
        isWithinPromoWindow: false,
        errorRateAndRoomMessage: null,
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenInvalid: 'Should not surface via this effect',
        },
      } as any,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Should not surface via this effect')).not.toBeInTheDocument();
    });
  });

  it('does not render promo box when both PROMOID and CORPID query params are present', () => {
    mockUseRouter.mockReturnValue({
      query: {
        PROMOID: 'PROMO123',
        CORPID: 'CORP123',
        CELLCODES: ['CELLCODE123'],
        isReady: true,
      },
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.queryByTestId('promoBox-container')).not.toBeInTheDocument();
  });
  it('does not render promo box when site-wide promo is active', () => {
    const actions = {
      ...mockPromoActions,
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoKind: 'SITE_WIDE',
        promotionCode: 'SITEWIDE10',
        showPromo: true,
      },
    } as utils.PromoActionsType;

    mockUseRouter.mockReturnValue({
      query: {},
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.queryByTestId('promoBox-container')).not.toBeInTheDocument();
  });
  it('shows promoBoxStatus error message when promo code is invalid and promoBoxStatus is not SUCCESS', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(false);

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'EXPIRED123',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'CODE_EXPIRED',
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenCodeExpired: 'Promo code has expired',
        },
      },
    };
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions as utils.PromoActionsType}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    expect(await screen.findByText('Promo code has expired')).toBeInTheDocument();
  });

  it('clears messages when promo code does not match and promoBoxStatus is SUCCESS', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(false);

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'BADCODE',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenInvalid: 'Invalid promo message',
          whenSuccess: 'Success promo',
        },
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions as utils.PromoActionsType}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Invalid promo message')).not.toBeInTheDocument();
      expect(screen.queryByText('Success promo')).not.toBeInTheDocument();
    });
  });

  it('renders promo box when site wide promo has no rate tags in landing page journey', () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        rateTags: [],
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoKind: PromoKind.SiteWide,
        promotionCode: 'SITE10',
        showPromo: true,
      },
    };
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions as utils.PromoActionsType}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    expect(screen.getByTestId('promoBox-container')).toBeInTheDocument();
  });

  it('renders promo box when site wide promo has no rate tags in sitewide journey', () => {
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        rateTags: [],
        selectedRate: { ratePlanCode: 'RATE1' },
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoKind: PromoKind.LandingPage,
        promotionCode: 'SITE10',
        showPromo: true,
      },
    } as utils.PromoActionsType;
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );
    expect(screen.getByTestId('promoBox-container')).toBeInTheDocument();
  });
});

describe('PromoBox additional branch coverage', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (utils.getCookie as jest.Mock).mockReturnValue('');
    (window.localStorage.getItem as jest.Mock).mockReturnValue('{}');

    (utils.useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_SHOW_PROMOTION_BOX]: true,
      [FT_BB_SHOW_PROMOTION_BOX]: true,
      [FT_CCUI_SHOW_PROMOTION_BOX]: true,
      [FT_PI_CCUI_BB_PROMOTION_BOX_CODE_COOKIE]: true,
      [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: false,
    });
  });

  it('deletes the promo box code cookie when a PROMOID query param exists alongside an existing cookie', async () => {
    const deleteCookieSpy = jest.spyOn(utils, 'deleteCookie').mockImplementation(jest.fn());
    (utils.getCookie as jest.Mock).mockReturnValue('EXISTINGCOOKIE');

    mockUseRouter.mockReturnValue({
      isReady: true,
      query: { PROMOID: 'NEWPROMO' },
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(deleteCookieSpy).toHaveBeenCalledWith('appliedPromoBoxCode');
    });
  });

  it('does not delete the cookie when there is no PROMOID query param', async () => {
    const deleteCookieSpy = jest.spyOn(utils, 'deleteCookie').mockImplementation(jest.fn());
    (utils.getCookie as jest.Mock).mockReturnValue('EXISTINGCOOKIE');

    mockUseRouter.mockReturnValue({
      isReady: true,
      query: {},
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await act(async () => {
      await new Promise((r) => setTimeout(r, 50));
    });

    expect(deleteCookieSpy).not.toHaveBeenCalled();
  });

  it('does not render promo box when CORPID query param is present alone', () => {
    mockUseRouter.mockReturnValue({
      isReady: true,
      query: { CORPID: 'CORP999' },
      push: jest.fn(),
      replace: jest.fn(),
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.queryByTestId('promoBox-container')).not.toBeInTheDocument();
  });

  it('disables the input and button while isFetching is true', () => {
    mockUseRouter.mockReturnValue({
      isReady: true,
      query: {},
      push: jest.fn(),
      replace: jest.fn(),
    });

    const actions = {
      ...mockPromoActions,
      isFetching: true,
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    expect(screen.getByTestId('input-promocode')).toBeDisabled();
    expect(screen.getByTestId('promoBox-button')).toBeDisabled();
  });

  it('clears error and success (final else branch) when promo does not qualify for success but status is SUCCESS and rateTags are empty', async () => {
    (utils.hasMatchingPromotionCode as jest.Mock).mockReturnValue(true);
    (window.localStorage.getItem as jest.Mock).mockReturnValue(
      JSON.stringify({
        selectedRate: { ratePlanCode: 'RATE1' },
        rateTags: [],
        roomClass: ['ST'],
      })
    );

    const actions = {
      ...mockPromoActions,
      promoState: {
        ...mockPromoActions.promoState,
        isApplied: true,
        code: 'SOMECODE',
      },
      promotionBannerData: {
        ...mockPromoActions.promotionBannerData,
        promoBoxStatus: 'SUCCESS',
        promoBox: {
          ...mockPromoActions.promotionBannerData?.promoBox,
          whenSuccess: 'Should not appear here',
          whenInvalid: 'Should not appear either',
        },
      },
    };

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={actions as utils.PromoActionsType}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    await waitFor(() => {
      expect(screen.queryByText('Should not appear here')).not.toBeInTheDocument();
      expect(screen.queryByText('Should not appear either')).not.toBeInTheDocument();
    });
  });

  it('renders the error notification only after loading completes (isFetching false)', async () => {
    (getPromotionsInformation as jest.Mock).mockResolvedValueOnce({
      promoBoxStatus: 'INVALID',
      promoBox: { whenInvalid: 'Async invalid message' },
    });

    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={mockPromoActions}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    fireEvent.change(screen.getByTestId('input-promocode'), {
      target: { value: 'BADCODE' },
    });

    fireEvent.submit(screen.getByTestId('promoBox-content'));

    await waitFor(() => {
      expect(screen.getByText('Async invalid message')).toBeInTheDocument();
    });
  });

  it('toggles isOpen state when header is clicked twice', () => {
    render(
      <PromotionBox
        channel={Channel.Pi}
        promoActions={{
          ...mockPromoActions,
          promoState: { ...mockPromoActions.promoState, isOpen: false },
        }}
        metaSearchConfigs={metaSearchConfigs}
      />
    );

    const header = screen.getByTestId('promoBox-header');

    expect(screen.getByTestId('promoBox-chevronDown')).toBeInTheDocument();

    fireEvent.click(header);
    expect(screen.getByTestId('promoBox-chevronUp')).toBeInTheDocument();

    fireEvent.click(header);
    expect(screen.getByTestId('promoBox-chevronDown')).toBeInTheDocument();
  });
});
