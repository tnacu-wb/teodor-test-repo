import { waitFor, renderHook } from '@testing-library/react';
import { useRouter } from 'next/router';

import * as useFeatureToggleModule from './use-feature-toggle';
import usePromotionsNotification from './use-promotions-notification';

jest.mock('./use-feature-toggle');
jest.mock('../helpers', () => ({
  getCookie: jest.fn(),
}));

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  FT_PI_PROMO_CODE_LANDING_PAGE: 'promo_feature_toggle',
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: jest.fn(),
}));

describe('usePromotionsNotification', () => {
  let originalWindow: Window & typeof globalThis;

  beforeAll(() => {
    originalWindow = { ...window };
  });

  afterAll(() => {
    global.window = originalWindow;
  });

  beforeEach(() => {
    jest.clearAllMocks();
    global.window = { ...originalWindow };
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should return false when feature toggle is disabled', async () => {
    (useRouter as jest.Mock).mockReturnValue({
      query: { PROMOID: 'ST10R' },
    });
    jest.spyOn(useFeatureToggleModule, 'default').mockReturnValue({ promo_feature_toggle: false });
    const { result } = renderHook(() => usePromotionsNotification());

    await waitFor(() => {
      expect(result.current.showPromotionsNotification).toBe(false);
    });
  });

  it('should return false when the PROMOID params is not present', async () => {
    (useRouter as jest.Mock).mockReturnValue({
      query: { PROMOID: '' },
    });
    jest.spyOn(useFeatureToggleModule, 'default').mockReturnValue({ promo_feature_toggle: true });
    const { result } = renderHook(() => usePromotionsNotification());

    await waitFor(() => {
      expect(result.current.showPromotionsNotification).toBe(false);
    });
  });

  it('should return true when all conditions are met', async () => {
    (useRouter as jest.Mock).mockReturnValue({
      query: { PROMOID: 'ST10R' },
    });
    jest.spyOn(useFeatureToggleModule, 'default').mockReturnValue({ promo_feature_toggle: true });
    const { result } = renderHook(() => usePromotionsNotification());

    await waitFor(() => {
      expect(result.current.showPromotionsNotification).toBe(true);
    });
  });
});
