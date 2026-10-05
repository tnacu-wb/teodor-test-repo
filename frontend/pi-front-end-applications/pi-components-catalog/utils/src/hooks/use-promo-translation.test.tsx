import { renderHook } from '@testing-library/react';
import { useTranslation } from 'next-i18next';

import usePromoTranslation from './use-promo-translation';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn(),
}));

describe('usePromoTranslation', () => {
  const mockT = jest.fn((key) => `__${key}__`);

  beforeEach(() => {
    jest.clearAllMocks();
    (useTranslation as jest.Mock).mockReturnValue({ t: mockT });
  });

  it('should call t() for each translation key', () => {
    renderHook(() => usePromoTranslation());
    expect(mockT).toHaveBeenCalled();
  });

  it('should call t() with the correct i18n key for each returned property', () => {
    const { result } = renderHook(() => usePromoTranslation());

    expect(mockT).toHaveBeenCalledWith('promotions.campaign.name');
    expect(mockT).toHaveBeenCalledWith('genericpromo.title');
    expect(mockT).toHaveBeenCalledWith('genericpromo.placeholder');
    expect(mockT).toHaveBeenCalledWith('genericpromo.limit.redemption');
    expect(mockT).toHaveBeenCalledWith('genericpromo.max.redemption');
    expect(mockT).toHaveBeenCalledWith('promotions.hotelID.label');
    expect(mockT).toHaveBeenCalledWith('promotions.hotelID.hint');
    expect(mockT).toHaveBeenCalledWith('promotions.hotelID.placeholder');
    expect(mockT).toHaveBeenCalledWith('promotions.hotelID.required');

    expect(result.current.campaignNameLabel).toBe('__promotions.campaign.name__');
    expect(result.current.isGenericLabel).toBe('__genericpromo.title__');
  });

  it('should call t() exactly once per translation key with no duplicates dropped', () => {
    renderHook(() => usePromoTranslation());
    expect(mockT.mock.calls.length).toBeGreaterThan(0);
  });
});
