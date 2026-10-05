import '@testing-library/jest-dom';
import {
  HIRoomRate,
  FT_PI_BREAKFAST_PROMO_CODE,
  PROMO_CODE_COOKIE,
  SESSION_STORAGE_PROMO_COOKIE_SET,
  PageName,
} from '@whitbread-eos/api';
import { useFeatureToggle, getCookie, setCookieWithDefaultDomain } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import React from 'react';

import { render } from '../../utils/test-utils';
import PromoCode from './PromoCode.component';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: jest.fn(),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useFeatureToggle: jest.fn(),
  getCookie: jest.fn(),
  setCookieWithDefaultDomain: jest.fn(),
}));

const PROMO_CODE = 'PROBF';

const mockTranslation = (isAvailable: boolean) => ({
  t: (key: string) => {
    switch (key) {
      case 'config.experiments.breakfastPromoCode.code':
        return PROMO_CODE;
      case 'config.experiments.breakfastPromoCode.isAvailable':
        return String(isAvailable);
      case 'config.experiments.breakfastPromoCode.available':
        return 'Enjoy a free breakfast on us when you book a stay before [date]';
      case 'config.experiments.breakfastPromoCode.notAvailable':
        return "We're sorry – this promotion is no longer available";
      case 'config.experiments.breakfastPromoCode.selectionInvalid':
        return 'Free breakfasts are not available at this hotel or for the dates you’ve selected';
      case 'config.experiments.breakfastPromoCode.promoBannerVisibility':
        // empty string -> no restriction -> visible on every page
        return '';
      default:
        return 'default';
    }
  },
});

// Same as mockTranslation, but lets a test restrict which pages the banner is
// allowed to show on, so the value returned by getPageName() actually drives
// the render outcome (rather than being computed and then ignored).
const mockTranslationWithVisibility = (isAvailable: boolean, visibilityPages: PageName[]) => ({
  t: (key: string) => {
    if (key === 'config.experiments.breakfastPromoCode.promoBannerVisibility') {
      return visibilityPages.join(',');
    }
    return mockTranslation(isAvailable).t(key);
  },
});

const mockRouter = (pathname = '/home') => ({
  push: jest.fn(),
  pathname,
  query: {},
});

beforeEach(() => {
  jest.clearAllMocks();
  sessionStorage.clear();
  (getCookie as jest.Mock).mockReturnValue('');
  (useRouter as jest.Mock).mockReturnValue(mockRouter());
});

describe('PromoCode', () => {
  it('should not display anything if feature flag is false', () => {
    (useTranslation as jest.Mock).mockReturnValue(mockTranslation(false));
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BREAKFAST_PROMO_CODE]: false,
    });

    const { queryByTestId } = render(<PromoCode />);

    expect(queryByTestId('HeaderBreakfastPromoCode')).toBeNull();
  });

  it('should not display anything if cookie is not valid', () => {
    (useTranslation as jest.Mock).mockReturnValue(mockTranslation(false));
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BREAKFAST_PROMO_CODE]: true,
    });
    (getCookie as jest.Mock).mockReturnValue('not valid');

    const { queryByTestId } = render(<PromoCode />);

    expect(queryByTestId('HeaderBreakfastPromoCode')).toBeNull();
  });

  it('should display notAvailable message if cookie is valid but promo is not available', () => {
    (useTranslation as jest.Mock).mockReturnValue(mockTranslation(false));
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BREAKFAST_PROMO_CODE]: true,
    });
    (getCookie as jest.Mock).mockReturnValue(PROMO_CODE);

    const { queryByTestId } = render(<PromoCode />);
    const banner = queryByTestId('HeaderBreakfastPromoCode');

    expect(banner).toBeTruthy();
    expect(banner?.innerHTML).toBe("We're sorry – this promotion is no longer available");
  });

  it('should display available message if cookie is valid and promo is available', () => {
    (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BREAKFAST_PROMO_CODE]: true,
    });
    (getCookie as jest.Mock).mockReturnValue(PROMO_CODE);

    const { queryByTestId } = render(<PromoCode />);
    const banner = queryByTestId('HeaderBreakfastPromoCode');

    expect(banner).toBeTruthy();
    expect(banner?.innerHTML).toBe(
      'Enjoy a free breakfast on us when you book a stay before [date]'
    );
  });

  it('should display available message if cookie is valid and promo rate is returned', () => {
    (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BREAKFAST_PROMO_CODE]: true,
    });
    (getCookie as jest.Mock).mockReturnValue(PROMO_CODE);

    const roomRates: HIRoomRate[] = [
      {
        ratePlanCode: 'PROBRKST',
        roomTypes: [],
        rateCategory: 'rateCategory',
        cellCode: null,
        promotionCode: PROMO_CODE,
      },
    ];

    const { queryByTestId } = render(<PromoCode roomRates={roomRates} />);
    const banner = queryByTestId('HeaderBreakfastPromoCode');

    expect(banner).toBeTruthy();
    expect(banner?.innerHTML).toBe(
      'Enjoy a free breakfast on us when you book a stay before [date]'
    );
  });

  it('should display not available message if cookie is valid and promo rate is not returned', () => {
    (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));
    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FT_PI_BREAKFAST_PROMO_CODE]: true,
    });
    (getCookie as jest.Mock).mockReturnValue(PROMO_CODE);

    const roomRates: HIRoomRate[] = [
      {
        ratePlanCode: 'FLEXRATE',
        roomTypes: [],
        rateCategory: 'rateCategory',
        cellCode: null,
        promotionCode: null,
      },
    ];

    const { queryByTestId } = render(<PromoCode roomRates={roomRates} />);
    const banner = queryByTestId('HeaderBreakfastPromoCode');

    expect(banner).toBeTruthy();
    expect(banner?.innerHTML).toBe(
      'Free breakfasts are not available at this hotel or for the dates you’ve selected'
    );
  });

  describe('cookie lifecycle via promoCodeFromUrl', () => {
    beforeEach(() => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_BREAKFAST_PROMO_CODE]: true,
      });
    });

    it('sets cookie and sessionStorage when promoCodeFromUrl matches configured code and isAvailable is true', () => {
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));

      render(<PromoCode promoCodeFromUrl={PROMO_CODE} />);

      expect(setCookieWithDefaultDomain).toHaveBeenCalledWith(
        PROMO_CODE_COOKIE,
        PROMO_CODE,
        undefined
      );
      expect(sessionStorage.getItem(SESSION_STORAGE_PROMO_COOKIE_SET)).toBe('true');
    });

    it('clears cookie when promoCodeFromUrl matches but isAvailable is false and session guard is set', () => {
      sessionStorage.setItem(SESSION_STORAGE_PROMO_COOKIE_SET, 'true');
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(false));

      render(<PromoCode promoCodeFromUrl={PROMO_CODE} />);

      expect(setCookieWithDefaultDomain).toHaveBeenCalledWith(PROMO_CODE_COOKIE, null, -1);
      expect(sessionStorage.getItem(SESSION_STORAGE_PROMO_COOKIE_SET)).toBeNull();
    });

    it('clears cookie when promoCodeFromUrl is invalid and cookie currently holds our code', () => {
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));
      (getCookie as jest.Mock).mockReturnValue(PROMO_CODE);

      render(<PromoCode promoCodeFromUrl="INVALID" />);

      expect(setCookieWithDefaultDomain).toHaveBeenCalledWith(PROMO_CODE_COOKIE, null, -1);
    });

    it('does not clear cookie when promoCodeFromUrl is invalid and no ownership guard is set', () => {
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));

      render(<PromoCode promoCodeFromUrl="INVALID" />);

      expect(setCookieWithDefaultDomain).not.toHaveBeenCalledWith(PROMO_CODE_COOKIE, null, -1);
    });

    it('clears cookie and sessionStorage when promoCodeFromUrl is null', () => {
      sessionStorage.setItem(SESSION_STORAGE_PROMO_COOKIE_SET, 'true');
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));

      render(<PromoCode promoCodeFromUrl={null} />);

      expect(setCookieWithDefaultDomain).toHaveBeenCalledWith(PROMO_CODE_COOKIE, null, -1);
      expect(sessionStorage.getItem(SESSION_STORAGE_PROMO_COOKIE_SET)).toBeNull();
    });

    it('does not touch cookie or sessionStorage when promoCodeFromUrl is not provided', () => {
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(true));

      render(<PromoCode />);

      expect(setCookieWithDefaultDomain).not.toHaveBeenCalled();
    });
  });

  describe('page-based banner visibility (getPageName branches)', () => {
    beforeEach(() => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_BREAKFAST_PROMO_CODE]: true,
      });
      (getCookie as jest.Mock).mockReturnValue(PROMO_CODE);
    });

    it('shows the banner on a home path when HOME is an allowed page', () => {
      (useRouter as jest.Mock).mockReturnValue(mockRouter('/home'));
      (useTranslation as jest.Mock).mockReturnValue(
        mockTranslationWithVisibility(true, [PageName.HOME])
      );

      const { queryByTestId } = render(<PromoCode />);

      expect(queryByTestId('HeaderBreakfastPromoCode')).toBeTruthy();
    });

    it('hides the banner on a home path when HOME is not an allowed page', () => {
      (useRouter as jest.Mock).mockReturnValue(mockRouter('/home'));
      (useTranslation as jest.Mock).mockReturnValue(
        mockTranslationWithVisibility(true, [PageName.SRP, PageName.HDP])
      );

      const { queryByTestId } = render(<PromoCode />);

      expect(queryByTestId('HeaderBreakfastPromoCode')).toBeNull();
    });

    it('shows the banner on a search results path when SRP is an allowed page', () => {
      (useRouter as jest.Mock).mockReturnValue(mockRouter('/hotel-search-results'));
      (useTranslation as jest.Mock).mockReturnValue(
        mockTranslationWithVisibility(true, [PageName.SRP])
      );

      const { queryByTestId } = render(<PromoCode />);

      expect(queryByTestId('HeaderBreakfastPromoCode')).toBeTruthy();
    });

    it('shows the banner on a hotels path when HDP is an allowed page', () => {
      (useRouter as jest.Mock).mockReturnValue(mockRouter('/hotels/london-city'));
      (useTranslation as jest.Mock).mockReturnValue(
        mockTranslationWithVisibility(true, [PageName.HDP])
      );

      const { queryByTestId } = render(<PromoCode />);

      expect(queryByTestId('HeaderBreakfastPromoCode')).toBeTruthy();
    });

    it('hides the banner on a path matching no known page when the allowed list is restricted', () => {
      (useRouter as jest.Mock).mockReturnValue(mockRouter('/checkout'));
      (useTranslation as jest.Mock).mockReturnValue(
        mockTranslationWithVisibility(true, [PageName.HOME])
      );

      const { queryByTestId } = render(<PromoCode />);

      expect(queryByTestId('HeaderBreakfastPromoCode')).toBeNull();
    });
  });

  describe('URL-based promo behaviour', () => {
    it('should show notAvailable banner when valid URL code but feature disabled', () => {
      (useFeatureToggle as jest.Mock).mockReturnValue({
        [FT_PI_BREAKFAST_PROMO_CODE]: true,
      });
      (useTranslation as jest.Mock).mockReturnValue(mockTranslation(false));

      const { getByTestId } = render(<PromoCode promoCodeFromUrl={PROMO_CODE} />);

      expect(getByTestId('HeaderBreakfastPromoCode')).toBeInTheDocument();
    });
  });
});
