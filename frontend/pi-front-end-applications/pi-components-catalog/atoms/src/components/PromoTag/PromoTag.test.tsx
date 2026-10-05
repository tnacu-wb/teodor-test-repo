import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import {
  FT_PI_PROMO_CODE_LANDING_PAGE,
  FT_CCUI_PROMO_CODE_LANDING_PAGE,
  FT_BB_PROMO_CODE_LANDING_PAGE,
  HotelBrand,
  HIRoomRate,
} from '@whitbread-eos/api';
import { useFeatureToggle, PromoActionsType } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React from 'react';

import { render } from '../../utils/test-utils';
import PromotionTag from './';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: jest.fn(),
}));

describe('PromoTag Component', () => {
  const mockReload = jest.fn();

  const getDefaultFeatureFlags = (enabled = false) => ({
    [FT_PI_PROMO_CODE_LANDING_PAGE]: enabled,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: false,
  });

  beforeEach(() => {
    jest.clearAllMocks();
    (useRouter as jest.Mock).mockReturnValue({
      reload: mockReload,
    });
  });

  it('should not render when feature flag is false', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue(getDefaultFeatureFlags(false));

    const { queryByTestId } = render(
      <PromotionTag rateDiscountTags={['10% discount']} customStyleName="" />
    );

    expect(queryByTestId('PromotionTagComponent')).toBeNull();
  });

  it('should not render when feature flag is true but no valid tags', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue(getDefaultFeatureFlags(true));

    const { queryByTestId } = render(
      <PromotionTag rateDiscountTags={['', '   ']} customStyleName="" />
    );

    expect(queryByTestId('PromotionTagComponent')).toBeNull();
  });

  it('should render promo tag when feature flag true and valid tags exist', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue(getDefaultFeatureFlags(true));

    const { getByTestId, getByText } = render(
      <PromotionTag rateDiscountTags={['10% discount']} customStyleName="rateItem" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
    expect(getByText('10% discount')).toBeInTheDocument();
  });

  it('should apply base styles correctly', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue(getDefaultFeatureFlags(true));

    const { getByTestId } = render(
      <PromotionTag rateDiscountTags={['10% discount']} customStyleName="rateItem" />
    );

    const wrapper = getByTestId('PromotionTagComponent');

    expect(wrapper).toHaveStyle('display: inline-flex');
  });

  it('should render with different customStyleName values', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue(getDefaultFeatureFlags(true));

    const { rerender, getByTestId } = render(
      <PromotionTag rateDiscountTags={['Promo']} customStyleName="rateItem" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();

    rerender(<PromotionTag rateDiscountTags={['Promo']} customStyleName="basketComponent" />);

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();

    rerender(<PromotionTag rateDiscountTags={['Promo']} customStyleName="BookingSummaryCard" />);

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
  });
});

describe('PromoTag remove functionality', () => {
  const mockReload = jest.fn();

  const appliedPromoCodeRef = { current: 'ST10R' } as React.MutableRefObject<string>;

  const promoActions: PromoActionsType = {
    shouldShowRemoveButton: true,
    appliedPromoCode: appliedPromoCodeRef,
    searchQuery: {
      arrival: '2026-02-10',
      departure: '2026-02-12',
      country: 'gb',
      language: 'en',
      hotelBrand: HotelBrand.PI,
      roomRates: [] as HIRoomRate[],
    },
    setPromoState: jest.fn(),
    promoState: {} as any,
    handleRemovePromoCode: jest.fn(),
  };

  const getFeatureFlags = () => ({
    [FT_PI_PROMO_CODE_LANDING_PAGE]: true,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: false,
  });

  beforeEach(() => {
    jest.clearAllMocks();

    (useFeatureToggle as jest.Mock).mockReturnValue(getFeatureFlags());

    (useRouter as jest.Mock).mockReturnValue({
      reload: mockReload,
    });
  });

  it('should render remove button when enabled', () => {
    const { getByTestId } = render(
      <PromotionTag
        rateDiscountTags={['Promo Applied']}
        customStyleName="rateItem"
        promoActions={promoActions}
      />
    );

    expect(getByTestId('promoDiscountTag-remove')).toBeInTheDocument();
  });

  it('should not render remove button when disabled', () => {
    const { queryByTestId } = render(
      <PromotionTag
        rateDiscountTags={['Promo Applied']}
        customStyleName="rateItem"
        promoActions={{ ...promoActions, shouldShowRemoveButton: false }}
      />
    );

    expect(queryByTestId('promoDiscountTag-remove')).toBeNull();
  });

  it('should call router.reload and show spinner on click', () => {
    const { getByTestId } = render(
      <PromotionTag
        rateDiscountTags={['Promo Applied']}
        customStyleName="rateItem"
        promoActions={promoActions}
      />
    );

    const removeBtn = getByTestId('promoDiscountTag-remove');
    fireEvent.click(removeBtn);
    expect(mockReload).toHaveBeenCalledTimes(1);
  });

  it('should prevent multiple reload calls when already removing', () => {
    const { getByTestId } = render(
      <PromotionTag
        rateDiscountTags={['Promo Applied']}
        customStyleName="rateItem"
        promoActions={promoActions}
      />
    );

    const removeBtn = getByTestId('promoDiscountTag-remove');

    fireEvent.click(removeBtn);
    fireEvent.click(removeBtn);

    expect(mockReload).toHaveBeenCalledTimes(1);
  });

  it('should prevent when rateItem is empty', () => {
    const { getByTestId } = render(
      <PromotionTag
        rateDiscountTags={['Promo Applied']}
        customStyleName=""
        promoActions={promoActions}
      />
    );

    const removeBtn = getByTestId('promoDiscountTag-remove');

    fireEvent.click(removeBtn);
    fireEvent.click(removeBtn);

    expect(mockReload).toHaveBeenCalledTimes(1);
  });
});
describe('PromoTag additional coverage', () => {
  const mockReload = jest.fn();
  const mockDeleteCookie = jest.fn();

  const appliedPromoCodeRef = { current: 'ST10R' } as React.MutableRefObject<string>;

  const promoActions: PromoActionsType = {
    shouldShowRemoveButton: true,
    appliedPromoCode: appliedPromoCodeRef,
    searchQuery: {
      arrival: '2026-02-10',
      departure: '2026-02-12',
      country: 'gb',
      language: 'en',
      hotelBrand: HotelBrand.PI,
      roomRates: [] as HIRoomRate[],
    },
    setPromoState: jest.fn(),
    promoState: {} as any,
    handleRemovePromoCode: jest.fn(),
  };

  const getFeatureFlags = () => ({
    [FT_PI_PROMO_CODE_LANDING_PAGE]: true,
    [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
    [FT_BB_PROMO_CODE_LANDING_PAGE]: false,
  });

  beforeEach(() => {
    jest.clearAllMocks();

    (useFeatureToggle as jest.Mock).mockReturnValue(getFeatureFlags());

    (useRouter as jest.Mock).mockReturnValue({
      reload: mockReload,
    });

    jest.mock('@whitbread-eos/utils', () => ({
      ...jest.requireActual('@whitbread-eos/utils'),
      useFeatureToggle: jest.fn(),
      deleteCookie: mockDeleteCookie,
    }));
  });

  it('should render with basketComponent customStyleName', () => {
    const { getByTestId } = render(
      <PromotionTag rateDiscountTags={['Promo']} customStyleName="basketComponent" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
  });

  it('should render with BookingSummaryCard customStyleName', () => {
    const { getByTestId } = render(
      <PromotionTag rateDiscountTags={['Promo']} customStyleName="BookingSummaryCard" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
  });

  it('should render with default customStyleName (unknown value)', () => {
    const { getByTestId } = render(
      <PromotionTag rateDiscountTags={['Promo']} customStyleName="unknownStyle" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
  });

  it('should render spinner after remove button is clicked', async () => {
    const { getByTestId } = render(
      <PromotionTag
        rateDiscountTags={['Promo Applied']}
        customStyleName="rateItem"
        promoActions={promoActions}
      />
    );

    const removeBtn = getByTestId('promoDiscountTag-remove');
    fireEvent.click(removeBtn);

    expect(getByTestId('promoDiscountTag-remove').querySelector('.chakra-spinner')).toBeTruthy();
  });

  it('should render multiple tags when multiple valid tags are provided', () => {
    const { getByTestId, getByText } = render(
      <PromotionTag rateDiscountTags={['10% discount', '20% off']} customStyleName="rateItem" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
    expect(getByText('10% discount')).toBeInTheDocument();
    expect(getByText('20% off')).toBeInTheDocument();
  });

  it('should not render tags when feature flags are all false', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_PROMO_CODE_LANDING_PAGE]: false,
      [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
      [FT_BB_PROMO_CODE_LANDING_PAGE]: false,
    });

    const { queryByTestId } = render(
      <PromotionTag rateDiscountTags={['10% discount']} customStyleName="rateItem" />
    );

    expect(queryByTestId('PromotionTagComponent')).toBeNull();
  });

  it('should render when FT_CCUI_PROMO_CODE_LANDING_PAGE is enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_PROMO_CODE_LANDING_PAGE]: false,
      [FT_CCUI_PROMO_CODE_LANDING_PAGE]: true,
      [FT_BB_PROMO_CODE_LANDING_PAGE]: false,
    });

    const { getByTestId } = render(
      <PromotionTag rateDiscountTags={['10% discount']} customStyleName="rateItem" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
  });

  it('should render when FT_BB_PROMO_CODE_LANDING_PAGE is enabled', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_PROMO_CODE_LANDING_PAGE]: false,
      [FT_CCUI_PROMO_CODE_LANDING_PAGE]: false,
      [FT_BB_PROMO_CODE_LANDING_PAGE]: true,
    });

    const { getByTestId } = render(
      <PromotionTag rateDiscountTags={['10% discount']} customStyleName="rateItem" />
    );

    expect(getByTestId('PromotionTagComponent')).toBeInTheDocument();
  });

  it('should render null when rateDiscountTags is undefined', () => {
    const { queryByTestId } = render(
      <PromotionTag rateDiscountTags={undefined} customStyleName="rateItem" />
    );

    expect(queryByTestId('PromotionTagComponent')).toBeNull();
  });

  it('should not render remove button when promoActions is undefined', () => {
    const { queryByTestId } = render(
      <PromotionTag rateDiscountTags={['Promo']} customStyleName="rateItem" />
    );

    expect(queryByTestId('promoDiscountTag-remove')).toBeNull();
  });
});
